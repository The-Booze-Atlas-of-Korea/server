package com.ssafy.sulmap.core.service.impl;

import com.ssafy.sulmap.core.model.DrinkingPlanModel;
import com.ssafy.sulmap.core.model.DrinkingPlanSpotModel;
import com.ssafy.sulmap.core.model.command.CreatePlanCommand;
import com.ssafy.sulmap.core.model.command.UpdatePlanCommand;
import com.ssafy.sulmap.core.repository.PlanRepository;
import com.ssafy.sulmap.share.result.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("PlanServiceImpl 유닛 테스트")
@ExtendWith(MockitoExtension.class)
public class PlanServiceImplTest {
        @Mock
        private PlanRepository _planRepository;

        @InjectMocks
        private PlanServiceImpl _planService;

        @Test
        @DisplayName("플랜 생성이 성공한다")
        void createPlan_Success() {
                // given
                DrinkingPlanSpotModel spot = DrinkingPlanSpotModel.builder()
                                .placeNameSnapshot("Test Place")
                                .sequence(1)
                                .build();

                CreatePlanCommand command = new CreatePlanCommand(
                                1L, "Test Plan", "Description", "FRIEND", 50000L, Arrays.asList(spot));

                DrinkingPlanModel savedPlan = DrinkingPlanModel.builder()
                                .id(1L)
                                .ownerUserId(1L)
                                .title("Test Plan")
                                .description("Description")
                                .theme("FRIEND")
                                .totalBudget(50000L)
                                .spots(Arrays.asList(spot))
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                when(_planRepository.save(any(DrinkingPlanModel.class))).thenReturn(savedPlan);

                // when
                Result<DrinkingPlanModel> result = _planService.createPlan(command);

                // then
                assertThat(result.isSuccess()).isTrue();
                assertThat(result.getValue()).isPresent();
                assertThat(result.getValue().get().getTitle()).isEqualTo("Test Plan");
                verify(_planRepository).save(any(DrinkingPlanModel.class));
        }

        @Test
        @DisplayName("플랜 수정이 성공한다")
        void updatePlan_Success() {
                // given
                Long planId = 1L;
                Long userId = 1L;

                DrinkingPlanModel existingPlan = DrinkingPlanModel.builder()
                                .id(planId)
                                .ownerUserId(userId)
                                .title("Old Title")
                                .description("Old Desc")
                                .theme("FRIEND")
                                .totalBudget(30000L)
                                .build();

                UpdatePlanCommand command = new UpdatePlanCommand(
                                planId, userId, "New Title", "New Desc", "COMPANY", 50000L, null);

                DrinkingPlanModel updatedPlan = DrinkingPlanModel.builder()
                                .id(planId)
                                .ownerUserId(userId)
                                .title("New Title")
                                .description("New Desc")
                                .theme("COMPANY")
                                .totalBudget(50000L)
                                .build();

                when(_planRepository.findById(eq(planId))).thenReturn(Optional.of(existingPlan));
                when(_planRepository.save(any(DrinkingPlanModel.class))).thenReturn(updatedPlan);

                // when
                Result<DrinkingPlanModel> result = _planService.updatePlan(command);

                // then
                assertThat(result.isSuccess()).isTrue();
                assertThat(result.getValue()).isPresent();
                assertThat(result.getValue().get().getTitle()).isEqualTo("New Title");
        }

        @Test
        @DisplayName("존재하지 않는 플랜 수정 시 실패한다 (404)")
        void updatePlan_PlanNotFound_Fail() {
                // given
                Long planId = 999L;
                Long userId = 1L;

                UpdatePlanCommand command = new UpdatePlanCommand(
                                planId, userId, "New Title", "New Desc", "COMPANY", 50000L, null);

                when(_planRepository.findById(eq(planId))).thenReturn(Optional.empty());

                // when
                Result<DrinkingPlanModel> result = _planService.updatePlan(command);

                // then
                assertThat(result.isFailure()).isTrue();
                assertThat(result.getErrors()).hasSize(1);
                assertThat(result.getErrors().get(0).getCode()).isEqualTo(404);
                assertThat(result.getErrors().get(0).getMessage()).contains("플랜을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("권한 없는 사용자가 플랜 수정 시 실패한다 (403)")
        void updatePlan_Unauthorized_Fail() {
                // given
                Long planId = 1L;
                Long ownerId = 1L;
                Long unauthorizedUserId = 2L;

                DrinkingPlanModel existingPlan = DrinkingPlanModel.builder()
                                .id(planId)
                                .ownerUserId(ownerId)
                                .title("Old Title")
                                .build();

                UpdatePlanCommand command = new UpdatePlanCommand(
                                planId, unauthorizedUserId, "New Title", "New Desc", "COMPANY", 50000L, null);

                when(_planRepository.findById(eq(planId))).thenReturn(Optional.of(existingPlan));

                // when
                Result<DrinkingPlanModel> result = _planService.updatePlan(command);

                // then
                assertThat(result.isFailure()).isTrue();
                assertThat(result.getErrors()).hasSize(1);
                assertThat(result.getErrors().get(0).getCode()).isEqualTo(403);
                assertThat(result.getErrors().get(0).getMessage()).contains("권한이 없습니다");
        }

        @Test
        @DisplayName("플랜 조회가 성공한다")
        void getPlan_Success() {
                // given
                Long planId = 1L;
                DrinkingPlanModel plan = DrinkingPlanModel.builder()
                                .id(planId)
                                .ownerUserId(1L)
                                .title("Test Plan")
                                .build();

                when(_planRepository.findById(eq(planId))).thenReturn(Optional.of(plan));

                // when
                Result<DrinkingPlanModel> result = _planService.getPlan(planId);

                // then
                assertThat(result.isSuccess()).isTrue();
                assertThat(result.getValue()).isPresent();
                assertThat(result.getValue().get().getId()).isEqualTo(planId);
        }

        @Test
        @DisplayName("존재하지 않는 플랜 조회 시 실패한다 (404)")
        void getPlan_NotFound_Fail() {
                // given
                Long planId = 999L;
                when(_planRepository.findById(eq(planId))).thenReturn(Optional.empty());

                // when
                Result<DrinkingPlanModel> result = _planService.getPlan(planId);

                // then
                assertThat(result.isFailure()).isTrue();
                assertThat(result.getErrors()).hasSize(1);
                assertThat(result.getErrors().get(0).getCode()).isEqualTo(404);
                assertThat(result.getErrors().get(0).getMessage()).contains("플랜을 찾을 수 없습니다");
        }
}
