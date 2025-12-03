package com.ssafy.sulmap.core.service;

import com.ssafy.sulmap.core.model.MemberDrinkHistoryOpen;
import com.ssafy.sulmap.core.model.UserModel;
import com.ssafy.sulmap.core.model.UserUpdateModel;
import com.ssafy.sulmap.share.result.Result;

import java.util.Date;

public interface UserService {
    /// FR1	사용자는 이름, 전화번호, 이메일, 생년월일, 주소, 성별을 입력해 회원가입을 할 수 있어야 한다.<br/>
    /// ConflictError 중복되는 아이디<br/>
    /// {@return UserID}
    Result<Long> registerUser(UserModel userModel);
    /// FR6	사용자는 자신의 프로필 정보(이름, 전화번호, 이메일, 주소 등)를 수정할 수 있어야 한다.<br/>
    /// NotFoundError 찾을수 없는 아이디<br/>
    /// {@return UserID}
    Result<Long> updateUser(long userId, UserUpdateModel userUpdateModel);

    /// FR7	사용자는 언제든지 계정을 탈퇴(삭제)할 수 있어야 하며, 관련 정책에 따라 데이터가 익명화 또는 삭제 처리되어야 한다.<br/>
    /// NotFoundError 찾을수 없는 아이디<br/>
    Result deleteUser(long userId);

    /// FR19 사용자는 자신의 술자리 이력·방문 기록의 공개 범위(전체 공개 / 친구만 / 비공개)를 설정할 수 있어야 한다.<br/>
    /// NotFoundError 찾을수 없는 아이디<br/>
    /// {@return UserID}
    Result<Long> updateUserDrinkHistory(long userId, MemberDrinkHistoryOpen historyOpen);

    /// NotFoundError 찾을수 없는 아이디<br/>
    /// {@return UserModel}
    Result<UserModel> findUserById(String userId);


}
