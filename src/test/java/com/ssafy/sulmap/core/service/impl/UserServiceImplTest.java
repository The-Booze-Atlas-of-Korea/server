package com.ssafy.sulmap.core.service.impl;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.ssafy.sulmap.core.command.CreateUserCommand;
import com.ssafy.sulmap.core.command.UpdateUserCommand;
import com.ssafy.sulmap.core.model.MemberDrinkHistoryOpen;
import com.ssafy.sulmap.core.model.UserModel;
import com.ssafy.sulmap.core.model.UserUpdateModel;
import com.ssafy.sulmap.core.query.FindUserResult;
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

import java.util.Date;

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
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordHasher;

    @InjectMocks
    private UserServiceImpl userService; // UserService 구현체

    @BeforeEach
    void setUp() {
        // @InjectMocks 로 생성자/필드 주입 자동 처리
        _fixtureMonkey = FixtureMonkey.create();
    }

    //회원가입 성공
    @Test
    void registerUser_success() {
        // given
        var createModel = _fixtureMonkey.giveMeOne(UserModel.class);

        when(userRepository.findByLoginId(createModel.getLoginId())).thenReturn(null);
        when(userRepository.create(any(CreateUserCommand.class))).thenReturn(1L);

        Result<Long> result = userService.registerUser(createModel);

        assertTrue(result.isSuccess(), "회원가입은 성공해야 한다.");
        assertEquals(1L, result.getValue().orElseThrow());
    }

    //회원가입 실패 -> 로그인 아이디 중복
    @Test
    void registerUser_duplicateLoginID_returnsConflictErrorError() {
        var createModel = _fixtureMonkey.giveMeOne(UserModel.class);

        when(userRepository.findByLoginId(createModel.getLoginId())).thenReturn(FindUserResult.builder().build());

        Result<Long> result = userService.registerUser(createModel);

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
    void updateUser_success() {
        var updateModel = _fixtureMonkey.giveMeOne(UserUpdateModel.class);
        var userId = 1L;
        when(userRepository.findById(userId)).thenReturn(FindUserResult.builder().build());
        when(userRepository.update(any(UpdateUserCommand.class))).thenReturn(1L);

        var result =  userService.updateUser(userId, updateModel);

        assertTrue(result.isSuccess(), "성공");
        assertEquals(1L, result.getValue().orElseThrow());
    }

    // 유저 업데이트 성공
    @Test
    void updateUser_NotFoundUserId_returnsNotFoundError() {
        var updateModel = _fixtureMonkey.giveMeOne(UserUpdateModel.class);
        var userId = 1L;
        when(userRepository.findById(userId)).thenReturn(null);

        var result = userService.updateUser(userId, updateModel);

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
    void deleteUser_success() {
        var userId = _fixtureMonkey.giveMeOne(Long.class);

        when(userRepository.findById(userId)).thenReturn(FindUserResult.builder().build());
        when(userRepository.delete(userId)).thenReturn(Boolean.TRUE);

        var result = userService.deleteUser(userId);
        assertTrue(result.isSuccess(), "성공");
    }

    @Test
    void deleteUser_NotFoundUserId_returnsNotFoundError() {
        var userId = _fixtureMonkey.giveMeOne(Long.class);

        when(userRepository.findById(userId)).thenReturn(null);

        var result = userService.deleteUser(userId);

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
    void updateUserDrinkHistory_success() {
        var memberDrinkHistoryOpen = _fixtureMonkey.giveMeOne(MemberDrinkHistoryOpen.class);
        var userId = 1L;

        when(userRepository.findById(userId)).thenReturn(FindUserResult.builder().build());
        when(userRepository.updateDrinkHistoryVisibility(userId, memberDrinkHistoryOpen)).thenReturn(1L);

        var result =  userService.updateUserDrinkHistory(userId, memberDrinkHistoryOpen);

        assertTrue(result.isSuccess(), "성공");
        assertEquals(1L, result.getValue().orElseThrow());
    }

    // 유저 업데이트 성공
    @Test
    void updateUserDrinkHistory_NotFoundUserId_returnsNotFoundError() {
        var memberDrinkHistoryOpen = _fixtureMonkey.giveMeOne(MemberDrinkHistoryOpen.class);
        var userId = 1L;

        when(userRepository.findById(userId)).thenReturn(null);

        var result =  userService.updateUserDrinkHistory(userId, memberDrinkHistoryOpen);

        assertTrue(result.isFailure(), "실패");
        assertNotNull(result.getErrors());
        assertFalse(result.getErrors().isEmpty(), "에러 리스트가 있어야 한다.");
        assertInstanceOf(NotFoundError.class, result.getErrors().get(0), "에러는 NotFoundError 이어야 한다.");
    }
}
