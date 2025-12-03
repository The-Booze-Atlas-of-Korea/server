package com.ssafy.sulmap.core.repository;

import com.ssafy.sulmap.core.command.CreatUserCommand;
import com.ssafy.sulmap.core.command.UpdateUserCommand;
import com.ssafy.sulmap.core.query.FindUserResult;

public interface UserRepository {
    /// 유저 생성
    /// @return 만들어진 user id
    Long create(CreatUserCommand createUserCommand);
    /// 유저 업데이트
    /// @return 업데이트 된 user id
    Long update(UpdateUserCommand updateUserCommand);
    /// 유저 삭제
    /// @return 성공 여부
    boolean delete(long id);
    /// 유저 찾기
    /// @return FindUserResult
    FindUserResult findById(long id);
    /// loginid로 유저찾기
    /// @return FindUserResult
    FindUserResult findByLoginId(String loginId);
}
