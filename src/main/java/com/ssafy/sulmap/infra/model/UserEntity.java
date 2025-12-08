package com.ssafy.sulmap.infra.model;

import java.util.Date;

import com.ssafy.sulmap.core.model.UserModel;
import com.ssafy.sulmap.core.model.enums.UserAuthProvider;
import com.ssafy.sulmap.core.model.enums.UserGender;
import com.ssafy.sulmap.core.model.enums.UserProfileVisitVisibility;
import com.ssafy.sulmap.core.model.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    private Long id;
    private String loginId;
    private String passwordHash;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Date birthDate;
    private String gender;
    private String profileImageUrl;
    private String authProvider;
    private String providerId;
    private String status;
    private String visitVisibilitySetting; //공개여부
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private Date lastLoginAt;


    public static UserEntity fromUserModel(UserModel userModel) {
        var builder = UserEntity.builder()
                .id(userModel.getId())
                .loginId(userModel.getLoginId())
                .passwordHash(userModel.getPasswordHash())
                .name(userModel.getName())
                .email(userModel.getEmail())
                .phone(userModel.getPhone())
                .address(userModel.getAddress())
                .birthDate(userModel.getBirthday())
                .gender(userModel.getGender().toString())
                .profileImageUrl(userModel.getProfileImageUrl())
                .authProvider(userModel.getAuthProvider()==null ? UserAuthProvider.LOCAL.toString() : userModel.getAuthProvider().toString())
                .providerId(userModel.getProviderId())
                .status(userModel.getStatus() == null ? UserStatus.ACTIVE.toString() : userModel.getStatus().toString())
                .visitVisibilitySetting(userModel.getVisitVisibilitySetting() == null ? UserProfileVisitVisibility.PUBLIC.toString() : userModel.getVisitVisibilitySetting().toString())
                .createdAt(userModel.getCreatedAt())
                .updatedAt(userModel.getUpdatedAt())
                .deletedAt(userModel.getDeletedAt())
                .lastLoginAt(userModel.getLastLoginAt());
        return builder.build();
    }

    public UserModel toUserModel() {
        return UserModel.builder()
                .id(id)
                .loginId(loginId)
                .passwordHash(passwordHash)
                .name(name)
                .email(email)
                .phone(phone)
                .address(address)
                .birthday(birthDate)
                .gender(UserGender.fromString(gender))
                .profileImageUrl(profileImageUrl)
                .authProvider(UserAuthProvider.fromValue(authProvider))
                .providerId(providerId)
                .status(UserStatus.fromString(status))
                .visitVisibilitySetting(UserProfileVisitVisibility.fromString(visitVisibilitySetting))
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .lastLoginAt(lastLoginAt)
                .build();
    }
}
