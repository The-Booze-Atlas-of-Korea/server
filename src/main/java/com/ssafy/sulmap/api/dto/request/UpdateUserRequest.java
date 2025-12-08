package com.ssafy.sulmap.api.dto.request;

import com.ssafy.sulmap.core.model.enums.UserGender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.util.Date;

public record UpdateUserRequest(
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

        @NotBlank
        @Past
        Date birthday,

        @NotBlank
        @Pattern(
                regexp = "^(M|F|OTHER|UNKNOWN)$",
                message = "성별은 M, F, OTHER, UNKNOWN 중 하나여야 합니다."
        )
        String gender,

        String profileImageUrl
) {
}
