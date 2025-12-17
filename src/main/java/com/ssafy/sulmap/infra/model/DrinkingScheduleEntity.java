package com.ssafy.sulmap.infra.model;

import com.ssafy.sulmap.core.model.enums.ScheduleStatus;
import com.ssafy.sulmap.core.model.DrinkingScheduleModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrinkingScheduleEntity {
    private Long id;
    private Long ownerUserId;
    private Long planId; // Nullable
    private String scheduleTitle;
    private LocalDateTime meetAt;
    private String status; // DB에는 String으로 저장
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Core Model을 Entity로 변환
     */
    public static DrinkingScheduleEntity fromModel(DrinkingScheduleModel model) {
        return DrinkingScheduleEntity.builder()
                .id(model.getId())
                .ownerUserId(model.getOwnerUserId())
                .planId(model.getPlanId())
                .scheduleTitle(model.getScheduleTitle())
                .meetAt(model.getMeetAt())
                .status(model.getStatus() != null ? model.getStatus().name() : null)
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }

    /**
     * Entity를 Core Model로 변환
     */
    public DrinkingScheduleModel toModel() {
        return DrinkingScheduleModel.builder()
                .id(this.id)
                .ownerUserId(this.ownerUserId)
                .planId(this.planId)
                .scheduleTitle(this.scheduleTitle)
                .meetAt(this.meetAt)
                .status(this.status != null ? ScheduleStatus.fromString(this.status) : null)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}
