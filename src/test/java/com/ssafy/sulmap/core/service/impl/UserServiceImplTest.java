package com.ssafy.sulmap.core.service.impl;

import com.ssafy.sulmap.core.command.CreateUserCommand;
import com.ssafy.sulmap.core.model.UserModel;
import com.ssafy.sulmap.core.query.FindUserResult;
import com.ssafy.sulmap.core.repository.UserRepository;
import com.ssafy.sulmap.share.result.Result;
import com.ssafy.sulmap.share.result.error.impl.ConflictError;
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

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordHasher;

    @InjectMocks
    private UserServiceImpl userService; // UserService 구현체

    @BeforeEach
    void setUp() {
        // @InjectMocks 로 생성자/필드 주입 자동 처리
    }

    //회원가입 성공
    @Test
    void registerUser_validInput_succeedsAndHashesPassword() {
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

        // when
        Result<Long> result = userService.registerUser(req);

        // then
        assertTrue(result.isFailure(), "중복 로그인아이디면 실패해야 한다.");
        assertNotNull(result.getErrors());
        assertFalse(result.getErrors().isEmpty(), "에러 리스트가 있어야 한다.");
        assertInstanceOf(ConflictError.class, result.getErrors().get(0), "에러는 ConflictError 이어야 한다.");
    }



    // ===================== 헬퍼 메서드 =====================

    private UserModel createUserModel(String email) {
        return UserModel.builder()
                .loginId("test")
                .password("password")
                .name("기본 유저")
                .phone("010-1111-2222")
                .email(email)
                .birth(new Date(2000, 10, 22))
                .address("서울 어딘가")
                .gender("M")
                .build();
    }
}
