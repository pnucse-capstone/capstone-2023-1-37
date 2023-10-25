package com.example.p2k.course;

import com.example.p2k._core.exception.Exception400;
import com.example.p2k._core.exception.Exception403;
import com.example.p2k._core.exception.Exception404;
import com.example.p2k._core.web.AdminConstants;
import com.example.p2k.courseuser.CourseUser;
import com.example.p2k.courseuser.CourseUserRepository;
import com.example.p2k.post.PostRepository;
import com.example.p2k.user.Role;
import com.example.p2k.user.User;
import com.example.p2k.user.UserRepository;
import com.example.p2k.vm.Vm;
import com.example.p2k.vm.VmRepository;
import com.example.p2k.vm.VmResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CourseService {

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGINATION_SIZE = 5;

    private final CourseRepository courseRepository;
    private final CourseUserRepository courseUserRepository;
    private final PostRepository postRepository;
    private final VmRepository vmRepository;
    private final UserRepository userRepository;

    //강좌 아이디로 강좌 조회
    public CourseResponse.FindById findCourse(Long id){
        Course course = getCourse(id);
        return new CourseResponse.FindById(course);
    }

    //사용자 아이디로 강좌 목록 조회
    public CourseResponse.VmCoursesDTO findCourses(Long userId) {
        List<Course> courses = courseUserRepository.findByUserIdAndAcceptIsTrue(userId);
        return new CourseResponse.VmCoursesDTO(courses);
    }

    //나의 강좌 조회
    public CourseResponse.FindCoursesDTO findCourses(Long userId, int page){
        Pageable pageable = getPageable(page, "id");
        Page<Course> courses = courseUserRepository.findByUserIdAndAcceptIsTrue(pageable, userId);
        return new CourseResponse.FindCoursesDTO(courses, DEFAULT_PAGINATION_SIZE);
    }

    //강좌 신청 페이지
    public CourseResponse.FindCoursesDTO findSearchCourses(String keyword, int page){
        Pageable pageable = getPageable(page, "id");
        Page<Course> courses = courseRepository.findByNameContainingIgnoreCase(pageable, keyword);
        return new CourseResponse.FindCoursesDTO(courses, DEFAULT_PAGINATION_SIZE);
    }

    //강좌 신청
    @Transactional
    public void apply(Long courseId, Long userId){
        User user = getUser(userId);
        checkStudentAuthorization(user);
        checkMaxCourseApplication(userId);

        courseUserRepository.findByCourseIdAndUserId(courseId, userId)
                .ifPresent(courseUser -> {
                    throw new Exception400("이미 신청한 강좌입니다.");
                });

        Course course = getCourse(courseId);
        CourseUser courseUser = CourseUser.builder().course(course).user(user).accept(false).build();
        courseUserRepository.save(courseUser);
    }

    //나의 가상 환경 조회
    public VmResponse.FindAllDTO findMyVm(Long courseId, User user){
        List<Vm> vms = vmRepository.findByUserIdAndCourseId(user.getId(), courseId);
        return new VmResponse.FindAllDTO(vms);
    }

    //교육자의 가상 환경 조회
    public VmResponse.FindAllDTO findInstructorVm(Long courseId, User user){
        checkStudentAuthorization(user);

        Long instructorId = getCourse(courseId).getInstructorId();
        validateInstructorExistence(instructorId);

        List<Vm> vms = vmRepository.findByUserIdAndCourseIdAndScopeIsTrue(instructorId, courseId);
        return new VmResponse.FindAllDTO(vms);
    }

    //강좌 취소
    @Transactional
    public void cancel(Long courseId, Long userId){
        User user = getUser(userId);
        checkStudentAuthorization(user);
        CourseUser courseUser = getCourseUser(courseId, userId);
        courseUserRepository.deleteById(courseUser.getId());
    }

    //강좌 생성
    @Transactional
    public void create(CourseRequest.SaveDTO requestDTO, Long userId){
        User user = getUser(userId);
        checkInstructorAuthorization(user);
        checkMaxCourseCreation(userId);

        Course course = Course.builder()
                .name(requestDTO.getName())
                .description(requestDTO.getDescription())
                .instructorId(userId)
                .build();
        courseRepository.save(course);

        CourseUser courseUser = CourseUser.builder()
                .course(course)
                .user(user)
                .accept(true)
                .build();
        courseUserRepository.save(courseUser);
    }

    //수강생 목록
    public CourseResponse.FindStudentsDTO findStudents(Long courseId, User user){
        checkInstructorAuthorization(user);

        List<User> users = courseUserRepository.findByCourseIdAndAcceptIsTrueAndUserIdNot(courseId, user.getId());
        Map<Long, List<Vm>> vmMap = new HashMap<>();
        users.forEach(u -> {
            List<Vm> vms = vmRepository.findByUserIdAndCourseIdAndScopeIsTrue(u.getId(), courseId);
            vmMap.put(u.getId(), vms);
        });

        return new CourseResponse.FindStudentsDTO(users, vmMap);
    }

    public CourseResponse.FindUsersDTO findUsers(Long courseId){
        Course course = getCourse(courseId);
        List<User> users = courseUserRepository.findByCourseIdAndAcceptIsTrueAndUserIdNot(courseId, course.getInstructorId());
        User instructor = getUser(course.getInstructorId());
        users.add(instructor);
        return new CourseResponse.FindUsersDTO(users);
    }

    //강좌 신청 대기 수강생 목록
    public CourseResponse.FindUnacceptedUserDTO findApplications(Long courseId, User user){
        checkInstructorAuthorization(user);
        List<User> users = courseUserRepository.findByCourseIdAndAcceptIsFalse(courseId);
        return new CourseResponse.FindUnacceptedUserDTO(users);
    }

    //강좌 신청 수락
    @Transactional
    public void accept(Long courseId, Long applicationId, User user){
        checkInstructorAuthorization(user);
        Course course = getCourse(courseId);
        CourseUser courseUser = getCourseUser(course.getId(), applicationId);
        courseUser.updateAccept(true);
    }

    //강좌 신청 거절
    @Transactional
    public void reject(Long courseId, Long applicationId, User user){
        checkInstructorAuthorization(user);
        Course course = getCourse(courseId);
        CourseUser courseUser = getCourseUser(course.getId(), applicationId);
        courseUserRepository.deleteById(courseUser.getId());
    }

    //강좌 삭제
    @Transactional
    public void delete(Long courseId, User user){
        checkInstructorAuthorization(user);
        Course course = getCourse(courseId);

        courseUserRepository.deleteByCourseId(course.getId());
        postRepository.deleteByCourseId(course.getId());
        vmRepository.findByCourseId(courseId).stream()
                .filter(vm -> vm.getCourse().getId().equals(courseId))
                .forEach(Vm::removeCourse);
        courseRepository.deleteById(course.getId());
    }

    private CourseUser getCourseUser(Long courseId, Long userId) {
        return courseUserRepository.findByCourseIdAndUserId(courseId, userId).orElseThrow(
                () -> new Exception404("해당 사용자-강좌를 찾을 수 없습니다.")
        );
    }

    private Course getCourse(Long courseId) {
        return courseRepository.findById(courseId).orElseThrow(
                () -> new Exception404("해당 강좌를 찾을 수 없습니다.")
        );
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new Exception404("해당 사용자를 찾을 수 없습니다.")
        );
    }

    private void checkMaxCourseCreation(Long userId) {
        List<Course> courses = courseRepository.findByInstructorId(userId);
        if(courses.size() >= AdminConstants.COURSE_CREATE_MAX_NUM){
            throw new Exception400("강좌는 최대 " + AdminConstants.COURSE_CREATE_MAX_NUM + "개까지 생성할 수 있습니다.");
        }
    }

    private void checkMaxCourseApplication(Long userId) {
        List<Course> courses = courseUserRepository.findByUserIdAndAcceptIsTrue(userId);
        if(courses.size() >= AdminConstants.COURSE_APPLY_MAX_NUM){
            throw new Exception400("강좌는 최대 " + AdminConstants.COURSE_APPLY_MAX_NUM + "개까지 신청할 수 있습니다.");
        }
    }

    private void validateInstructorExistence(Long instructorId) {
        if(instructorId == null){
            throw new Exception400("해당 강좌의 교육자가 존재하지 않습니다.");
        }
        getUser(instructorId);
    }

    private static void checkInstructorAuthorization(User user) {
        if(user.getPending() || !(user.getRole() == Role.ROLE_INSTRUCTOR || user.getRole() == Role.ROLE_ADMIN)){
            throw new Exception403("교육자 권한이 없는 사용자입니다.");
        }
    }

    private static void checkStudentAuthorization(User user) {
        if(!(user.getRole() == Role.ROLE_STUDENT || user.getRole() == Role.ROLE_ADMIN)){
            throw new Exception403("학생 권한이 없는 사용자입니다.");
        }
    }

    private static Pageable getPageable(int page, String orderBy) {
        List<Sort.Order> sorts = Collections.singletonList(Sort.Order.desc(orderBy));
        return PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.by(sorts));
    }
}