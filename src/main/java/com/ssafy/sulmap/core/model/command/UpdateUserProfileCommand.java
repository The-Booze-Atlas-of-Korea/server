package com.ssafy.sulmap.core.model.command;

import com.ssafy.sulmap.core.model.enums.UserGender;
import com.ssafy.sulmap.core.model.enums.UserProfileVisitVisibility;
import com.ssafy.sulmap.core.model.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserProfileCommand {
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Date birthday;
    private UserGender gender;
    private String profileImageUrl;
}
