package com.ssafy.sulmap.core.command;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreatUserCommand {
    String loginId;
    String passwordHash;
    String name;
    String email;
    String phone;
    String address;
    String birthday;
    String gender;
}
