package com.example.p2k.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public class AdminRequest {

    @Getter
    @Setter
    public static class UpdateDTO{

        @Min(value = 0, message = "생성할 수 있는 가상 환경 최대값은 최소 0 이상이어야 합니다.")
        private int vmMaxNum;

        @Min(value = 0, message = "생성할 수 있는 강좌 최대값은 최소 0 이상이어야 합니다.")
        private int courseCreateMaxNum;

        @Min(value = 0, message = "신청할 수 있는 강좌 최대값은 최소 0 이상이어야 합니다.")
        private int courseApplyMaxNum;
    }
}
