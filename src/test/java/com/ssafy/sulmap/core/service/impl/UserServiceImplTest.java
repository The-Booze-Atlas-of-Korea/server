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
import org.junit.jupiter.api.DisplayName;   // ⬅ 추가
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
@DisplayName("UserServiceImpl 유닛 테스트")
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
    @DisplayName("회원가입 성공 - 새로운 로그인 아이디로 가입하면 UserId를 반환한다")
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
    @DisplayName("회원가입 실패 - 로그인 아이디 중복 시 ConflictError를 반환한다")
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

    /// FR6
    // 유저 업데이트 성공
    @Test
    @DisplayName("프로필 수정 성공 - 존재하는 유저의 프로필 정보를 수정하면 UserId를 반환한다")
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

    // 유저 업데이트 실패
    @Test
    @DisplayName("프로필 수정 실패 - 존재하지 않는 유저 ID면 NotFoundError를 반환한다")
    void updateUserProfile_NotFoundUserId_returnsNotFoundError() {
        var updateCommand = _fixtureMonkey.giveMeOne(UpdateUserProfileCommand.class);

        when(_userRepository.findById(updateCommand.getUserId())).thenReturn(Optional.empty());

        var result = _userService.updateUserProfile(updateCommand);

        assertTrue(result.isFailure(), "실패");
        assertNotNull(result.getErrors());
        assertFalse(result.getErrors().isEmpty(), "에러 리스트가 있어야 한다.");
        assertInstanceOf(NotFoundError.class, result.getErrors().get(0), "에러는 NotFoundError 이어야 한다.");
    }

    /// FR7
    // 유저 삭제 성공
    @Test
    @DisplayName("소프트 삭제 성공 - 존재하는 유저를 삭제하면 UserId를 반환한다")
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
    @DisplayName("소프트 삭제 실패 - 존재하지 않는 유저 ID면 NotFoundError를 반환한다")
    void softDeleteUser_NotFoundUserId_returnsNotFoundError() {
        var userId = _fixtureMonkey.giveMeOne(Long.class);

        when(_userRepository.findById(userId)).thenReturn(Optional.empty());

        var result = _userService.softDeleteUser(userId);

        assertTrue(result.isFailure(), "실패");
        assertNotNull(result.getErrors());
        assertFalse(result.getErrors().isEmpty(), "에러 리스트가 있어야 한다.");
        assertInstanceOf(NotFoundError.class, result.getErrors().get(0), "에러는 NotFoundError 이어야 한다.");
    }

    /// FR19
    //업데이트 성공
    @Test
    @DisplayName("프로필 방문 공개 범위 수정 성공 - 존재하는 유저의 공개 범위를 변경하면 UserId를 반환한다")
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
    @DisplayName("프로필 방문 공개 범위 수정 실패 - 존재하지 않는 유저 ID면 NotFoundError를 반환한다")
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

    /// NotFoundError 찾을수 없는 아이디
    /// {@return UserModel}
    //유저 찾기 성공
    @Test
    @DisplayName("로그인 아이디로 유저 조회 성공 - 존재하는 loginId면 UserModel을 반환한다")
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
    @DisplayName("로그인 아이디로 유저 조회 실패 - 존재하지 않는 loginId면 NotFoundError를 반환한다")
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
    @DisplayName("ID로 유저 조회 성공 - 존재하는 UserId면 UserModel을 반환한다")
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
    @DisplayName("ID로 유저 조회 실패 - 존재하지 않는 UserId면 NotFoundError를 반환한다")
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

    /// FR2 로그인
    //로그인 성공
    @Test
    @DisplayName("로그인 성공 - 아이디와 비밀번호가 일치하면 UserId를 반환한다")
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
    @DisplayName("로그인 실패 - 존재하지 않는 로그인 아이디면 NotFoundError를 반환한다")
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
    @DisplayName("로그인 실패 - 비밀번호가 일치하지 않으면 NotFoundError를 반환한다")
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

    //유저가 다른 유저의 프로필을 보기위해 모델을 불려올때
    @Test
    @DisplayName("ID로 Viewer용 유저 조회 성공 - 존재하는 UserId면 UserModel을 반환한다")
    void findUserByIdForViewer_success() {
        var userId = _fixtureMonkey.giveMeOne(Long.class);
        var userModel = _fixtureMonkey.giveMeOne(UserModel.class);
        userModel.setVisitVisibilitySetting(UserProfileVisitVisibility.PUBLIC);
        userModel.setId(userId);

        when(_userRepository.findById(userId)).thenReturn(Optional.of(userModel));

        var result = _userService.findUserByIdForViewer(userId);

        assertTrue(result.isSuccess(), "성공");
        assertEquals(userModel.toString(), result.getValue().orElseThrow().toString());
    }

    //유저 찾기 실패 -> id를 찾을수 없음
    @Test
    @DisplayName("ID로 Viewer용 유저 조회 실패 - 존재하지 않는 UserId면 NotFoundError를 반환한다")
    void findUserByIdForViewer_NotFoundUserId_returnsNotFoundError() {
        var userId = _fixtureMonkey.giveMeOne(Long.class);
        var userModel = _fixtureMonkey.giveMeOne(UserModel.class);
        userModel.setId(userId);

        when(_userRepository.findById(userId)).thenReturn(Optional.empty());

        var result = _userService.findUserByIdForViewer(userId);

        assertTrue(result.isFailure(), "실패");
        assertNotNull(result.getErrors());
        assertFalse(result.getErrors().isEmpty(), "에러 리스트가 있어야 한다.");
        assertInstanceOf(NotFoundError.class, result.getErrors().get(0), "에러는 NotFoundError 이어야 한다.");
    }

    @Test
    @DisplayName("ID로 Viewer용 유저 조회 실패 - 프로필 공개가 private면 ConflictError를 반환한다.")
    void findUserByIdForViewer_NotOpenProfile_returnsConflictError() {
        var userId = _fixtureMonkey.giveMeOne(Long.class);
        var userModel = _fixtureMonkey.giveMeOne(UserModel.class);
        userModel.setId(userId);
        userModel.setVisitVisibilitySetting(UserProfileVisitVisibility.PRIVATE);

        when(_userRepository.findById(userId)).thenReturn(Optional.of(userModel));

        var result = _userService.findUserByIdForViewer(userId);

        assertTrue(result.isFailure(), "실패");
        assertNotNull(result.getErrors());
        assertFalse(result.getErrors().isEmpty(), "에러 리스트가 있어야 한다.");
        assertInstanceOf(ConflictError.class, result.getErrors().get(0), "에러는 ConflictError 이어야 한다.");
    }
}
