package com.example.p2k.reply;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public class ReplyRequest {

    @Getter
    @Setter
    public static class SaveCommentDTO {

        @NotBlank(message = "내용은 필수 입력 값입니다.")
        private String content;
    }

    @Getter
    @Setter
    public static class SaveReplyDTO {

        @NotBlank(message = "내용은 필수 입력 값입니다.")
        private String content;
    }

    @Getter
    @Setter
    public static class UpdateDTO{

        @NotBlank(message = "내용은 필수 입력 값입니다.")
        private String content;
    }
}
