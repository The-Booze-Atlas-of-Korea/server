package com.ssafy.sulmap.core.service.impl;


import com.ssafy.sulmap.core.model.enums.ScheduleStatus;
import com.ssafy.sulmap.core.model.DrinkingScheduleModel;
import com.ssafy.sulmap.core.model.command.CreateScheduleCommand;
import com.ssafy.sulmap.core.model.command.UpdateScheduleCommand;
import com.ssafy.sulmap.core.repository.ScheduleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceImplTest {

    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Test
    @DisplayName("일정 생성 성공")
    void createSchedule_Success() {
        // given
        CreateScheduleCommand command = new CreateScheduleCommand(
                1L, null, "My Schedule", LocalDateTime.now().plusDays(1));
        DrinkingScheduleModel savedSchedule = DrinkingScheduleModel.builder()
                .id(10L)
                .scheduleTitle("My Schedule")
                .status(ScheduleStatus.PLANNED)
                .build();

        given(scheduleRepository.save(any(DrinkingScheduleModel.class))).willReturn(savedSchedule);

        // when
        DrinkingScheduleModel result = scheduleService.createSchedule(command);

        // then
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getStatus()).isEqualTo(ScheduleStatus.PLANNED);
    }

    @Test
    @DisplayName("일정 수정 성공")
    void updateSchedule_Success() {
        // given
        Long scheduleId = 10L;
        Long userId = 1L;
        LocalDateTime newTime = LocalDateTime.now().plusDays(2);

        UpdateScheduleCommand command = new UpdateScheduleCommand(
                scheduleId, userId, "Updated Title", newTime, ScheduleStatus.COMPLETED);

        DrinkingScheduleModel existingSchedule = DrinkingScheduleModel.builder()
                .id(scheduleId)
                .ownerUserId(userId)
                .scheduleTitle("Old Title")
                .meetAt(LocalDateTime.now())
                .status(ScheduleStatus.PLANNED)
                .build();

        given(scheduleRepository.findById(scheduleId)).willReturn(Optional.of(existingSchedule));
        given(scheduleRepository.save(any(DrinkingScheduleModel.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        DrinkingScheduleModel result = scheduleService.updateSchedule(command);

        // then
        assertThat(result.getScheduleTitle()).isEqualTo("Updated Title");
        assertThat(result.getStatus()).isEqualTo(ScheduleStatus.COMPLETED);
    }
}
