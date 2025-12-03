package com.ssafy.sulmap.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {
    String loginId;
    String password;
    String name;
    String phone;
    String email;
    Date birth;
    String address;
    String gender;
    String profile_image_url;
}
