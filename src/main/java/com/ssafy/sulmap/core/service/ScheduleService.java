package com.ssafy.sulmap.core.service;

import com.ssafy.sulmap.core.model.DrinkingScheduleModel;
import com.ssafy.sulmap.core.model.command.CreateScheduleCommand;
import com.ssafy.sulmap.core.model.query.GetSchedulesInPeriodQuery;
import java.util.List;

public interface ScheduleService {
    DrinkingScheduleModel createSchedule(CreateScheduleCommand command);
    DrinkingScheduleModel updateSchedule(CreateScheduleCommand command);
    DrinkingScheduleModel getSchedule(Long scheduleId);
    List<DrinkingScheduleModel> getSchedulesInPeriod(GetSchedulesInPeriodQuery query);
}
