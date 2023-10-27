package com.example.p2k.user;

import com.example.p2k._core.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    // 사용자 회원가입 페이지 GET
    @GetMapping("/join")
    public String join(Model model) {
        model.addAttribute("user", new UserRequest.JoinDTO());
        return "user/join";
    }

    // 회원가입 페이지의 데이터 POST
    @PostMapping("/join")
    public String join (@Valid @ModelAttribute("user") UserRequest.JoinDTO requestDTO, Error errors) {
        userService.save(requestDTO);
        return "redirect:/user/login";
    }

    // 로그인 페이지 GET
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new UserRequest.LoginDTO());
        return "user/login";
    }

    //회원 정보 페이지
    @GetMapping("/info")
    public String userInfoForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        UserResponse.FindByIdDTO user = userService.findById(userDetails.getUser().getId());
        model.addAttribute("user", user);
        return "user/user-info";
    }

    //회원 정보 수정
    @PostMapping("/info")
    public String update(@Valid @ModelAttribute UserRequest.UpdateDTO requestDTO, Error errors,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.update(userDetails.getUser().getId(), requestDTO);
        return "redirect:/user/info";
    }

    //비밀번호 재설정 페이지
    @GetMapping("/reset")
    public String resetPasswordForm(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        model.addAttribute("user",userDetails.getUser());
        model.addAttribute("resetDTO", new UserRequest.ResetDTO());
        return "user/reset";
    }

    //비밀번호 재설정
    @PostMapping("/reset")
    public String resetPassword(@Valid @ModelAttribute UserRequest.ResetDTO requestDTO, Error errors) {
        userService.resetPassword(requestDTO);
        return "redirect:/user/info";
    }

    //회원 탈퇴
    @GetMapping("/delete")
    public String delete(@AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.delete(userDetails.getUser().getId());
        return "redirect:/";
    }

    @GetMapping("/logout")
    public void logout() {}

    //이메일 중복 확인
    @PostMapping("/check-email/{email}")
    @ResponseBody
    public Map<String, Boolean> checkEmail(@PathVariable("email") String email) {
        Map<String, Boolean> response = new HashMap<>();
        Boolean check = userService.checkEmail(email);
        response.put("available", check);
        return response;
    }

}