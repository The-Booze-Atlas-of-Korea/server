package com.ssafy.sulmap.core.repository;

import com.ssafy.sulmap.core.model.UserModel;

import java.util.Optional;

public interface UserRepository {
    /// 유저 생성 <br>
    /// 유저 id가 null이거나 0이면 생성 <br>
    /// 아니면 업데이트
    /// @return 만들어진 user id
    Long save(UserModel user);
    /// 유저 찾기
    /// @return FindUserResult
    Optional<UserModel> findById(long userId);
    /// loginid로 유저찾기
    /// @return FindUserResult
    Optional<UserModel> findByLoginId(String userLoginId);
}
