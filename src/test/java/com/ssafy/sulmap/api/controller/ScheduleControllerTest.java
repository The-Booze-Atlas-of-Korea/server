package com.ssafy.sulmap.api.controller;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.ssafy.sulmap.api.security.model.UserDetail;
import com.ssafy.sulmap.core.model.DrinkingScheduleModel;
import com.ssafy.sulmap.core.model.UserModel;
import com.ssafy.sulmap.core.model.command.CreateScheduleCommand;
import com.ssafy.sulmap.core.model.command.UpdateScheduleCommand;
import com.ssafy.sulmap.core.model.enums.ScheduleStatus;
import com.ssafy.sulmap.core.model.query.GetSchedulesInPeriodQuery;
import com.ssafy.sulmap.core.service.ScheduleService;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * ScheduleController 유닛 테스트
 * - UserControllerTest 패턴 따름
 * - Result mocking + HttpStatus 검증
 */
@DisplayName("ScheduleController 유닛 테스트")
@ExtendWith(MockitoExtension.class)
class ScheduleControllerTest {

    private FixtureMonkey _fixtureMonkey;

    @Mock
    private ScheduleService _scheduleService;

    @InjectMocks
    private ScheduleController scheduleController;

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
    @DisplayName("POST /api/schedules - 일정 생성 성공")
    void createSchedule_Success() throws Exception {
        // given
        Long userId = 1L;
        UserDetail principal = createUserDetail(userId);

        var request = new com.ssafy.sulmap.api.dto.request.CreateScheduleRequest(
                10L,
                "크리스마스 모임",
                LocalDateTime.of(2024, 12, 25, 18, 0));

        DrinkingScheduleModel createdSchedule = DrinkingScheduleModel.builder()
                .id(100L)
                .ownerUserId(userId)
                .planId(10L)
                .scheduleTitle("크리스마스 모임")
                .meetAt(LocalDateTime.of(2024, 12, 25, 18, 0))
                .status(ScheduleStatus.PLANNED)
                .build();

        @SuppressWarnings("unchecked")
        Result<DrinkingScheduleModel> result = (Result<DrinkingScheduleModel>) mock(Result.class);
        when(result.isFailure()).thenReturn(false);
        when(result.getOrThrow()).thenReturn(createdSchedule);
        when(_scheduleService.createSchedule(any(CreateScheduleCommand.class))).thenReturn(result);

        // when
        ResponseEntity<?> response = scheduleController.createSchedule(request, principal);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(_scheduleService).createSchedule(any(CreateScheduleCommand.class));
    }

