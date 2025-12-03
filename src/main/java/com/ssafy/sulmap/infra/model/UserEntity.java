package com.ssafy.sulmap.infra.model;

import java.util.Date;
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
    private Date birthday;
    private String gender;
    private String profileImageUrl;
    private String status;
    private boolean visitVisibility; //공개여부
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private Date lastLogin;
}
