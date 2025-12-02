package com.ssafy.sulmap.core.query;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.jdbc.repository.query.Query;

import java.util.Date;

@Data
@Builder
public class FindUserResult {
    String loginId;
    String passwordHash;
    String name;
    String email;
    String phone;
    String address;
    String birthday;
    String gender;
    String profileImageUrl;
    String status;
    String visit_visibility;
    Date createdAt;
    Date updatedAt;
    Date deletedAt;
    Date lastLogin;
}
