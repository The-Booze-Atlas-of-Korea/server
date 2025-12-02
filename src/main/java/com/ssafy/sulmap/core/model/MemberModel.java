package com.ssafy.sulmap.core.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class MemberModel {
    String id;
    String password;
    String name;
    String phoneNumber;
    String email;
    Date birthday;
    String address;
    String sex;
}
