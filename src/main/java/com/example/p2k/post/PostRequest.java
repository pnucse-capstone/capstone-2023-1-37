package com.example.p2k.post;

import com.example.p2k._core.validator.ValidEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public class PostRequest {

    @Getter
    @Setter
    public static class SaveDTO {

        @NotBlank(message = "제목은 필수 입력 값입니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력 값입니다.")
        private String content;

        @ValidEnum(enumClass = Scope.class, message = "공개 범위는 필수 입력 값입니다.")
        private Scope scope = Scope.PUBLIC;
    }

    @Getter
    @Setter
    public static class UpdateDTO {

        @NotBlank(message = "제목은 필수 입력 값입니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력 값입니다.")
        private String content;

        @ValidEnum(enumClass = Scope.class, message = "공개 범위는 필수 입력 값입니다.")
        private Scope scope = Scope.PUBLIC;
    }
}
