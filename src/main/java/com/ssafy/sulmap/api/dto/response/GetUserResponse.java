package com.ssafy.sulmap.api.dto.response;

import com.ssafy.sulmap.core.model.UserModel;
import com.ssafy.sulmap.core.model.enums.UserAuthProvider;
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
public class GetUserResponse {
    private Long id;
    private String loginId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Date birthday;
    private UserGender gender;
    private String profileImageUrl;
    private UserAuthProvider authProvider;
    private String providerId;
    private UserStatus status;
    private UserProfileVisitVisibility visitVisibilitySetting; //공개여부
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private Date lastLoginAt;

    public static GetUserResponse fromModel(UserModel userModel){
        return GetUserResponse.builder()
                .id(userModel.getId())
                .loginId(userModel.getLoginId())
                .name(userModel.getName())
                .email(userModel.getEmail())
                .phone(userModel.getPhone())
                .address(userModel.getAddress())
                .birthday(userModel.getBirthday())
                .gender(userModel.getGender())
                .profileImageUrl(userModel.getProfileImageUrl())
                .authProvider(userModel.getAuthProvider())
                .providerId(userModel.getProviderId())
                .status(userModel.getStatus())
                .visitVisibilitySetting(userModel.getVisitVisibilitySetting())
                .createdAt(userModel.getCreatedAt())
                .updatedAt(userModel.getUpdatedAt())
                .deletedAt(userModel.getDeletedAt())
                .lastLoginAt(userModel.getLastLoginAt())
                .build();
    }
}
