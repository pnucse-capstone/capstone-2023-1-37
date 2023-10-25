package com.example.p2k.admin;

import com.example.p2k._core.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin")
@RequiredArgsConstructor
@Controller
public class AdminController {

    private final AdminService adminService;
    private final CloudWatchService cloudWatchService;

    @GetMapping("/k8s-resources")
    public String manageK8sResources(Model model, @AuthenticationPrincipal CustomUserDetails userDetails){
        model.addAttribute("user", userDetails.getUser());
        return "admin/grafana";
    }

    @GetMapping("/s3-resources")
    public String manageResources(Model model, @AuthenticationPrincipal CustomUserDetails userDetails){
        MetricDataResponse bucketSize = cloudWatchService.getS3BucketSize();
        MetricDataResponse numberOfObjects = cloudWatchService.getS3NumberOfObjects();
        model.addAttribute("bucketSizeTimestamp", bucketSize.getTimestamps());
        model.addAttribute("bucketSizeValue", bucketSize.getValue());
        model.addAttribute("numberOfObjectsTimestamp", numberOfObjects.getTimestamps());
        model.addAttribute("numberOfObjectsValue", numberOfObjects.getValue());
        model.addAttribute("user", userDetails.getUser());
        return "admin/resources";
    }

    @GetMapping("/vms")
    public String manageVms(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                            @AuthenticationPrincipal CustomUserDetails userDetails){
        AdminResponse.VmsDTO vms = adminService.findAllVms(page);
        model.addAttribute("vms", vms);
        model.addAttribute("user", userDetails.getUser());
        return "admin/vms";
    }

    @GetMapping("/courses")
    public String manageCourses(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                            @AuthenticationPrincipal CustomUserDetails userDetails){
        AdminResponse.CoursesDTO courses = adminService.findAllCourses(page);
        model.addAttribute("courses", courses);
        model.addAttribute("user", userDetails.getUser());
        return "admin/courses";
    }

    @GetMapping("/users")
    public String manageUsers(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                              @AuthenticationPrincipal CustomUserDetails userDetails){
        AdminResponse.UsersDTO users = adminService.findAllUsers(page);
        model.addAttribute("users", users);
        model.addAttribute("user", userDetails.getUser());
        return "admin/users";
    }

    @PostMapping("/users/{userId}/accept")
    public String acceptUser(@PathVariable Long userId, @AuthenticationPrincipal CustomUserDetails userDetails){
        adminService.accept(userId, userDetails.getUser());
        return "redirect:/admin/users";
    }

    @GetMapping("/setting")
    public String setting(Model model, @AuthenticationPrincipal CustomUserDetails userDetails){
        AdminResponse.SettingDTO constants = adminService.getConstants();
        model.addAttribute("updateDTO", constants);
        model.addAttribute("user", userDetails.getUser());
        return "admin/setting";
    }

    @PostMapping("/setting")
    public String updateSetting(@Valid @ModelAttribute AdminRequest.UpdateDTO updateDTO, Error errors,
                                @AuthenticationPrincipal CustomUserDetails userDetails){
        adminService.updateSetting(updateDTO, userDetails.getUser());
        return "redirect:/admin/setting";
    }
}