package com.ssafy.sulmap.core.model;

import lombok.*;

import java.util.Date;

@Getter
@Builder
@ToString
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
