package com.example.p2k.user;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UserRequest {

    @Getter
    @Setter
    public static class JoinDTO {

        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "올바른 이메일 주소를 입력해주세요.")
        private String email;

        @NotBlank(message = "이름은 필수 입력 값입니다.")
        private String name;

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,16}$", message = "비밀번호는 8~16자 영문 대 소문자, 숫자를 사용하세요.")
        private String password;

        @NotBlank(message = "비밀번호가 확인되지 않았습니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,16}$", message = "비밀번호는 8~16자 영문 대 소문자, 숫자를 사용하세요.")
        private String passwordConf;

        private Boolean emailAvailability;

        private Role role = Role.ROLE_STUDENT;
    }

    @Getter
    @Setter
    public static class LoginDTO {

        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "올바른 이메일 주소를 입력해주세요.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,16}$", message = "비밀번호는 8~16자 영문 대 소문자, 숫자를 사용하세요.")
        private String password;
    }

    @Getter
    @Setter
    public static class UpdateDTO {

        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "올바른 이메일 주소를 입력해주세요.")
        private String email;

        @NotBlank(message = "이름은 필수 입력 값입니다.")
        private String name;
    }

    @Getter
    @Setter
    public static class ResetDTO {

        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "올바른 이메일 주소를 입력해주세요.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,16}$", message = "비밀번호는 8~16자 영문 대 소문자, 숫자를 사용하세요.")
        private String password;

        @NotBlank(message = "비밀번호가 확인되지 않았습니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,16}$", message = "비밀번호는 8~16자 영문 대 소문자, 숫자를 사용하세요.")
        private String passwordConf;
    }
}
