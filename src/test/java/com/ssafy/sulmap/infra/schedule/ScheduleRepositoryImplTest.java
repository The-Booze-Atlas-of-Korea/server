package com.ssafy.sulmap.infra.schedule;

import com.ssafy.sulmap.core.model.enums.ScheduleStatus;
import com.ssafy.sulmap.core.model.DrinkingScheduleModel;
import com.ssafy.sulmap.infra.mapper.ScheduleMapper;
import com.ssafy.sulmap.infra.model.DrinkingScheduleEntity;
import com.ssafy.sulmap.infra.repository.ScheduleRepositoryImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleRepositoryImplTest {

    @Mock
    private ScheduleMapper _scheduleMapper;

    @InjectMocks
    private ScheduleRepositoryImpl scheduleRepository;

    @Test
    @DisplayName("신규 일정 저장 시 ID가 자동 생성된다")
    void save_shouldInsertScheduleAndReturnWithId() {
        // given: 신규 일정 (id == null)
        LocalDateTime meetAt = LocalDateTime.of(2024, 12, 25, 18, 0);
        DrinkingScheduleModel newSchedule = DrinkingScheduleModel.builder()
                .ownerUserId(1L)
                .planId(10L)
                .scheduleTitle("크리스마스 모임")
                .meetAt(meetAt)
                .status(ScheduleStatus.PLANNED)
                .build();

        // Mock: insert 시 ID 세팅
        doAnswer(invocation -> {
            DrinkingScheduleEntity entity = invocation.getArgument(0);
            entity.setId(100L); // PK 자동 생성 시뮬레이션
            return 1;
        }).when(_scheduleMapper).insert(any(DrinkingScheduleEntity.class));

        // when
        DrinkingScheduleModel savedSchedule = scheduleRepository.save(newSchedule);

        // then
        verify(_scheduleMapper).insert(any(DrinkingScheduleEntity.class));

        assertThat(savedSchedule.getId()).isEqualTo(100L);
        assertThat(savedSchedule.getScheduleTitle()).isEqualTo("크리스마스 모임");
        assertThat(savedSchedule.getMeetAt()).isEqualTo(meetAt);
        assertThat(savedSchedule.getStatus()).isEqualTo(ScheduleStatus.PLANNED);
    }

    @Test
    @DisplayName("기존 일정 수정이 정상적으로 동작한다")
    void save_shouldUpdateExistingSchedule() {
        // given: 기존 일정 (id != null)
        LocalDateTime newMeetAt = LocalDateTime.of(2025, 1, 1, 20, 0);
        DrinkingScheduleModel existingSchedule = DrinkingScheduleModel.builder()
                .id(50L)
                .ownerUserId(1L)
                .planId(10L)
                .scheduleTitle("신년 모임")
                .meetAt(newMeetAt)
                .status(ScheduleStatus.COMPLETED)
                .build();

        when(_scheduleMapper.update(any(DrinkingScheduleEntity.class))).thenReturn(1);

        // when
        DrinkingScheduleModel updated = scheduleRepository.save(existingSchedule);

        // then
        verify(_scheduleMapper).update(any(DrinkingScheduleEntity.class));
        verify(_scheduleMapper, never()).insert(any(DrinkingScheduleEntity.class));

        assertThat(updated.getId()).isEqualTo(50L);
        assertThat(updated.getScheduleTitle()).isEqualTo("신년 모임");
        assertThat(updated.getStatus()).isEqualTo(ScheduleStatus.COMPLETED);
    }

    @Test
    @DisplayName("일정 단건 조회가 정상적으로 동작한다")
    void findById_shouldReturnSchedule() {
        // given
        LocalDateTime meetAt = LocalDateTime.of(2024, 12, 31, 23, 0);
        DrinkingScheduleEntity entity = DrinkingScheduleEntity.builder()
                .id(20L)
                .ownerUserId(1L)
                .planId(5L)
                .scheduleTitle("연말 파티")
                .meetAt(meetAt)
                .status("PLANNED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(_scheduleMapper.selectById(eq(20L))).thenReturn(entity);

        // when
        Optional<DrinkingScheduleModel> result = scheduleRepository.findById(20L);

        // then
        assertThat(result).isPresent();
        DrinkingScheduleModel schedule = result.get();
        assertThat(schedule.getId()).isEqualTo(20L);
        assertThat(schedule.getScheduleTitle()).isEqualTo("연말 파티");
        assertThat(schedule.getMeetAt()).isEqualTo(meetAt);
        assertThat(schedule.getStatus()).isEqualTo(ScheduleStatus.PLANNED);
    }

    @Test
    @DisplayName("기간별 일정 조회 시 범위 내 일정만 반환된다")
    void findByPeriod_shouldReturnSchedulesInRange() {
        // given
        LocalDateTime start = LocalDateTime.of(2024, 12, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 12, 31, 23, 59);

        List<DrinkingScheduleEntity> entities = Arrays.asList(
                DrinkingScheduleEntity.builder()
                        .id(1L)
                        .ownerUserId(1L)
                        .scheduleTitle("12월 첫째주")
                        .meetAt(LocalDateTime.of(2024, 12, 5, 18, 0))
                        .status("PLANNED")
                        .build(),
                DrinkingScheduleEntity.builder()
                        .id(2L)
                        .ownerUserId(1L)
                        .scheduleTitle("12월 넷째주")
                        .meetAt(LocalDateTime.of(2024, 12, 25, 19, 0))
                        .status("COMPLETED")
                        .build());

        when(_scheduleMapper.selectByPeriod(eq(1L), eq(start), eq(end))).thenReturn(entities);

        // when
        List<DrinkingScheduleModel> schedules = scheduleRepository.findByPeriod(1L, start, end);

        // then
        verify(_scheduleMapper).selectByPeriod(1L, start, end);

        assertThat(schedules).hasSize(2);
        assertThat(schedules.get(0).getScheduleTitle()).isEqualTo("12월 첫째주");
        assertThat(schedules.get(1).getScheduleTitle()).isEqualTo("12월 넷째주");
        assertThat(schedules.get(1).getStatus()).isEqualTo(ScheduleStatus.COMPLETED);
    }

    @Test
    @DisplayName("해당 기간에 일정이 없을 때 빈 리스트를 반환한다")
    void findByPeriod_whenNoSchedules_shouldReturnEmptyList() {
        // given
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 31, 23, 59);

        when(_scheduleMapper.selectByPeriod(eq(1L), eq(start), eq(end))).thenReturn(Arrays.asList());

        // when
        List<DrinkingScheduleModel> schedules = scheduleRepository.findByPeriod(1L, start, end);

        // then
        assertThat(schedules).isEmpty();
    }
}
