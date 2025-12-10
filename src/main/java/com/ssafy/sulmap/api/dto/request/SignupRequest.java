package com.ssafy.sulmap.api.dto.request;

import jakarta.validation.constraints.*;

import java.util.Date;

public record SignupRequest(
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
        String password,

        @NotBlank
        String name,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Pattern(
                regexp = "^01[016789]-?\\d{3,4}-?\\d{4}$",
                message = "휴대폰 번호 형식이 올바르지 않습니다."
        )
        String phone,

        @NotBlank
        String address,

        @NotNull
        @Past
        Date birthday,

        @NotBlank
        @Pattern(
                regexp = "^(M|F|OTHER|UNKNOWN)$",
                message = "성별은 M, F, OTHER, UNKNOWN 중 하나여야 합니다."
        )
        String gender
) {
}
