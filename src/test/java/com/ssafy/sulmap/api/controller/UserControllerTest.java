package com.ssafy.sulmap.api.controller;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.instantiator.Instantiator;
import com.ssafy.sulmap.api.dto.request.UpdateUserRequest;
import com.ssafy.sulmap.api.dto.response.GetUserResponse;
import com.ssafy.sulmap.api.security.model.UserDetail;
import com.ssafy.sulmap.core.model.UserModel;
import com.ssafy.sulmap.core.model.enums.UserAuthProvider;
import com.ssafy.sulmap.core.model.enums.UserGender;
import com.ssafy.sulmap.core.model.command.UpdateUserProfileCommand;
import com.ssafy.sulmap.core.model.enums.UserProfileVisitVisibility;
import com.ssafy.sulmap.core.model.enums.UserStatus;
import com.ssafy.sulmap.core.service.UserService;
import com.ssafy.sulmap.share.result.Result;
import com.ssafy.sulmap.share.result.error.impl.NotFoundError;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UserController 유닛 테스트
 * - 스프링 컨텍스트 없이 순수 컨트롤러 메서드 호출
 * - UserService 는 Mockito mock 으로 대체
 */
@DisplayName("UserController 유닛 테스트")
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    //faker
    private FixtureMonkey _fixtureMonkey;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        _fixtureMonkey = FixtureMonkey.builder()
                .defaultNotNull(true)
                .build();
    }


    @AfterEach
    void tearDown() {
        // 테스트 사이에 SecurityContext 오염 방지
        SecurityContextHolder.clearContext();
    }

    private UserModel createUserModel(long id) {
        var model = _fixtureMonkey.giveMeOne(UserModel.class);
        model.setGender(_fixtureMonkey.giveMeOne(UserGender.class));
        model.setAuthProvider(_fixtureMonkey.giveMeOne(UserAuthProvider.class));
        model.setStatus(_fixtureMonkey.giveMeOne(UserStatus.class));
        model.setVisitVisibilitySetting(_fixtureMonkey.giveMeOne(UserProfileVisitVisibility.class));
        model.setId(id);
        return model;
    }

    private UpdateUserRequest createUpdateUserRequest() {
        Date birthday = Date.from(LocalDate.of(1995, 1, 1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
        return _fixtureMonkey.giveMeBuilder(UpdateUserRequest.class)
                .instantiate(Instantiator.constructor()
                        .parameter(String.class)
                        .parameter(String.class)
                        .parameter(String.class)
                        .parameter(String.class)
                        .parameter(Date.class)
                        .parameter(String.class)
                        .parameter(String.class)
                )
                .set("gender", _fixtureMonkey.giveMeOne(UserGender.class).toString())
                .set("birthday", birthday)
                .sample();
    }

    @Test
    @DisplayName("GET /users/me - 현재 로그인한 유저 정보를 반환한다")
    void getUser_me_success() throws Exception {
        // given
        UserModel userModel = createUserModel(1L);
        UserDetail principal = new UserDetail(userModel);

        // when
        ResponseEntity<?> response = userController.getUser(principal);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(GetUserResponse.class, response.getBody());

        GetUserResponse body = (GetUserResponse) response.getBody();
        GetUserResponse expected = GetUserResponse.fromModel(userModel);

        // fromModel 결과와 동일한지 toString 기준으로 비교 (필드 구조를 몰라도 됨)
        assertEquals(expected.toString(), body.toString());
    }

    @Test
    @DisplayName("PATCH /users/me - 프로필 수정 성공 시 UserId 를 반환하고 SecurityContext 의 Authentication 을 갱신한다")
    void updateUserProfile_success() throws Exception {
        // given
        long userId = 1L;
        UserModel originalUser = createUserModel(userId);
        UserDetail principal = new UserDetail(originalUser);
        UpdateUserRequest request = createUpdateUserRequest();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                principal,
                "dummy-credentials",
                Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // userService.updateUserProfile 성공 result mock
        @SuppressWarnings("unchecked")
        Result<Long> updateResult = (Result<Long>) mock(Result.class);
        when(updateResult.isFailure()).thenReturn(false);
        when(updateResult.getOrThrow()).thenReturn(userId);
        when(userService.updateUserProfile(any(UpdateUserProfileCommand.class)))
                .thenReturn(updateResult);

        // update 이후 findUserById 에서 최신 UserModel 반환
        UserModel refreshedUser = createUserModel(userId);
        refreshedUser.setName(request.name());
        refreshedUser.setEmail(request.email());

        @SuppressWarnings("unchecked")
        Result<UserModel> refreshedResult = (Result<UserModel>) mock(Result.class);
        when(refreshedResult.getOrThrow()).thenReturn(refreshedUser);
        when(userService.findUserById(userId)).thenReturn(refreshedResult);

        // when
        ResponseEntity<?> response = userController.updateUserProfile(request, principal);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userId, response.getBody());

        // SecurityContext 에 Authentication 이 설정되었는지 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication, "Authentication 이 SecurityContext 에 설정되어야 한다.");
        assertInstanceOf(UserDetail.class, authentication.getPrincipal(), "principal 은 UserDetail 이어야 한다.");
        UserDetail newPrincipal = (UserDetail) authentication.getPrincipal();
        assertEquals(refreshedUser.getId(), newPrincipal.userModel().getId());
        assertEquals(refreshedUser.getName(), newPrincipal.userModel().getName());

        // 서비스 호출 검증
        verify(userService).updateUserProfile(any(UpdateUserProfileCommand.class));
        verify(userService).findUserById(userId);
    }

    @Test
    @DisplayName("PATCH /users/me - 프로필 수정 실패 시 에러의 HttpStatus 를 반환한다")
    void updateUserProfile_failure_returnsErrorStatus() throws Exception {
        // given
        long userId = 1L;
        UserModel userModel = createUserModel(userId);
        UserDetail principal = new UserDetail(userModel);
        UpdateUserRequest request = createUpdateUserRequest();

        @SuppressWarnings("unchecked")
        Result<Long> updateResult = (Result<Long>) mock(Result.class);
        NotFoundError error = mock(NotFoundError.class);

        when(updateResult.isFailure()).thenReturn(true);
        when(updateResult.getSingleErrorOrThrow()).thenReturn(error);
        when(error.getStatus()).thenReturn(HttpStatus.NOT_FOUND);

        when(userService.updateUserProfile(any(UpdateUserProfileCommand.class)))
                .thenReturn(updateResult);

        // when
        ResponseEntity<?> response = userController.updateUserProfile(request, principal);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        // 실패 시에는 findUserById 를 호출하지 않아야 한다
        verify(userService, never()).findUserById(anyLong());
    }

    @Test
    @DisplayName("DELETE /users/me - 소프트 삭제 성공 시 UserId 를 반환한다")
    void deleteUser_success() throws Exception {
        // given
        long userId = 1L;
        UserModel userModel = createUserModel(userId);
        UserDetail principal = new UserDetail(userModel);

        @SuppressWarnings("unchecked")
        Result<Long> deleteResult = (Result<Long>) mock(Result.class);
        when(deleteResult.isFailure()).thenReturn(false);
        when(deleteResult.getOrThrow()).thenReturn(userId);

        when(userService.softDeleteUser(userId)).thenReturn(deleteResult);

        // when
        ResponseEntity<?> response = userController.deleteUser(principal);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userId, response.getBody());
        verify(userService).softDeleteUser(userId);
    }

    @Test
    @DisplayName("DELETE /users/me - 소프트 삭제 실패 시 에러의 HttpStatus 를 반환한다")
    void deleteUser_failure_returnsErrorStatus() throws Exception {
        // given
        long userId = 1L;
        UserModel userModel = createUserModel(userId);
        UserDetail principal = new UserDetail(userModel);

        @SuppressWarnings("unchecked")
        Result<Long> deleteResult = (Result<Long>) mock(Result.class);
        NotFoundError error = mock(NotFoundError.class);

        when(deleteResult.isFailure()).thenReturn(true);
        when(deleteResult.getSingleErrorOrThrow()).thenReturn(error);
        when(error.getStatus()).thenReturn(HttpStatus.NOT_FOUND);

        when(userService.softDeleteUser(userId)).thenReturn(deleteResult);

        // when
        ResponseEntity<?> response = userController.deleteUser(principal);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService).softDeleteUser(userId);
    }

    @Test
    @DisplayName("GET /users/user/{userid} - 조회 대상 유저가 존재하면 GetUserResponse 를 반환한다")
    void getUserByUserId_success() throws Exception {
        // given
        long targetUserId = 10L;
        UserModel targetUser = createUserModel(targetUserId);

        @SuppressWarnings("unchecked")
        Result<UserModel> result = (Result<UserModel>) mock(Result.class);
        when(result.isFailure()).thenReturn(false);
        when(result.getOrThrow()).thenReturn(targetUser);

        when(userService.findUserByIdForViewer(targetUserId)).thenReturn(result);

        // when
        ResponseEntity<?> response = userController.getUserByUserId(targetUserId);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof GetUserResponse);

        GetUserResponse body = (GetUserResponse) response.getBody();
        GetUserResponse expected = GetUserResponse.fromModel(targetUser);
        assertEquals(expected.toString(), body.toString());

        verify(userService).findUserByIdForViewer(targetUserId);
    }

    @Test
    @DisplayName("GET /users/user/{userid} - 조회 대상 유저가 없으면 에러의 HttpStatus 를 반환한다")
    void getUserByUserId_failure_returnsErrorStatus() throws Exception {
        // given
        long targetUserId = 10L;

        @SuppressWarnings("unchecked")
        Result<UserModel> result = (Result<UserModel>) mock(Result.class);
        NotFoundError error = mock(NotFoundError.class);

        when(result.isFailure()).thenReturn(true);
        when(result.getSingleErrorOrThrow()).thenReturn(error);
        when(error.getStatus()).thenReturn(HttpStatus.NOT_FOUND);

        when(userService.findUserByIdForViewer(targetUserId)).thenReturn(result);

        // when
        ResponseEntity<?> response = userController.getUserByUserId(targetUserId);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService).findUserByIdForViewer(targetUserId);
    }
}
