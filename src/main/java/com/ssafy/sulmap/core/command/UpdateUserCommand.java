package com.ssafy.sulmap.core.command;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserCommand {
    String name;
    String email;
    String phone;
    String address;
    String birthday;
    String gender;
}
