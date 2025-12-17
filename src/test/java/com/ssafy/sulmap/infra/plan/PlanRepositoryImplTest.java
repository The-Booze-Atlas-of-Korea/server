package com.ssafy.sulmap.infra.plan;

import com.ssafy.sulmap.core.model.enums.PlanTheme;
import com.ssafy.sulmap.core.model.DrinkingPlanModel;
import com.ssafy.sulmap.core.model.DrinkingPlanSpotModel;
import com.ssafy.sulmap.infra.mapper.PlanMapper;
import com.ssafy.sulmap.infra.model.DrinkingPlanEntity;
import com.ssafy.sulmap.infra.model.DrinkingPlanSpotEntity;
import com.ssafy.sulmap.infra.repository.PlanRepositoryImpl;
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
public class PlanRepositoryImplTest {

    @Mock
    private PlanMapper _planMapper;

    @InjectMocks
    private PlanRepositoryImpl planRepository;

    @Test
    @DisplayName("신규 플랜 저장 시 ID가 자동 생성되고, 장소 목록도 함께 저장된다")
    void save_shouldInsertPlanAndSpotsAndReturnWithId() {
        // given: 신규 플랜 (id == null)
        DrinkingPlanSpotModel spot1 = DrinkingPlanSpotModel.builder()
                .placeId(100L)
                .placeNameSnapshot("1차 삼겹살집")
                .placeAddressSnapshot("서울시 강남구")
                .latitude(37.5)
                .longitude(127.0)
                .sequence(1)
                .memo("여기 맛있음")
                .build();

        DrinkingPlanSpotModel spot2 = DrinkingPlanSpotModel.builder()
                .placeId(101L)
                .placeNameSnapshot("2차 펍")
                .placeAddressSnapshot("서울시 강남구")
                .latitude(37.51)
                .longitude(127.01)
                .sequence(2)
                .build();

        DrinkingPlanModel newPlan = DrinkingPlanModel.builder()
                .ownerUserId(1L)
                .title("연말 회식 플랜")
                .description("2024 연말 회식")
                .theme(PlanTheme.COMPANY)
                .totalBudget(100000L)
                .spots(Arrays.asList(spot1, spot2))
                .build();

        // Mock: insertPlan 시 ID 세팅
        doAnswer(invocation -> {
            DrinkingPlanEntity entity = invocation.getArgument(0);
            entity.setId(10L); // PK 자동 생성 시뮬레이션
            return 1;
        }).when(_planMapper).insertPlan(any(DrinkingPlanEntity.class));

        // Mock: insertPlanSpot 시 ID 세팅
        doAnswer(invocation -> {
            DrinkingPlanSpotEntity spotEntity = invocation.getArgument(0);
            if (spotEntity.getSequence() == 1) {
                spotEntity.setId(100L);
            } else if (spotEntity.getSequence() == 2) {
                spotEntity.setId(101L);
            }
            return 1;
        }).when(_planMapper).insertPlanSpot(any(DrinkingPlanSpotEntity.class));

        // when
        DrinkingPlanModel savedPlan = planRepository.save(newPlan);

        // then
        verify(_planMapper).insertPlan(any(DrinkingPlanEntity.class));
        verify(_planMapper, times(2)).insertPlanSpot(any(DrinkingPlanSpotEntity.class));

        assertThat(savedPlan.getId()).isEqualTo(10L);
        assertThat(savedPlan.getTitle()).isEqualTo("연말 회식 플랜");
        assertThat(savedPlan.getSpots()).hasSize(2);
        assertThat(savedPlan.getSpots().get(0).getId()).isEqualTo(100L);
        assertThat(savedPlan.getSpots().get(1).getId()).isEqualTo(101L);
    }

