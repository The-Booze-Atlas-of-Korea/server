package com.ssafy.sulmap.core.model.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserCommand {
    private String loginId;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Date birthday;
    private String gender;
}
