package com.example.p2k.course;

import com.example.p2k._core.security.CustomUserDetails;
import com.example.p2k.user.Role;
import com.example.p2k.user.User;
import com.example.p2k.vm.VmResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/courses")
@Controller
public class CourseController {

    private final CourseService courseService;

    //나의 강좌 조회
    @GetMapping
    public String findCourses(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                              @AuthenticationPrincipal CustomUserDetails userDetails){
        model.addAttribute("courseDTOs", getMyCoursesDTO(page, userDetails));
        model.addAttribute("user", userDetails.getUser());
        return "course/course";
    }

    //강좌 신청 페이지
    @GetMapping("/application")
    public String applyForm(@RequestParam(value = "keyword", defaultValue = "") String keyword,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            Model model, @AuthenticationPrincipal CustomUserDetails userDetails){
        model.addAttribute("courseDTOs", getSearchCoursesDTO(keyword, page));
        model.addAttribute("user", userDetails.getUser());
        return "course/apply";
    }

    //강좌 신청
    @PostMapping("/application/{courseId}")
    public String apply(@PathVariable Long courseId, @AuthenticationPrincipal CustomUserDetails userDetails){
        courseService.apply(courseId, userDetails.getUser().getId());
        return "redirect:/courses/application";
    }

    //나의 가상 환경 조회
    @GetMapping("/{courseId}/my-vm")
    public String findMyVm(@PathVariable Long courseId, Model model, @AuthenticationPrincipal CustomUserDetails userDetails){
        model.addAttribute("myVms", getMyVmsDTO(courseId, userDetails.getUser()));
        model.addAttribute("courseDTO", getCourseDTO(courseId));
        model.addAttribute("user", userDetails.getUser());
        return "course/myVm";
    }

    //교육자의 가상 환경 조회
    @GetMapping("/{courseId}/instructor-vm")
    public String findInstructorVm(@PathVariable Long courseId, Model model,
                                   @AuthenticationPrincipal CustomUserDetails userDetails){
        model.addAttribute("instructorVms", getInstructorVmsDTO(courseId, userDetails.getUser()));
        model.addAttribute("courseDTO", getCourseDTO(courseId));
        model.addAttribute("user", userDetails.getUser());
        return "course/instructorVm";
    }

    //강좌 취소
    @PostMapping("/{courseId}/cancel")
    public String cancel(@PathVariable Long courseId, Model model,
                         @AuthenticationPrincipal CustomUserDetails userDetails){
        courseService.cancel(courseId, userDetails.getUser().getId());
        model.addAttribute("user", userDetails.getUser());
        return "redirect:/courses";
    }

    //강좌 생성
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute CourseRequest.SaveDTO requestDTO, Error errors,
                         @AuthenticationPrincipal CustomUserDetails userDetails){
        courseService.create(requestDTO, userDetails.getUser().getId());
        return "redirect:/courses";
    }

    //강좌 생성 폼
    @GetMapping("/create")
    public String createForm(Model model, @AuthenticationPrincipal CustomUserDetails userDetails){
        model.addAttribute("saveDTO", new CourseRequest.SaveDTO());
        model.addAttribute("user", userDetails.getUser());
        return "course/create";
    }

    //수강생 관리 페이지
    @GetMapping("/{courseId}/students")
    public String findStudents(@PathVariable Long courseId, Model model,
                               @AuthenticationPrincipal CustomUserDetails userDetails){
        model.addAttribute("studentDTOs", getFindStudentsDTO(courseId, userDetails.getUser()));
        model.addAttribute("courseDTO", getCourseDTO(courseId));
        model.addAttribute("user", userDetails.getUser());
        return "course/students";
    }

    //설정 및 관리 페이지
    @GetMapping("/{courseId}/setting")
    public String setting(@PathVariable Long courseId, Model model,
                          @AuthenticationPrincipal CustomUserDetails userDetails){
        model.addAttribute("usersDTOs", getUsersDTO(courseId));
        model.addAttribute("courseDTO", getCourseDTO(courseId));
        model.addAttribute("user", userDetails.getUser());

        if(userDetails.getUser().getRole() == Role.ROLE_STUDENT){
            return "course/setting-student";
        }else{
            model.addAttribute("unacceptedUserDTOs", getFindUnacceptedUserDTO(courseId, userDetails.getUser()));
            return "course/setting";
        }
    }

    //강좌 신청 수락
    @PostMapping("/{courseId}/application/{applicationId}/accept")
    public String accept(@PathVariable Long courseId, @PathVariable Long applicationId,
                         @AuthenticationPrincipal CustomUserDetails userDetails){
        courseService.accept(courseId, applicationId, userDetails.getUser());
        return "redirect:/courses/{courseId}/setting";
    }

    //강좌 신청 거절
    @PostMapping("/{courseId}/application/{applicationId}/reject")
    public String reject(@PathVariable Long courseId, @PathVariable Long applicationId,
                         @AuthenticationPrincipal CustomUserDetails userDetails){
        courseService.reject(courseId, applicationId, userDetails.getUser());
        return "redirect:/courses/{courseId}/setting";
    }

    //강좌 삭제
    @PostMapping("/{courseId}/delete")
    public String delete(@PathVariable Long courseId,
                         @AuthenticationPrincipal CustomUserDetails userDetails){
        courseService.delete(courseId, userDetails.getUser());
        return "redirect:/courses";
    }

    private CourseResponse.FindCoursesDTO getMyCoursesDTO(int page, CustomUserDetails userDetails) {
        return courseService.findCourses(userDetails.getUser().getId(), page);
    }

    private CourseResponse.FindCoursesDTO getSearchCoursesDTO(String keyword, int page) {
        return courseService.findSearchCourses(keyword, page);
    }

    private CourseResponse.FindById getCourseDTO(Long courseId) {
        return courseService.findCourse(courseId);
    }

    private CourseResponse.FindStudentsDTO getFindStudentsDTO(Long courseId, User user) {
        return courseService.findStudents(courseId, user);
    }

    private CourseResponse.FindUnacceptedUserDTO getFindUnacceptedUserDTO(Long courseId, User user) {
        return courseService.findApplications(courseId, user);
    }

    private VmResponse.FindAllDTO getInstructorVmsDTO(Long courseId, User user) {
        return courseService.findInstructorVm(courseId, user);
    }

    private VmResponse.FindAllDTO getMyVmsDTO(Long courseId, User user) {
        return courseService.findMyVm(courseId, user);
    }

    private CourseResponse.FindUsersDTO getUsersDTO(Long courseId) {
        return courseService.findUsers(courseId);
    }
}
