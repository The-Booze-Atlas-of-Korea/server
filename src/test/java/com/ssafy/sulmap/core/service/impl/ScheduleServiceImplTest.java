package com.ssafy.sulmap.core.service.impl;

import com.ssafy.sulmap.core.model.enums.ScheduleStatus;
import com.ssafy.sulmap.core.model.DrinkingScheduleModel;
import com.ssafy.sulmap.core.model.command.CreateScheduleCommand;
import com.ssafy.sulmap.core.model.command.UpdateScheduleCommand;
import com.ssafy.sulmap.core.model.query.GetSchedulesInPeriodQuery;
import com.ssafy.sulmap.core.repository.ScheduleRepository;
import com.ssafy.sulmap.share.result.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceImplTest {

        @Mock
        private ScheduleRepository _scheduleRepository;

        @InjectMocks
        private ScheduleServiceImpl scheduleService;

        @Test
        @DisplayName("일정 생성이 성공한다")
        void createSchedule_Success() {
                // given
                LocalDateTime meetAt = LocalDateTime.of(2024, 12, 25, 18, 0);
                CreateScheduleCommand command = new CreateScheduleCommand(
                                1L, 10L, "Christmas Party", meetAt);

                DrinkingScheduleModel savedSchedule = DrinkingScheduleModel.builder()
                                .id(1L)
                                .ownerUserId(1L)
                                .planId(10L)
                                .scheduleTitle("Christmas Party")
                                .meetAt(meetAt)
                                .status(ScheduleStatus.PLANNED)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                when(_scheduleRepository.save(any(DrinkingScheduleModel.class))).thenReturn(savedSchedule);

                // when
                Result<DrinkingScheduleModel> result = scheduleService.createSchedule(command);

                // then
                assertThat(result.isSuccess()).isTrue();
                assertThat(result.getValue()).isPresent();
                assertThat(result.getValue().get().getScheduleTitle()).isEqualTo("Christmas Party");
                verify(_scheduleRepository).save(any(DrinkingScheduleModel.class));
        }

        @Test
        @DisplayName("일정 수정이 성공한다")
        void updateSchedule_Success() {
                // given
                Long scheduleId = 1L;
                Long userId = 1L;
                LocalDateTime newMeetAt = LocalDateTime.of(2025, 1, 1, 20, 0);

                DrinkingScheduleModel existingSchedule = DrinkingScheduleModel.builder()
                                .id(scheduleId)
                                .ownerUserId(userId)
                                .scheduleTitle("Old Title")
                                .meetAt(LocalDateTime.of(2024, 12, 31, 18, 0))
                                .status(ScheduleStatus.PLANNED)
                                .build();

                UpdateScheduleCommand command = new UpdateScheduleCommand(
                                scheduleId, userId, "New Year Party", newMeetAt, ScheduleStatus.COMPLETED);

                DrinkingScheduleModel updatedSchedule = DrinkingScheduleModel.builder()
                                .id(scheduleId)
                                .ownerUserId(userId)
                                .scheduleTitle("New Year Party")
                                .meetAt(newMeetAt)
                                .status(ScheduleStatus.COMPLETED)
                                .build();

                when(_scheduleRepository.findById(eq(scheduleId))).thenReturn(Optional.of(existingSchedule));
                when(_scheduleRepository.save(any(DrinkingScheduleModel.class))).thenReturn(updatedSchedule);

                // when
                Result<DrinkingScheduleModel> result = scheduleService.updateSchedule(command);

                // then
                assertThat(result.isSuccess()).isTrue();
                assertThat(result.getValue()).isPresent();
                assertThat(result.getValue().get().getScheduleTitle()).isEqualTo("New Year Party");
        }

        @Test
        @DisplayName("존재하지 않는 일정 수정 시 실패한다 (404)")
        void updateSchedule_ScheduleNotFound_Fail() {
                // given
                Long scheduleId = 999L;
                Long userId = 1L;

                UpdateScheduleCommand command = new UpdateScheduleCommand(
                                scheduleId, userId, "New Title", LocalDateTime.now(), ScheduleStatus.COMPLETED);

                when(_scheduleRepository.findById(eq(scheduleId))).thenReturn(Optional.empty());

                // when
                Result<DrinkingScheduleModel> result = scheduleService.updateSchedule(command);

                // then
                assertThat(result.isFailure()).isTrue();
                assertThat(result.getErrors()).hasSize(1);
                assertThat(result.getErrors().get(0).getCode()).isEqualTo(404);
                assertThat(result.getErrors().get(0).getMessage()).contains("일정을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("권한 없는 사용자가 일정 수정 시 실패한다 (403)")
        void updateSchedule_Unauthorized_Fail() {
                // given
                Long scheduleId = 1L;
                Long ownerId = 1L;
                Long unauthorizedUserId = 2L;

                DrinkingScheduleModel existingSchedule = DrinkingScheduleModel.builder()
                                .id(scheduleId)
                                .ownerUserId(ownerId)
                                .scheduleTitle("Old Title")
                                .build();

                UpdateScheduleCommand command = new UpdateScheduleCommand(
                                scheduleId, unauthorizedUserId, "New Title", LocalDateTime.now(),
                                ScheduleStatus.COMPLETED);

                when(_scheduleRepository.findById(eq(scheduleId))).thenReturn(Optional.of(existingSchedule));

                // when
                Result<DrinkingScheduleModel> result = scheduleService.updateSchedule(command);

                // then
                assertThat(result.isFailure()).isTrue();
                assertThat(result.getErrors()).hasSize(1);
                assertThat(result.getErrors().get(0).getCode()).isEqualTo(403);
                assertThat(result.getErrors().get(0).getMessage()).contains("권한이 없습니다");
        }

        @Test
        @DisplayName("일정 조회가 성공한다")
        void getSchedule_Success() {
                // given
                Long scheduleId = 1L;
                DrinkingScheduleModel schedule = DrinkingScheduleModel.builder()
                                .id(scheduleId)
                                .ownerUserId(1L)
                                .scheduleTitle("Test Schedule")
                                .build();

                when(_scheduleRepository.findById(eq(scheduleId))).thenReturn(Optional.of(schedule));

                // when
                Result<DrinkingScheduleModel> result = scheduleService.getSchedule(scheduleId);

                // then
                assertThat(result.isSuccess()).isTrue();
                assertThat(result.getValue()).isPresent();
                assertThat(result.getValue().get().getId()).isEqualTo(scheduleId);
        }

        @Test
        @DisplayName("존재하지 않는 일정 조회 시 실패한다 (404)")
        void getSchedule_NotFound_Fail() {
                // given
                Long scheduleId = 999L;
                when(_scheduleRepository.findById(eq(scheduleId))).thenReturn(Optional.empty());

                // when
                Result<DrinkingScheduleModel> result = scheduleService.getSchedule(scheduleId);

                // then
                assertThat(result.isFailure()).isTrue();
                assertThat(result.getErrors()).hasSize(1);
                assertThat(result.getErrors().get(0).getCode()).isEqualTo(404);
                assertThat(result.getErrors().get(0).getMessage()).contains("일정을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("기간별 일정 조회가 성공한다")
        void getSchedulesInPeriod_Success() {
                // given
                Long userId = 1L;
                LocalDateTime start = LocalDateTime.of(2024, 12, 1, 0, 0);
                LocalDateTime end = LocalDateTime.of(2024, 12, 31, 23, 59);

                GetSchedulesInPeriodQuery query = new GetSchedulesInPeriodQuery(userId, start, end);

                List<DrinkingScheduleModel> schedules = Arrays.asList(
                                DrinkingScheduleModel.builder().id(1L).scheduleTitle("Schedule 1").build(),
                                DrinkingScheduleModel.builder().id(2L).scheduleTitle("Schedule 2").build());

                when(_scheduleRepository.findByPeriod(eq(userId), eq(start), eq(end))).thenReturn(schedules);

                // when
                Result<List<DrinkingScheduleModel>> result = scheduleService.getSchedulesInPeriod(query);

                // then
                assertThat(result.isSuccess()).isTrue();
                assertThat(result.getValue()).isPresent();
                assertThat(result.getValue().get()).hasSize(2);
        }
}