    @Test
    @DisplayName("POST /api/schedules - 일정 생성 실패 시 에러 상태 반환")
    void createSchedule_Failure_ReturnsErrorStatus() throws Exception {
        // given
        Long userId = 1L;
        UserDetail principal = createUserDetail(userId);

        var request = new com.ssafy.sulmap.api.dto.request.CreateScheduleRequest(
                null,
                "일정 제목",
                LocalDateTime.now());

        @SuppressWarnings("unchecked")
        Result<DrinkingScheduleModel> result = (Result<DrinkingScheduleModel>) mock(Result.class);
        NotFoundError error = mock(NotFoundError.class);

        when(result.isFailure()).thenReturn(true);
        when(result.getSingleErrorOrThrow()).thenReturn(error);
        when(error.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
        when(_scheduleService.createSchedule(any(CreateScheduleCommand.class))).thenReturn(result);

        // when
        ResponseEntity<?> response = scheduleController.createSchedule(request, principal);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("PUT /api/schedules/{id} - 일정 수정 성공")
    void updateSchedule_Success() throws Exception {
        // given
        Long userId = 1L;
        Long scheduleId = 100L;
        UserDetail principal = createUserDetail(userId);

        var request = new com.ssafy.sulmap.api.dto.request.UpdateScheduleRequest(
                "수정된 일정",
                null,
                "COMPLETED");

        DrinkingScheduleModel updatedSchedule = DrinkingScheduleModel.builder()
                .id(scheduleId)
                .ownerUserId(userId)
                .scheduleTitle("수정된 일정")
                .status(ScheduleStatus.COMPLETED)
                .build();

        @SuppressWarnings("unchecked")
        Result<DrinkingScheduleModel> result = (Result<DrinkingScheduleModel>) mock(Result.class);
        when(result.isFailure()).thenReturn(false);
        when(result.getOrThrow()).thenReturn(updatedSchedule);
        when(_scheduleService.updateSchedule(any(UpdateScheduleCommand.class))).thenReturn(result);

        // when
        ResponseEntity<?> response = scheduleController.updateSchedule(scheduleId, request, principal);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(_scheduleService).updateSchedule(any(UpdateScheduleCommand.class));
    }

    @Test
    @DisplayName("PUT /api/schedules/{id} - 존재하지 않는 일정 수정 시 404 반환")
    void updateSchedule_NotFound() throws Exception {
        // given
        Long userId = 1L;
        Long scheduleId = 999L;
        UserDetail principal = createUserDetail(userId);

        var request = new com.ssafy.sulmap.api.dto.request.UpdateScheduleRequest(
                "일정 제목",
                null,
                null);

        @SuppressWarnings("unchecked")
        Result<DrinkingScheduleModel> result = (Result<DrinkingScheduleModel>) mock(Result.class);
        NotFoundError error = mock(NotFoundError.class);

        when(result.isFailure()).thenReturn(true);
        when(result.getSingleErrorOrThrow()).thenReturn(error);
        when(error.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
        when(_scheduleService.updateSchedule(any(UpdateScheduleCommand.class))).thenReturn(result);

        // when
        ResponseEntity<?> response = scheduleController.updateSchedule(scheduleId, request, principal);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("PUT /api/schedules/{id} - 권한 없는 사용자가 수정 시 403 반환")
    void updateSchedule_Unauthorized() throws Exception {
        // given
        Long userId = 2L; // 다른 사용자
        Long scheduleId = 100L;
        UserDetail principal = createUserDetail(userId);

        var request = new com.ssafy.sulmap.api.dto.request.UpdateScheduleRequest(
                "일정 제목",
                null,
                null);

        @SuppressWarnings("unchecked")
        Result<DrinkingScheduleModel> result = (Result<DrinkingScheduleModel>) mock(Result.class);
        NotFoundError error = mock(NotFoundError.class);

        when(result.isFailure()).thenReturn(true);
        when(result.getSingleErrorOrThrow()).thenReturn(error);
        when(error.getStatus()).thenReturn(HttpStatus.FORBIDDEN);
        when(_scheduleService.updateSchedule(any(UpdateScheduleCommand.class))).thenReturn(result);

        // when
        ResponseEntity<?> response = scheduleController.updateSchedule(scheduleId, request, principal);

        // then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("GET /api/schedules/{id} - 일정 조회 성공")
    void getSchedule_Success() throws Exception {
        // given
        Long scheduleId = 100L;

        DrinkingScheduleModel schedule = DrinkingScheduleModel.builder()
                .id(scheduleId)
                .ownerUserId(1L)
                .scheduleTitle("테스트 일정")
                .status(ScheduleStatus.PLANNED)
                .build();

        @SuppressWarnings("unchecked")
        Result<DrinkingScheduleModel> result = (Result<DrinkingScheduleModel>) mock(Result.class);
        when(result.isFailure()).thenReturn(false);
        when(result.getOrThrow()).thenReturn(schedule);
        when(_scheduleService.getSchedule(eq(scheduleId))).thenReturn(result);

        // when
        ResponseEntity<?> response = scheduleController.getSchedule(scheduleId);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(_scheduleService).getSchedule(scheduleId);
    }

    @Test
    @DisplayName("GET /api/schedules/{id} - 존재하지 않는 일정 조회 시 404 반환")
    void getSchedule_NotFound() throws Exception {
        // given
        Long scheduleId = 999L;

        @SuppressWarnings("unchecked")
        Result<DrinkingScheduleModel> result = (Result<DrinkingScheduleModel>) mock(Result.class);
        NotFoundError error = mock(NotFoundError.class);

        when(result.isFailure()).thenReturn(true);
        when(result.getSingleErrorOrThrow()).thenReturn(error);
        when(error.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
        when(_scheduleService.getSchedule(eq(scheduleId))).thenReturn(result);

        // when
        ResponseEntity<?> response = scheduleController.getSchedule(scheduleId);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("GET /api/schedules/calendar - 기간별 일정 조회 성공")
    void getSchedulesByPeriod_Success() throws Exception {
        // given
        Long userId = 1L;
        UserDetail principal = createUserDetail(userId);
        LocalDateTime from = LocalDateTime.of(2024, 12, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 12, 31, 23, 59);

        List<DrinkingScheduleModel> schedules = Arrays.asList(
                DrinkingScheduleModel.builder().id(1L).scheduleTitle("일정 1").build(),
                DrinkingScheduleModel.builder().id(2L).scheduleTitle("일정 2").build());

        @SuppressWarnings("unchecked")
        Result<List<DrinkingScheduleModel>> result = (Result<List<DrinkingScheduleModel>>) mock(Result.class);
        when(result.isFailure()).thenReturn(false);
        when(result.getOrThrow()).thenReturn(schedules);
        when(_scheduleService.getSchedulesInPeriod(any(GetSchedulesInPeriodQuery.class))).thenReturn(result);

        // when
        ResponseEntity<?> response = scheduleController.getSchedulesByPeriod(from, to, principal);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("GET /api/schedules/calendar - 잘못된 기간 (from > to) 시 400 반환")
    void getSchedulesByPeriod_InvalidPeriod() throws Exception {
        // given
        Long userId = 1L;
        UserDetail principal = createUserDetail(userId);
        LocalDateTime from = LocalDateTime.of(2024, 12, 31, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 12, 1, 0, 0); // from > to

        @SuppressWarnings("unchecked")
        Result<List<DrinkingScheduleModel>> result = (Result<List<DrinkingScheduleModel>>) mock(Result.class);
        NotFoundError error = mock(NotFoundError.class);

        when(result.isFailure()).thenReturn(true);
        when(result.getSingleErrorOrThrow()).thenReturn(error);
        when(error.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);

        // when
        ResponseEntity<?> response = scheduleController.getSchedulesByPeriod(from, to, principal);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /api/schedules/{id} - 일정 삭제 성공")
    void deleteSchedule_Success() throws Exception {
        // given
        Long userId = 1L;
        Long scheduleId = 100L;
        UserDetail principal = createUserDetail(userId);

        @SuppressWarnings("unchecked")
        Result<Void> result = (Result<Void>) mock(Result.class);
        when(result.isFailure()).thenReturn(false);

        // when
        ResponseEntity<?> response = scheduleController.deleteSchedule(scheduleId, principal);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /api/schedules/{id} - 권한 없는 사용자가 삭제 시 403 반환")
    void deleteSchedule_Unauthorized() throws Exception {
        // given
        Long userId = 2L;
        Long scheduleId = 100L;
        UserDetail principal = createUserDetail(userId);

        @SuppressWarnings("unchecked")
        Result<Void> result = (Result<Void>) mock(Result.class);
        NotFoundError error = mock(NotFoundError.class);

        when(result.isFailure()).thenReturn(true);
        when(result.getSingleErrorOrThrow()).thenReturn(error);
        when(error.getStatus()).thenReturn(HttpStatus.FORBIDDEN);

        // when
        ResponseEntity<?> response = scheduleController.deleteSchedule(scheduleId, principal);

        // then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }
}
