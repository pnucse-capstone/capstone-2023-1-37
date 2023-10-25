package com.example.p2k.vm;

import com.example.p2k._core.exception.Exception400;
import com.example.p2k._core.security.CustomUserDetails;
import com.example.p2k.course.CourseResponse;
import com.example.p2k.course.CourseService;
import com.example.p2k.user.User;
import com.example.p2k.user.UserResponse;
import com.example.p2k.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/vm")
public class VmController {

    private final VmService vmService;
    private final UserService userService;
    private final CourseService courseService;


    @GetMapping
    public String vm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        UserResponse.FindByIdDTO user = userService.findById(userDetails.getUser().getId());
        VmResponse.FindAllDTO vmList = vmService.findAllByUserId(userDetails.getUser().getId());
        model.addAttribute("user", user);
        model.addAttribute("vm", vmList);
        return "vm/vm";
    }

    // 가상환경 생성 페이지
    @GetMapping("/create")
    public String create(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User user = userDetails.getUser();
        model.addAttribute("user", user);
        model.addAttribute("vm", new VmRequest.CreateDTO());
        model.addAttribute("vmError", new VmResponse.CreateDTO("false"));
        return "vm/create";
    }

    // 가상환경 생성하기
    @PostMapping("/create")
    public String create(@AuthenticationPrincipal CustomUserDetails userDetails, @ModelAttribute("vm") VmRequest.CreateDTO requestDTO, Model model) throws Exception {
        User user = userDetails.getUser();

        try {
            vmService.create(user, requestDTO);
        } catch (Exception400 e) {
            model.addAttribute("user", user);
            model.addAttribute("vmError", new VmResponse.CreateDTO("true"));
            return "vm/create";
        }
        return "redirect:/vm";
    }

    // 가상환경 로드 페이지
    @GetMapping("/load")
    public String load(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("user", userDetails.getUser());
        model.addAttribute("loadDTO", new VmRequest.LoadDTO());
        model.addAttribute("vmError", new VmResponse.LoadDTO("false"));
        return "vm/load";
    }

    // 가상환경 수정 페이지
    @GetMapping("/update/{id}")
    public String update(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Vm vm = vmService.findById(id);

        CourseResponse.VmCoursesDTO vmCoursesDTOs = courseService.findCourses(userDetails.getUser().getId());
        VmRequest.UpdateDTO vmDTO = VmRequest.UpdateDTO.builder()
                .id(vm.getId())
                .description(vm.getDescription())
                .name(vm.getVmname())
                .password(vm.getPassword())
                .control(vm.getControl())
                .scope(vm.getScope())
                .build();

        if (vm.getCourse()==null) {
            vmDTO.setCourseId(null);
        } else {
            vmDTO.setCourseId(vm.getCourse().getId());
        }

        model.addAttribute("courseDTOs", vmCoursesDTOs);
        model.addAttribute("user", userDetails.getUser());
        model.addAttribute("vmDTO", vmDTO);

        return "vm/update";
    }

    // 가상환경 수정하기
    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute VmRequest.UpdateDTO requestDTO) throws Exception {
        vmService.update(id, requestDTO);
        return "redirect:/vm";
    }

    // 가상환경 로드하기
    @PostMapping("/load")
    public String load(@AuthenticationPrincipal CustomUserDetails userDetails, @ModelAttribute("loadDTO") VmRequest.LoadDTO requestDTO, Model model) throws Exception {
        User user = userDetails.getUser();
        try {
            vmService.load(user, requestDTO);
        } catch (Exception e) {
            model.addAttribute("user", user);
            model.addAttribute("loadDTO", new VmRequest.LoadDTO());
            model.addAttribute("vmError", new VmResponse.LoadDTO("true"));
            return "vm/load";
        }
        return "redirect:/vm";
    }

    // 가상환경 실행하기
    @PostMapping("/start/{id}")
    public String start(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        vmService.start(id);
        return "redirect:/vm";
    }

    // 가상환경 중지하기
    @PostMapping("/stop/{id}")
    public String stop(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        vmService.stop(id);
        return "redirect:/vm";
    }

    // 가상환경 저장하기
    @PostMapping("/save/{id}")
    public String save(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        vmService.save(userDetails.getUser(), id);
        return "redirect:/vm";
    }

    // 가상환경 삭제하기
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        vmService.delete(userDetails.getUser(), id);
        return "redirect:/vm";
    }

    // 가상환경 조회 페이지
    @GetMapping("/search")
    public String search(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        //UserResponse.FindByIdDTO user = userService.findById(userDetails.getUser().getId());
        VmResponse.FindAllDTO vmList = vmService.findAll();
        model.addAttribute("user", userDetails.getUser());
        model.addAttribute("vm", vmList);
        return "vm/search";
    }

    // 가상환경 조회 페이지에서 검색하기
    @PostMapping("/search")
    public String search(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam String keyword, Model model) {
        if (keyword.isEmpty()) {
            return "redirect:/vm/search";
        }

        VmResponse.FindAllDTO vmList = vmService.findAllByKeyword(keyword);
        model.addAttribute("user", userDetails.getUser());
        model.addAttribute("vm", vmList);

        return "vm/search";
    }
}