package com.ssafy.sulmap.api.controller;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.ssafy.sulmap.api.security.model.UserDetail;
import com.ssafy.sulmap.core.model.DrinkingPlanModel;
import com.ssafy.sulmap.core.model.DrinkingPlanSpotModel;
import com.ssafy.sulmap.core.model.UserModel;
import com.ssafy.sulmap.core.model.command.CreatePlanCommand;
import com.ssafy.sulmap.core.model.command.UpdatePlanCommand;
import com.ssafy.sulmap.core.model.enums.PlanTheme;
import com.ssafy.sulmap.core.service.PlanService;
import com.ssafy.sulmap.share.result.Result;
import com.ssafy.sulmap.share.result.error.impl.NotFoundError;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * PlanController 유닛 테스트
 * - UserControllerTest 패턴 따름
 * - Result mocking + HttpStatus 검증
 */
@DisplayName("PlanController 유닛 테스트")
@ExtendWith(MockitoExtension.class)
class PlanControllerTest {

    private FixtureMonkey _fixtureMonkey;

    @Mock
    private PlanService _planService;

    @InjectMocks
    private PlanController planController;

    @BeforeEach
    void setUp() {
        _fixtureMonkey = FixtureMonkey.builder()
                .defaultNotNull(true)
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private UserModel createUserModel(long id) {
        var model = _fixtureMonkey.giveMeOne(UserModel.class);
        model.setId(id);
        return model;
    }

    private UserDetail createUserDetail(long userId) {
        return new UserDetail(createUserModel(userId));
    }

    @Test
    @DisplayName("POST /api/plans - 플랜 생성 성공")
    void createPlan_Success() throws Exception {
        // given
        Long userId = 1L;
        UserDetail principal = createUserDetail(userId);

        DrinkingPlanModel createdPlan = DrinkingPlanModel.builder()
                .id(10L)
                .ownerUserId(userId)
                .title("연말 회식 플랜")
                .description("2024 연말 회식")
                .theme(PlanTheme.COMPANY)
                .totalBudget(100000L)
                .spots(Arrays.asList())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        @SuppressWarnings("unchecked")
        Result<DrinkingPlanModel> result = (Result<DrinkingPlanModel>) mock(Result.class);
        when(result.isFailure()).thenReturn(false);
        when(result.getOrThrow()).thenReturn(createdPlan);
        when(_planService.createPlan(any(CreatePlanCommand.class))).thenReturn(result);

        // when
        ResponseEntity<?> response = planController.createPlan(null, principal); // request는 나중에 구현

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(_planService).createPlan(any(CreatePlanCommand.class));
    }

    @Test
    @DisplayName("POST /api/plans - 플랜 생성 실패 시 에러 상태 반환")
    void createPlan_Failure_ReturnsErrorStatus() throws Exception {
        // given
        Long userId = 1L;
        UserDetail principal = createUserDetail(userId);

        @SuppressWarnings("unchecked")
        Result<DrinkingPlanModel> result = (Result<DrinkingPlanModel>) mock(Result.class);
        NotFoundError error = mock(NotFoundError.class);

        when(result.isFailure()).thenReturn(true);
        when(result.getSingleErrorOrThrow()).thenReturn(error);
        when(error.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
        when(_planService.createPlan(any(CreatePlanCommand.class))).thenReturn(result);

        // when
        ResponseEntity<?> response = planController.createPlan(null, principal);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("PUT /api/plans/{id} - 플랜 수정 성공")
    void updatePlan_Success() throws Exception {
        // given
        Long userId = 1L;
        Long planId = 10L;
        UserDetail principal = createUserDetail(userId);

        DrinkingPlanModel updatedPlan = DrinkingPlanModel.builder()
                .id(planId)
                .ownerUserId(userId)
                .title("수정된 플랜")
                .theme(PlanTheme.FRIEND)
                .build();

        @SuppressWarnings("unchecked")
        Result<DrinkingPlanModel> result = (Result<DrinkingPlanModel>) mock(Result.class);
        when(result.isFailure()).thenReturn(false);
        when(result.getOrThrow()).thenReturn(updatedPlan);
        when(_planService.updatePlan(any(UpdatePlanCommand.class))).thenReturn(result);

        // when
        ResponseEntity<?> response = planController.updatePlan(planId, null, principal);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(_planService).updatePlan(any(UpdatePlanCommand.class));
    }

    @Test
    @DisplayName("PUT /api/plans/{id} - 존재하지 않는 플랜 수정 시 404 반환")
    void updatePlan_NotFound() throws Exception {
        // given
        Long userId = 1L;
        Long planId = 999L;
        UserDetail principal = createUserDetail(userId);

        @SuppressWarnings("unchecked")
        Result<DrinkingPlanModel> result = (Result<DrinkingPlanModel>) mock(Result.class);
        NotFoundError error = mock(NotFoundError.class);

        when(result.isFailure()).thenReturn(true);
        when(result.getSingleErrorOrThrow()).thenReturn(error);
        when(error.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
        when(_planService.updatePlan(any(UpdatePlanCommand.class))).thenReturn(result);

        // when
        ResponseEntity<?> response = planController.updatePlan(planId, null, principal);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("PUT /api/plans/{id} - 권한 없는 사용자가 수정 시 403 반환")
    void updatePlan_Unauthorized() throws Exception {
        // given
        Long userId = 2L; // 다른 사용자
        Long planId = 10L;
        UserDetail principal = createUserDetail(userId);

        @SuppressWarnings("unchecked")
        Result<DrinkingPlanModel> result = (Result<DrinkingPlanModel>) mock(Result.class);
        NotFoundError error = mock(NotFoundError.class);

        when(result.isFailure()).thenReturn(true);
        when(result.getSingleErrorOrThrow()).thenReturn(error);
        when(error.getStatus()).thenReturn(HttpStatus.FORBIDDEN);
        when(_planService.updatePlan(any(UpdatePlanCommand.class))).thenReturn(result);

        // when
        ResponseEntity<?> response = planController.updatePlan(planId, null, principal);

        // then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("GET /api/plans/{id} - 플랜 조회 성공")
    void getPlan_Success() throws Exception {
        // given
        Long planId = 10L;

        DrinkingPlanModel plan = DrinkingPlanModel.builder()
                .id(planId)
                .ownerUserId(1L)
                .title("테스트 플랜")
                .theme(PlanTheme.FRIEND)
                .build();

        @SuppressWarnings("unchecked")
        Result<DrinkingPlanModel> result = (Result<DrinkingPlanModel>) mock(Result.class);
        when(result.isFailure()).thenReturn(false);
        when(result.getOrThrow()).thenReturn(plan);
        when(_planService.getPlan(eq(planId))).thenReturn(result);

        // when
        ResponseEntity<?> response = planController.getPlan(planId);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(_planService).getPlan(planId);
    }

    @Test
    @DisplayName("GET /api/plans/{id} - 존재하지 않는 플랜 조회 시 404 반환")
    void getPlan_NotFound() throws Exception {
        // given
        Long planId = 999L;

        @SuppressWarnings("unchecked")
        Result<DrinkingPlanModel> result = (Result<DrinkingPlanModel>) mock(Result.class);
        NotFoundError error = mock(NotFoundError.class);

        when(result.isFailure()).thenReturn(true);
        when(result.getSingleErrorOrThrow()).thenReturn(error);
        when(error.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
        when(_planService.getPlan(eq(planId))).thenReturn(result);

        // when
        ResponseEntity<?> response = planController.getPlan(planId);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("DELETE /api/plans/{id} - 플랜 삭제 성공")
    void deletePlan_Success() throws Exception {
        // given
        Long userId = 1L;
        Long planId = 10L;
        UserDetail principal = createUserDetail(userId);

        @SuppressWarnings("unchecked")
        Result<Void> result = (Result<Void>) mock(Result.class);
        when(result.isFailure()).thenReturn(false);
        // deletePlan은 void를 감싼 Result이므로 getOrThrow 필요 없음

        // when
        ResponseEntity<?> response = planController.deletePlan(planId, principal);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /api/plans/{id} - 권한 없는 사용자가 삭제 시 403 반환")
    void deletePlan_Unauthorized() throws Exception {
        // given
        Long userId = 2L;
        Long planId = 10L;
        UserDetail principal = createUserDetail(userId);

        @SuppressWarnings("unchecked")
        Result<Void> result = (Result<Void>) mock(Result.class);
        NotFoundError error = mock(NotFoundError.class);

        when(result.isFailure()).thenReturn(true);
        when(result.getSingleErrorOrThrow()).thenReturn(error);
        when(error.getStatus()).thenReturn(HttpStatus.FORBIDDEN);

        // when
        ResponseEntity<?> response = planController.deletePlan(planId, principal);

        // then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }
}
