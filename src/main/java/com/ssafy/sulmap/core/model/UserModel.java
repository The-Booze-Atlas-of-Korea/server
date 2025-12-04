package com.ssafy.sulmap.core.model;

import com.ssafy.sulmap.core.model.enums.UserAuthProvider;
import com.ssafy.sulmap.core.model.enums.UserProfileVisitVisibility;
import com.ssafy.sulmap.core.model.enums.UserStatus;
import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {
    private Long id;
    private String loginId;
    private String passwordHash;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Date birthday;
    private String gender;
    private String profileImageUrl;
    private UserAuthProvider authProvider;
    private String providerId;
    private UserStatus status;
    private UserProfileVisitVisibility visitVisibilitySetting; //공개여부
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private Date lastLoginAt;
}
