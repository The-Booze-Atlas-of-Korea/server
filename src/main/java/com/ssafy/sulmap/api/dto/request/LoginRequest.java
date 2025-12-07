package com.ssafy.sulmap.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank
        @Size(min = 4, max = 20)
        // 영문 소문자/숫자만 허용, 4~20자
        @Pattern(
                regexp = "^[a-z0-9]{4,20}$",
                message = "로그인 아이디는 영문 소문자와 숫자로 4~20자여야 합니다."
        )
        String loginId,

        @NotBlank
        // 영문/숫자/특수문자 포함 8~20자 (예시)
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()_+\\-=?{}\\[\\]|:;\"'<>,./]).{8,20}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함해 8~20자여야 합니다."
        )
        String password) {
}
