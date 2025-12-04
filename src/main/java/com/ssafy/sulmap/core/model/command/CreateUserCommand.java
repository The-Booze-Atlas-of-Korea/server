package com.ssafy.sulmap.core.model.command;

import com.ssafy.sulmap.core.model.enums.UserGender;
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
    private UserGender gender;
}
