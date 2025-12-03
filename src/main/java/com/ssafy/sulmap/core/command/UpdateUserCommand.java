package com.ssafy.sulmap.core.command;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UpdateUserCommand {
    Long id;
    String name;
    String email;
    String phone;
    String address;
    Date birthday;
    String gender;
}
