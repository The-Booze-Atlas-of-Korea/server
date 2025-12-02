package com.ssafy.sulmap.core.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UserUpdateModel {
    String name;
    String phone;
    String email;
    Date birth;
    String address;
    String gender;
    String profile_image_url;
}
