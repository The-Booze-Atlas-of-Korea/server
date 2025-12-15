package com.ssafy.sulmap.core.service.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ssafy.sulmap.core.model.DrinkingPlanModel;
import com.ssafy.sulmap.core.model.command.CreatePlanCommand;
import com.ssafy.sulmap.core.model.enums.PlanTheme;
import com.ssafy.sulmap.core.repository.PlanRepository;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("PlanServiceImpl 유닛 테스트")
@ExtendWith(MockitoExtension.class)
public class PlanServiceImplTest {
        @InjectMocks
        private PlanServiceImpl planService;

        @Mock
        private PlanRepository planRepository;

        @Test
        @DisplayName("플랜 생성 성공")
        void createPlan_Success() {
            // given
            CreatePlanCommand command = new CreatePlanCommand(
                    1L, "Test Plan", "Desc", PlanTheme.FRIEND, 50000L, Collections.emptyList());
            DrinkingPlanModel savedPlan = DrinkingPlanModel.builder()
                    .id(100L)
                    .ownerUserId(1L)
                    .title("Test Plan")
                    .build();

            given(planRepository.save(any(DrinkingPlanModel.class))).willReturn(savedPlan);

            // when
            DrinkingPlanModel result = planService.createPlan(command);

            // then
            assertThat(result.getId()).isEqualTo(100L);
            assertThat(result.getTitle()).isEqualTo("Test Plan");
            verify(planRepository).save(any(DrinkingPlanModel.class));
        }
}


