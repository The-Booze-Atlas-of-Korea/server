package com.ssafy.sulmap.core.service.impl;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.ssafy.sulmap.core.command.CreateUserCommand;
import com.ssafy.sulmap.core.command.UpdateUserCommand;
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
        String pw = "plain-password";
        var email = "test@example.com";
        var req = createUserModel(email);

        when(userRepository.findByLoginId(req.getLoginId())).thenReturn(null);
        when(userRepository.create(any(CreateUserCommand.class))).thenReturn(1L);

        Result<Long> result = userService.registerUser(req);

        assertTrue(result.isSuccess(), "회원가입은 성공해야 한다.");
        assertEquals(1L, result.getValue().orElseThrow());
    }

    //회원가입 실패 -> 로그인 아이디 중복
    @Test
    void registerUser_duplicateLoginID_returnsConflictErrorError() {
        String pw = "plain-password";
        var email = "test@example.com";
        var req = createUserModel(email);

        when(userRepository.findByLoginId(req.getLoginId())).thenReturn(FindUserResult.builder().build());

        Result<Long> result = userService.registerUser(req);

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



    // ===================== 헬퍼 메서드 =====================
}
