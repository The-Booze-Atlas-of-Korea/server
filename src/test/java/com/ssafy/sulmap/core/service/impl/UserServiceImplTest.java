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
import com.ssafy.sulmap.share.result.error.impl.ServerError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 순수 유닛 테스트:
 * - 스프링 컨텍스트 없이
 * - UserServiceImpl이 UserRepository, PasswordEncoder를 어떻게 호출하는지 검증
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private FixtureMonkey fixtureMonkey;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordHasher;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        fixtureMonkey = FixtureMonkey.create();
    }

    // ---------------------------
    // 회원가입 (registerUser)
    // ---------------------------

    // 회원가입 성공
    @Test
    void registerUser_success() {
        // given
        UserModel createModel = UserModel.builder()
                .loginId("test-login")
                .password("plain-password")
                .name("테스트유저")
                .email("test@example.com")
                .phone("010-0000-0000")
                .gender("M")
                .address("서울시 어딘가")
                .birth(new Date())
                .build();

        when(userRepository.findByLoginId(createModel.getLoginId()))
                .thenReturn(null); // 기존 유저 없음
        when(passwordHasher.encode(createModel.getPassword()))
                .thenReturn("encoded-password");
        when(userRepository.create(any(CreateUserCommand.class)))
                .thenReturn(1L);

        // when
        Result<Long> result = userService.registerUser(createModel);

        // then
        assertTrue(result.isSuccess(), "회원가입은 성공해야 한다.");
        assertEquals(1L, result.getValue().orElseThrow());

        // PasswordEncoder, Repository 호출 검증
        verify(userRepository).findByLoginId(createModel.getLoginId());
        verify(passwordHasher).encode(createModel.getPassword());

        ArgumentCaptor<CreateUserCommand> cmdCaptor = ArgumentCaptor.forClass(CreateUserCommand.class);
        verify(userRepository).create(cmdCaptor.capture());

        CreateUserCommand cmd = cmdCaptor.getValue();
        assertEquals("test-login", cmd.getLoginId());
        assertEquals("encoded-password", cmd.getPasswordHash());
        assertEquals("테스트유저", cmd.getName());
        assertEquals("test@example.com", cmd.getEmail());
        assertEquals("010-0000-0000", cmd.getPhone());
        assertEquals("M", cmd.getGender());
        assertEquals("서울시 어딘가", cmd.getAddress());
        assertNotNull(cmd.getBirthday());
    }

    // 회원가입 실패 -> 로그인 아이디 중복
    @Test
    void registerUser_duplicateLoginID_returnsConflictError() {
        // given
        UserModel createModel = UserModel.builder()
                .loginId("duplicate-login")
                .password("plain-password")
                .build();

        // 이미 존재하는 로그인 아이디 (deletedAt == null)
        FindUserResult existing = FindUserResult.builder()
                .loginId("duplicate-login")
                .deletedAt(null)
                .build();

        when(userRepository.findByLoginId(createModel.getLoginId()))
                .thenReturn(existing);

        // when
        Result<Long> result = userService.registerUser(createModel);

        // then
        assertTrue(result.isFailure(), "중복 로그인아이디면 실패해야 한다.");
        assertNotNull(result.getErrors());
        assertFalse(result.getErrors().isEmpty(), "에러 리스트가 있어야 한다.");
        assertInstanceOf(ConflictError.class, result.getErrors().get(0), "에러는 ConflictError 이어야 한다.");

        // 중복이면 create가 호출되면 안 된다.
        verify(userRepository, never()).create(any(CreateUserCommand.class));
    }

    // 회원가입 실패 -> Repository에서 null 반환 (ServerError)
    @Test
    void registerUser_createReturnsNull_returnsServerError() {
        // given
        UserModel createModel = UserModel.builder()
                .loginId("server-error")
                .password("plain-password")
                .build();

        when(userRepository.findByLoginId(createModel.getLoginId()))
                .thenReturn(null);
        when(passwordHasher.encode(any()))
                .thenReturn("encoded-password");
        when(userRepository.create(any(CreateUserCommand.class)))
                .thenReturn(null); // 비정상 상황

        // when
        Result<Long> result = userService.registerUser(createModel);

        // then
        assertTrue(result.isFailure(), "create가 null이면 실패해야 한다.");
        assertInstanceOf(ServerError.class, result.getErrors().get(0));
    }

    // ---------------------------
    // 프로필 수정 (updateUser)
    // ---------------------------

    // 유저 업데이트 성공
    @Test
    void updateUser_success() {
        // given
        long userId = 1L;
        UserUpdateModel updateModel = UserUpdateModel.builder()
                .name("새이름")
                .email("new@example.com")
                .phone("010-1111-2222")
                .address("새 주소")
                .birth(new Date())
                .gender("F")
                .profile_image_url("https://example.com/profile.png")
                .build();

        // 존재하는 유저 (deletedAt == null)
        FindUserResult findResult = FindUserResult.builder()
                .loginId("login-id")
                .deletedAt(null)
                .build();

        when(userRepository.findById(userId))
                .thenReturn(findResult);
        when(userRepository.update(any(UpdateUserCommand.class)))
                .thenReturn(1L);

        // when
        Result<Long> result = userService.updateUser(userId, updateModel);

        // then
        assertTrue(result.isSuccess(), "성공해야 한다.");
        assertEquals(1L, result.getValue().orElseThrow());

        // UpdateUserCommand에 id가 잘 매핑됐는지 검증
        ArgumentCaptor<UpdateUserCommand> captor = ArgumentCaptor.forClass(UpdateUserCommand.class);
        verify(userRepository).update(captor.capture());

        UpdateUserCommand cmd = captor.getValue();
        assertEquals(updateModel.getName(), cmd.getName());
        assertEquals(updateModel.getEmail(), cmd.getEmail());
        assertEquals(updateModel.getPhone(), cmd.getPhone());
        assertEquals(updateModel.getAddress(), cmd.getAddress());
        assertEquals(updateModel.getBirth(), cmd.getBirthday());
        assertEquals(updateModel.getGender(), cmd.getGender());
    }

    // 유저 업데이트 실패 -> 유저 id 찾을 수 없음
    @Test
    void updateUser_NotFoundUserId_returnsNotFoundError() {
        // given
        long userId = 1L;
        UserUpdateModel updateModel = fixtureMonkey.giveMeOne(UserUpdateModel.class);

        when(userRepository.findById(userId))
                .thenReturn(null);

        // when
        Result<Long> result = userService.updateUser(userId, updateModel);

        // then
        assertTrue(result.isFailure(), "유저가 없으면 실패해야 한다.");
        assertInstanceOf(NotFoundError.class, result.getErrors().get(0));

        verify(userRepository, never()).update(any(UpdateUserCommand.class));
    }

    // 유저 업데이트 실패 -> 삭제된 유저(deletedAt != null)
    @Test
    void updateUser_DeletedUser_returnsNotFoundError() {
        // given
        long userId = 1L;
        UserUpdateModel updateModel = fixtureMonkey.giveMeOne(UserUpdateModel.class);

        FindUserResult deletedUser = FindUserResult.builder()
                .loginId("login-id")
                .deletedAt(new Date())
                .build();

        when(userRepository.findById(userId))
                .thenReturn(deletedUser);

        // when
        Result<Long> result = userService.updateUser(userId, updateModel);

        // then
        assertTrue(result.isFailure(), "삭제된 유저도 NotFound로 처리해야 한다.");
        assertInstanceOf(NotFoundError.class, result.getErrors().get(0));
        verify(userRepository, never()).update(any(UpdateUserCommand.class));
    }

    // 유저 업데이트 실패 -> Repository에서 null 반환 (ServerError)
    @Test
    void updateUser_updateReturnsNull_returnsServerError() {
        // given
        long userId = 1L;
        UserUpdateModel updateModel = fixtureMonkey.giveMeOne(UserUpdateModel.class);

        FindUserResult findResult = FindUserResult.builder()
                .loginId("login-id")
                .deletedAt(null)
                .build();

        when(userRepository.findById(userId))
                .thenReturn(findResult);
        when(userRepository.update(any(UpdateUserCommand.class)))
                .thenReturn(null);

        // when
        Result<Long> result = userService.updateUser(userId, updateModel);

        // then
        assertTrue(result.isFailure(), "update가 null이면 실패해야 한다.");
        assertInstanceOf(ServerError.class, result.getErrors().get(0));
    }

    // ---------------------------
    // 계정 삭제 (deleteUser)
    // ---------------------------

    // 유저 삭제 성공
    @Test
    void deleteUser_success() {
        // given
        long userId = fixtureMonkey.giveMeOne(Long.class);
        FindUserResult findResult = FindUserResult.builder()
                .loginId("login-id")
                .deletedAt(null)
                .build();

        when(userRepository.findById(userId))
                .thenReturn(findResult);
        when(userRepository.delete(userId))
                .thenReturn(Boolean.TRUE);

        // when
        Result result = userService.deleteUser(userId);

        // then
        assertTrue(result.isSuccess(), "성공해야 한다.");
        verify(userRepository).delete(userId);
    }

    // 유저 삭제 실패 -> 유저 id 찾을 수 없음
    @Test
    void deleteUser_NotFoundUserId_returnsNotFoundError() {
        // given
        long userId = fixtureMonkey.giveMeOne(Long.class);

        when(userRepository.findById(userId))
                .thenReturn(null);

        // when
        Result result = userService.deleteUser(userId);

        // then
        assertTrue(result.isFailure(), "유저가 없으면 실패해야 한다.");
        assertInstanceOf(NotFoundError.class, result.getErrors().get(0));
        verify(userRepository, never()).delete(anyLong());
    }

    // 유저 삭제 실패 -> Repository에서 false 반환 (ServerError)
    @Test
    void deleteUser_deleteReturnsFalse_returnsServerError() {
        // given
        long userId = fixtureMonkey.giveMeOne(Long.class);
        FindUserResult findResult = FindUserResult.builder()
                .loginId("login-id")
                .deletedAt(null)
                .build();

        when(userRepository.findById(userId))
                .thenReturn(findResult);
        when(userRepository.delete(userId))
                .thenReturn(Boolean.FALSE);

        // when
        Result result = userService.deleteUser(userId);

        // then
        assertTrue(result.isFailure(), "delete가 false면 실패해야 한다.");
        assertInstanceOf(ServerError.class, result.getErrors().get(0));
    }

    // ---------------------------
    // 술자리 이력 공개 범위 (updateUserDrinkHistory)
    // ---------------------------

    // 업데이트 성공
    @Test
    void updateUserDrinkHistory_success() {
        // given
        long userId = 1L;
        MemberDrinkHistoryOpen historyOpen = MemberDrinkHistoryOpen.OPEN;

        FindUserResult findResult = FindUserResult.builder()
                .loginId("login-id")
                .deletedAt(null)
                .build();

        when(userRepository.findById(userId))
                .thenReturn(findResult);
        when(userRepository.updateDrinkHistoryVisibility(userId, historyOpen))
                .thenReturn(1L);

        // when
        Result<Long> result = userService.updateUserDrinkHistory(userId, historyOpen);

        // then
        assertTrue(result.isSuccess(), "성공해야 한다.");
        assertEquals(1L, result.getValue().orElseThrow());
    }

    // 유저 업데이트 실패 -> 유저 id 찾을 수 없음
    @Test
    void updateUserDrinkHistory_NotFoundUserId_returnsNotFoundError() {
        // given
        long userId = 1L;
        MemberDrinkHistoryOpen historyOpen = MemberDrinkHistoryOpen.CLOSED;

        when(userRepository.findById(userId))
                .thenReturn(null);

        // when
        Result<Long> result = userService.updateUserDrinkHistory(userId, historyOpen);

        // then
        assertTrue(result.isFailure(), "유저가 없으면 실패해야 한다.");
        assertInstanceOf(NotFoundError.class, result.getErrors().get(0));
        verify(userRepository, never()).updateDrinkHistoryVisibility(anyLong(), any());
    }

    // 업데이트 실패 -> Repository에서 null 반환 (ServerError)
    @Test
    void updateUserDrinkHistory_updateReturnsNull_returnsServerError() {
        // given
        long userId = 1L;
        MemberDrinkHistoryOpen historyOpen = MemberDrinkHistoryOpen.ONLY_FRIENDS;

        FindUserResult findResult = FindUserResult.builder()
                .loginId("login-id")
                .deletedAt(null)
                .build();

        when(userRepository.findById(userId))
                .thenReturn(findResult);
        when(userRepository.updateDrinkHistoryVisibility(userId, historyOpen))
                .thenReturn(null);

        // when
        Result<Long> result = userService.updateUserDrinkHistory(userId, historyOpen);

        // then
        assertTrue(result.isFailure(), "update 결과가 null이면 실패해야 한다.");
        assertInstanceOf(ServerError.class, result.getErrors().get(0));
    }

    // ---------------------------
    // 유저 조회 (findUserById)
    // ---------------------------

    // 유저 찾기 성공
    @Test
    void findUserById_success() {
        // given
        String userLoginId = "user-login-id";

        Date birthday = new Date();

        FindUserResult findResult = FindUserResult.builder()
                .loginId(userLoginId)
                .passwordHash("hashed-pw")
                .name("이름")
                .phone("010-1234-5678")
                .email("user@example.com")
                .birthday(birthday)
                .address("서울 어딘가")
                .gender("M")
                .profileImageUrl("https://example.com/profile.png")
                .deletedAt(null)
                .build();

        when(userRepository.findByLoginId(userLoginId))
                .thenReturn(findResult);

        // when
        Result<UserModel> result = userService.findUserById(userLoginId);

        // then
        assertTrue(result.isSuccess(), "성공해야 한다.");
        UserModel actual = result.getValue().orElseThrow();

        assertEquals(findResult.getLoginId(), actual.getLoginId());
        assertEquals(findResult.getPasswordHash(), actual.getPassword());
        assertEquals(findResult.getName(), actual.getName());
        assertEquals(findResult.getPhone(), actual.getPhone());
        assertEquals(findResult.getEmail(), actual.getEmail());
        assertEquals(findResult.getBirthday(), actual.getBirth());
        assertEquals(findResult.getAddress(), actual.getAddress());
        assertEquals(findResult.getGender(), actual.getGender());
        assertEquals(findResult.getProfileImageUrl(), actual.getProfile_image_url());
    }

    // 유저 찾기 실패 -> id를 찾을 수 없음
    @Test
    void findUserById_NotFoundUserId_returnsNotFoundError() {
        // given
        String userLoginId = fixtureMonkey.giveMeOne(String.class);

        when(userRepository.findByLoginId(userLoginId))
                .thenReturn(null);

        // when
        Result<UserModel> result = userService.findUserById(userLoginId);

        // then
        assertTrue(result.isFailure(), "유저가 없으면 실패해야 한다.");
        assertInstanceOf(NotFoundError.class, result.getErrors().get(0));
    }
}
