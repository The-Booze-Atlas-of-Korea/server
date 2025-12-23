package com.ssafy.sulmap.core.service;

import com.ssafy.sulmap.core.model.DrinkingScheduleModel;
import com.ssafy.sulmap.core.model.command.CreateScheduleCommand;
import com.ssafy.sulmap.core.model.command.UpdateScheduleCommand;
import com.ssafy.sulmap.core.model.query.GetSchedulesInPeriodQuery;
import com.ssafy.sulmap.share.result.Result;

import java.util.List;

public interface ScheduleService {
    Result<DrinkingScheduleModel> createSchedule(CreateScheduleCommand command);

    Result<DrinkingScheduleModel> updateSchedule(UpdateScheduleCommand command);

    Result<DrinkingScheduleModel> getSchedule(Long scheduleId);

    Result<List<DrinkingScheduleModel>> getSchedulesInPeriod(GetSchedulesInPeriodQuery query);

    Result<List<DrinkingScheduleModel>> getScheduleHistory(Long userId, int page, int size);
}