    @Test
    @DisplayName("기존 플랜 수정 시 장소 목록이 전체 재구성된다")
    void save_shouldUpdatePlanAndReplaceSpots() {
        // given: 기존 플랜 (id != null)
        DrinkingPlanSpotModel newSpot = DrinkingPlanSpotModel.builder()
                .placeNameSnapshot("새로운 1차")
                .sequence(1)
                .build();

        DrinkingPlanModel existingPlan = DrinkingPlanModel.builder()
                .id(5L)
                .ownerUserId(1L)
                .title("수정된 플랜")
                .description("변경됨")
                .theme(PlanTheme.FRIEND)
                .totalBudget(50000L)
                .spots(Arrays.asList(newSpot))
                .build();

        when(_planMapper.updatePlan(any(DrinkingPlanEntity.class))).thenReturn(1);
        when(_planMapper.deleteSpotsByPlanId(eq(5L))).thenReturn(2); // 기존 2개 삭제

        doAnswer(invocation -> {
            DrinkingPlanSpotEntity spotEntity = invocation.getArgument(0);
            spotEntity.setId(200L);
            return 1;
        }).when(_planMapper).insertPlanSpot(any(DrinkingPlanSpotEntity.class));

        // when
        DrinkingPlanModel updated = planRepository.save(existingPlan);

        // then
        verify(_planMapper).updatePlan(any(DrinkingPlanEntity.class));
        verify(_planMapper).deleteSpotsByPlanId(5L); // 기존 장소 삭제
        verify(_planMapper, times(1)).insertPlanSpot(any(DrinkingPlanSpotEntity.class)); // 새 장소 삽입

        assertThat(updated.getId()).isEqualTo(5L);
        assertThat(updated.getTitle()).isEqualTo("수정된 플랜");
        assertThat(updated.getSpots()).hasSize(1);
        assertThat(updated.getSpots().get(0).getId()).isEqualTo(200L);
    }

    @Test
    @DisplayName("플랜 조회 시 장소 목록도 함께 조회된다")
    void findById_shouldReturnPlanWithSpots() {
        // given
        DrinkingPlanEntity planEntity = DrinkingPlanEntity.builder()
                .id(10L)
                .ownerUserId(1L)
                .title("테스트 플랜")
                .description("설명")
                .theme("FRIEND")
                .totalBudget(50000L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<DrinkingPlanSpotEntity> spotEntities = Arrays.asList(
                DrinkingPlanSpotEntity.builder()
                        .id(100L)
                        .planId(10L)
                        .placeNameSnapshot("1차")
                        .sequence(1)
                        .build(),
                DrinkingPlanSpotEntity.builder()
                        .id(101L)
                        .planId(10L)
                        .placeNameSnapshot("2차")
                        .sequence(2)
                        .build());

        when(_planMapper.selectById(eq(10L))).thenReturn(planEntity);
        when(_planMapper.selectSpotsByPlanId(eq(10L))).thenReturn(spotEntities);

        // when
        Optional<DrinkingPlanModel> result = planRepository.findById(10L);

        // then
        assertThat(result).isPresent();
        DrinkingPlanModel plan = result.get();
        assertThat(plan.getId()).isEqualTo(10L);
        assertThat(plan.getTitle()).isEqualTo("테스트 플랜");
        assertThat(plan.getSpots()).hasSize(2);
        assertThat(plan.getSpots().get(0).getPlaceNameSnapshot()).isEqualTo("1차");
        assertThat(plan.getSpots().get(1).getPlaceNameSnapshot()).isEqualTo("2차");
    }

    @Test
    @DisplayName("존재하지 않는 플랜 조회 시 Optional.empty()를 반환한다")
    void findById_whenNotExists_shouldReturnEmpty() {
        // given
        when(_planMapper.selectById(eq(999L))).thenReturn(null);

        // when
        Optional<DrinkingPlanModel> result = planRepository.findById(999L);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("플랜 삭제 시 관련 장소도 함께 삭제된다")
    void delete_shouldRemovePlanAndSpots() {
        // given
        when(_planMapper.deleteSpotsByPlanId(eq(10L))).thenReturn(2);
        when(_planMapper.deletePlan(eq(10L))).thenReturn(1);

        // when
        planRepository.delete(10L);

        // then
        verify(_planMapper).deleteSpotsByPlanId(10L); // 장소 먼저 삭제
        verify(_planMapper).deletePlan(10L); // 플랜 삭제
    }
}
