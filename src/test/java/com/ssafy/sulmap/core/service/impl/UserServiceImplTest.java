package com.ssafy.sulmap.core.service.impl;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.ssafy.sulmap.core.model.command.CreateUserCommand;
import com.ssafy.sulmap.core.model.command.LoginUserCommand;
import com.ssafy.sulmap.core.model.command.UpdateUserProfileCommand;
import com.ssafy.sulmap.core.model.UserModel;
import com.ssafy.sulmap.core.model.enums.UserProfileVisitVisibility;
import com.ssafy.sulmap.core.repository.UserRepository;
import com.ssafy.sulmap.share.result.Result;
import com.ssafy.sulmap.share.result.error.impl.ConflictError;
import com.ssafy.sulmap.share.result.error.impl.NotFoundError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 순수 유닛 테스트:
 * - 스프링 컨텍스트 없이
 * - UserServiceImpl이 UserRepository, PasswordHasher를 어떻게 호출하는지 검증
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    //faker
    private FixtureMonkey _fixtureMonkey;

    @Mock
    private UserRepository _userRepository;

    @Mock
    private PasswordEncoder _passwordEncoder;

    @InjectMocks
    private UserServiceImpl _userService; // UserService 구현체

    @BeforeEach
    void setUp() {
        _fixtureMonkey = FixtureMonkey.builder()
                .defaultNotNull(true)
                .build();
    }

    //회원가입 성공
    @Test
    void CreateUser_success() {
        var createUserCommand = _fixtureMonkey.giveMeOne(CreateUserCommand.class);
        var userId = _fixtureMonkey.giveMeOne(Long.class);

        when(_userRepository.findByLoginId(createUserCommand.getLoginId())).thenReturn(Optional.empty());
        when(_userRepository.save(any(UserModel.class))).thenReturn(userId);

        Result<Long> result = _userService.CreateUser(createUserCommand);

        assertTrue(result.isSuccess(), "회원가입은 성공해야 한다.");
        assertEquals(userId, result.getValue().orElseThrow());
    }

    //회원가입 실패 -> 로그인 아이디 중복
    @Test
    void CreateUser_duplicateLoginID_returnsConflictErrorError() {
        var createUserCommand = _fixtureMonkey.giveMeOne(CreateUserCommand.class);
        var userModel = _fixtureMonkey.giveMeOne(UserModel.class);

        when(_userRepository.findByLoginId(createUserCommand.getLoginId())).thenReturn(Optional.ofNullable(userModel));

        Result<Long> result = _userService.CreateUser(createUserCommand);

        assertTrue(result.isFailure(), "중복 로그인아이디면 실패해야 한다.");
        assertNotNull(result.getErrors());
        assertFalse(result.getErrors().isEmpty(), "에러 리스트가 있어야 한다.");
        assertInstanceOf(ConflictError.class, result.getErrors().get(0), "에러는 ConflictError 이어야 한다.");
    }

    /// FR6	사용자는 자신의 프로필 정보(이름, 전화번호, 이메일, 주소 등)를 수정할 수 있어야 한다.<br/>
    /// NotFoundError 찾을수 없는 아이디<br/>
    /// {@return UserID}<br/>
    /// Result<Long> updateUser(long userId, UserUpdateModel userUpdateModel);<br/>
    // 유저 업데이트 성공
    @Test
    void updateUserProfile_success() {
        var updateCommand = _fixtureMonkey.giveMeOne(UpdateUserProfileCommand.class);
        var userModel = _fixtureMonkey.giveMeOne(UserModel.class);
        userModel.setId(updateCommand.getUserId());

        when(_userRepository.findById(updateCommand.getUserId())).thenReturn(Optional.of(userModel));
        when(_userRepository.save(any(UserModel.class))).thenReturn(updateCommand.getUserId());

        var result =  _userService.updateUserProfile(updateCommand);

        assertTrue(result.isSuccess(), "성공");
        assertNotNull(userModel);
        assertEquals(userModel.getId(), result.getValue().orElseThrow());
    }

    // 유저 업데이트 성공
    @Test
    void updateUserProfile_NotFoundUserId_returnsNotFoundError() {
        var updateCommand = _fixtureMonkey.giveMeOne(UpdateUserProfileCommand.class);

        when(_userRepository.findById(updateCommand.getUserId())).thenReturn(Optional.empty());

        var result = _userService.updateUserProfile(updateCommand);

        assertTrue(result.isFailure(), "실패");
        assertNotNull(result.getErrors());
        assertFalse(result.getErrors().isEmpty(), "에러 리스트가 있어야 한다.");
        assertInstanceOf(NotFoundError.class, result.getErrors().get(0), "에러는 NotFoundError 이어야 한다.");
    }

    /// FR7	사용자는 언제든지 계정을 탈퇴(삭제)할 수 있어야 하며, 관련 정책에 따라 데이터가 익명화 또는 삭제 처리되어야 한다.<br/>
    /// NotFoundError 찾을수 없는 아이디<br/>
    // Result deleteUser(long userId);
    // 유저 삭제 성공
    @Test
    void softDeleteUser_success() {
        var userId = _fixtureMonkey.giveMeOne(Long.class);
        var userModel = _fixtureMonkey.giveMeOne(UserModel.class);
        userModel.setId(userId);

        when(_userRepository.findById(userId)).thenReturn(Optional.of(userModel));
        when(_userRepository.save(any(UserModel.class))).thenReturn(userId);

        var result = _userService.softDeleteUser(userId);
        assertTrue(result.isSuccess(), "성공");
        assertNotNull(userModel);
        assertEquals(userId, result.getValue().orElseThrow());
    }

    @Test
    void softDeleteUser_NotFoundUserId_returnsNotFoundError() {
        var userId = _fixtureMonkey.giveMeOne(Long.class);

        when(_userRepository.findById(userId)).thenReturn(Optional.empty());

        var result = _userService.softDeleteUser(userId);

        assertTrue(result.isFailure(), "실패");
        assertNotNull(result.getErrors());
        assertFalse(result.getErrors().isEmpty(), "에러 리스트가 있어야 한다.");
        assertInstanceOf(NotFoundError.class, result.getErrors().get(0), "에러는 NotFoundError 이어야 한다.");
    }

    /// FR19 사용자는 자신의 술자리 이력·방문 기록의 공개 범위(전체 공개 / 친구만 / 비공개)를 설정할 수 있어야 한다.<br/>
    /// NotFoundError 찾을수 없는 아이디<br/>
    /// {@return UserID}
    //Result<Long> updateUserDrinkHistory(long userId, MemberDrinkHistoryOpen historyOpen);
    //업데이트 성공
    // 유저 업데이트 성공
    @Test
    void updateUserProfileVisitVisibility_success() {
        var userProfileVisitVisibility = _fixtureMonkey.giveMeOne(UserProfileVisitVisibility.class);
        var userId = _fixtureMonkey.giveMeOne(Long.class);
        var userModel = _fixtureMonkey.giveMeOne(UserModel.class);
        userModel.setId(userId);

        when(_userRepository.findById(userId)).thenReturn(Optional.of(userModel));
        when(_userRepository.save(any(UserModel.class))).thenReturn(userId);

        var result =  _userService.updateUserProfileVisitVisibility(userId, userProfileVisitVisibility);

        assertTrue(result.isSuccess(), "성공");
        assertEquals(userId, result.getValue().orElseThrow());
    }

    // 유저 업데이트 실패 -> 유저 id 찾을수 없음
    @Test
    void updateUserDrinkHistory_NotFoundUserId_returnsNotFoundError() {
        var userProfileVisitVisibility = _fixtureMonkey.giveMeOne(UserProfileVisitVisibility.class);
        var userId = _fixtureMonkey.giveMeOne(Long.class);
        var userModel = _fixtureMonkey.giveMeOne(UserModel.class);
        userModel.setId(userId);

        when(_userRepository.findById(userId)).thenReturn(Optional.empty());

        var result =  _userService.updateUserProfileVisitVisibility(userId, userProfileVisitVisibility);

        assertTrue(result.isFailure(), "실패");
        assertNotNull(result.getErrors());
        assertFalse(result.getErrors().isEmpty(), "에러 리스트가 있어야 한다.");
        assertInstanceOf(NotFoundError.class, result.getErrors().get(0), "에러는 NotFoundError 이어야 한다.");
    }

    /// NotFoundError 찾을수 없는 아이디<br/>
    /// {@return UserModel}
    //Result<UserModel> findUserById(String userId);
    //유저 찾기 성공
    @Test
    void findUserByLoginId_success() {
        var userLoginId = _fixtureMonkey.giveMeOne(String.class);
        var userModel = _fixtureMonkey.giveMeOne(UserModel.class);
        userModel.setLoginId(userLoginId);

        when(_userRepository.findByLoginId(userLoginId)).thenReturn(Optional.of(userModel));

        var result = _userService.findUserByLoginId(userLoginId);

        assertTrue(result.isSuccess(), "성공");
        assertEquals(userModel.toString(), result.getValue().orElseThrow().toString());
    }

    //유저 찾기 실패 -> id를 찾을수 없음
    @Test
    void findUserByLoginId_NotFoundUserId_returnsNotFoundError() {
        var userLoginId = _fixtureMonkey.giveMeOne(String.class);
        var userModel = _fixtureMonkey.giveMeOne(UserModel.class);
        userModel.setLoginId(userLoginId);

        when(_userRepository.findByLoginId(userLoginId)).thenReturn(Optional.empty());

        var result = _userService.findUserByLoginId(userLoginId);

        assertTrue(result.isFailure(), "실패");
        assertNotNull(result.getErrors());
        assertFalse(result.getErrors().isEmpty(), "에러 리스트가 있어야 한다.");
        assertInstanceOf(NotFoundError.class, result.getErrors().get(0), "에러는 NotFoundError 이어야 한다.");
    }

    //유저 찾기 성공
    @Test
    void findUserById_success() {
        var userId = _fixtureMonkey.giveMeOne(Long.class);
        var userModel = _fixtureMonkey.giveMeOne(UserModel.class);
        userModel.setId(userId);

        when(_userRepository.findById(userId)).thenReturn(Optional.of(userModel));

        var result = _userService.findUserById(userId);

        assertTrue(result.isSuccess(), "성공");
        assertEquals(userModel.toString(), result.getValue().orElseThrow().toString());
    }

    //유저 찾기 실패 -> id를 찾을수 없음
    @Test
    void findUserById_NotFoundUserId_returnsNotFoundError() {
        var userId = _fixtureMonkey.giveMeOne(Long.class);
        var userModel = _fixtureMonkey.giveMeOne(UserModel.class);
        userModel.setId(userId);


        when(_userRepository.findById(userId)).thenReturn(Optional.empty());

        var result = _userService.findUserById(userId);

        assertTrue(result.isFailure(), "실패");
        assertNotNull(result.getErrors());
        assertFalse(result.getErrors().isEmpty(), "에러 리스트가 있어야 한다.");
        assertInstanceOf(NotFoundError.class, result.getErrors().get(0), "에러는 NotFoundError 이어야 한다.");
    }

    /// FR2	사용자는 아이디와 비밀번호로 로그인 및 로그아웃을 할 수 있어야 한다. <br/>
    /// NotFoundError 아이디 또는 비밀번호가 틀렸을때 <br/>
    /// {@return UserID} <br/>
    /// Result<Long> LoginUser(LoginUserCommand command);
    //로그인 성공
    @Test
    void LoginUser_success() {
        var command = _fixtureMonkey.giveMeOne(LoginUserCommand.class);
        var userModel = _fixtureMonkey.giveMeOne(UserModel.class);
        userModel.setLoginId(command.getLoginId());

        when(_userRepository.findByLoginId(command.getLoginId())).thenReturn(Optional.of(userModel));
        when(_passwordEncoder.matches(command.getPassword(), userModel.getPasswordHash())).thenReturn(true);
        when(_userRepository.save(any(UserModel.class))).thenReturn(userModel.getId());

        var result = _userService.LoginUser(command);

        assertTrue(result.isSuccess(), "성공");
        assertEquals(userModel.getId(), result.getValue().orElseThrow());
    }

    //로그인 실패 - 아이디가 없음
    @Test
    void LoginUser_NotFoundUserId_returnsNotFoundError() {
        var command = _fixtureMonkey.giveMeOne(LoginUserCommand.class);
        var userModel = _fixtureMonkey.giveMeOne(UserModel.class);
        userModel.setLoginId(command.getLoginId());

        when(_userRepository.findByLoginId(command.getLoginId())).thenReturn(Optional.empty());

        var result = _userService.LoginUser(command);

        assertTrue(result.isFailure(), "실패");
        assertNotNull(result.getErrors());
        assertFalse(result.getErrors().isEmpty(), "에러 리스트가 있어야 한다.");
        assertInstanceOf(NotFoundError.class, result.getErrors().get(0), "에러는 NotFoundError 이어야 한다.");
    }

    //로그인 실패 - 비밀번호가 틀림
    @Test
    void LoginUser_NotFoundPassword_returnsNotFoundError() {
        var command = _fixtureMonkey.giveMeOne(LoginUserCommand.class);
        var userModel = _fixtureMonkey.giveMeOne(UserModel.class);
        userModel.setLoginId(command.getLoginId());

        when(_userRepository.findByLoginId(command.getLoginId())).thenReturn(Optional.of(userModel));
        when(_passwordEncoder.matches(command.getPassword(), userModel.getPasswordHash())).thenReturn(false);

        var result = _userService.LoginUser(command);

        assertTrue(result.isFailure(), "실패");
        assertNotNull(result.getErrors());
        assertFalse(result.getErrors().isEmpty(), "에러 리스트가 있어야 한다.");
        assertInstanceOf(NotFoundError.class, result.getErrors().get(0), "에러는 NotFoundError 이어야 한다.");
    }
}
