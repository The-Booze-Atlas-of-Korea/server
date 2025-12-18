package com.ssafy.sulmap.api.dto.response;

import com.ssafy.sulmap.core.model.DrinkingScheduleModel;
import com.ssafy.sulmap.core.model.enums.ScheduleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleResponse {
    private Long id;
    private Long ownerUserId;
    private Long planId;
    private String scheduleTitle;
    private LocalDateTime meetAt;
    private ScheduleStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ScheduleResponse fromModel(DrinkingScheduleModel model) {
        return ScheduleResponse.builder()
                .id(model.getId())
                .ownerUserId(model.getOwnerUserId())
                .planId(model.getPlanId())
                .scheduleTitle(model.getScheduleTitle())
                .meetAt(model.getMeetAt())
                .status(model.getStatus())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }
}
