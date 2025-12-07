package com.ssafy.sulmap.api.dto.request;

import com.ssafy.sulmap.core.model.enums.UserGender;

import java.util.Date;

public record SignupRequest(
        String loginId,
        String password,
        String name,
        String email,
        String phone,
        String address,
        Date birthday,
        String Gender
) {
}
