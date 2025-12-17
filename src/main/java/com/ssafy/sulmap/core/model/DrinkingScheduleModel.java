package com.ssafy.sulmap.core.model;

import com.ssafy.sulmap.core.model.enums.ScheduleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrinkingScheduleModel {
    private Long id;
    private Long ownerUserId;
    private Long planId; // Nullable
    private String scheduleTitle;
    private LocalDateTime meetAt;
    private ScheduleStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void changeStatus(ScheduleStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public void reschedule(LocalDateTime newMeetAt, String newTitle) {
        this.meetAt = newMeetAt;
        if (newTitle != null && !newTitle.isBlank()) {
            this.scheduleTitle = newTitle;
        }
        this.updatedAt = LocalDateTime.now();
    }
}
