package com.example.p2k.admin;

import com.example.p2k._core.exception.Exception403;
import com.example.p2k._core.exception.Exception404;
import com.example.p2k._core.web.AdminConstants;
import com.example.p2k.course.Course;
import com.example.p2k.course.CourseRepository;
import com.example.p2k.courseuser.CourseUserRepository;
import com.example.p2k.post.PostRepository;
import com.example.p2k.reply.ReplyRepository;
import com.example.p2k.user.Role;
import com.example.p2k.user.User;
import com.example.p2k.user.UserRepository;
import com.example.p2k.vm.Vm;
import com.example.p2k.vm.VmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AdminService {

    public static final int USER_PAGE_SIZE = 10;
    public static final int VM_PAGE_SIZE = 10;
    public static final int COURSE_PAGE_SIZE = 10;
    public static final int USER_PAGINATION_SIZE = 5;
    public static final int VM_PAGINATION_SIZE = 5;
    public static final int COURSE_PAGINATION_SIZE = 5;

    private final UserRepository userRepository;
    private final VmRepository vmRepository;
    private final CourseRepository courseRepository;
    private final CourseUserRepository courseUserRepository;
    private final PostRepository postRepository;
    private final ReplyRepository replyRepository;

    public AdminResponse.UsersDTO findAllUsers(int page){
        Pageable pageable = getPageable(page, "name", USER_PAGE_SIZE);
        Page<User> users = userRepository.findAll(pageable);
        List<Integer> vmNums = users.stream().map(user -> vmRepository.countByUserId(user.getId())).toList();
        List<Integer> courseNums = users.stream().map(user -> courseUserRepository.countByUserId(user.getId())).toList();
        List<Integer> postNums = users.stream().map(user -> postRepository.countByUserId(user.getId())).toList();
        List<Integer> replyNums = users.stream().map(user -> replyRepository.countByUserId(user.getId())).toList();
        return new AdminResponse.UsersDTO(users, USER_PAGINATION_SIZE, vmNums, courseNums, postNums, replyNums);
    }

    public AdminResponse.CoursesDTO findAllCourses(int page){
        Pageable pageable = getPageable(page, "name", COURSE_PAGE_SIZE);
        Page<Course> courses = courseRepository.findAll(pageable);
        List<Integer> studentNums = getStudentNums(courses);
        List<String> instructorNames = getInstructorNames(courses);
        return new AdminResponse.CoursesDTO(courses, COURSE_PAGINATION_SIZE, studentNums, instructorNames);
    }

    public AdminResponse.VmsDTO findAllVms(int page){
        Pageable pageable = getPageable(page, "vmname", VM_PAGE_SIZE);
        Page<Vm> vms = vmRepository.findAll(pageable);
        return new AdminResponse.VmsDTO(vms, VM_PAGINATION_SIZE);
    }

    @Transactional
    public void accept(Long findUserId, User user){
        checkAdminAuthorization(user);
        User findUser = userRepository.findById(findUserId).orElseThrow(
                () -> new Exception404("해당 사용자를 찾을 수 없습니다.")
        );
        findUser.updatePending(false);
    }

    @Transactional
    public void updateSetting(AdminRequest.UpdateDTO updateDTO, User user){
        checkAdminAuthorization(user);
        AdminConstants.VM_MAX_NUM = updateDTO.getVmMaxNum();
        AdminConstants.COURSE_CREATE_MAX_NUM = updateDTO.getCourseCreateMaxNum();
        AdminConstants.COURSE_APPLY_MAX_NUM = updateDTO.getCourseApplyMaxNum();
    }

    public AdminResponse.SettingDTO getConstants(){
        return new AdminResponse.SettingDTO(AdminConstants.VM_MAX_NUM, AdminConstants.COURSE_CREATE_MAX_NUM, AdminConstants.COURSE_APPLY_MAX_NUM);
    }

    private List<Integer> getStudentNums(Page<Course> courses) {
        return courses.stream()
                .mapToInt(course -> courseUserRepository.countByCourseId(course.getId()) - 1)
                .boxed()
                .toList();
    }

    private List<String> getInstructorNames(Page<Course> courses) {
        return courses.stream()
                .map(course -> userRepository.findById(course.getInstructorId()).orElseThrow(
                        () -> new Exception404("해당 사용자를 찾을 수 없습니다.")).getName())
                .toList();
    }

    private static Pageable getPageable(int page, String orderBy, int size) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.asc(orderBy));
        return PageRequest.of(page, size, Sort.by(sorts));
    }

    private static void checkAdminAuthorization(User user) {
        if(user.getRole() != Role.ROLE_ADMIN){
            throw new Exception403("관리자 권한이 없는 사용자입니다.");
        }
    }
}