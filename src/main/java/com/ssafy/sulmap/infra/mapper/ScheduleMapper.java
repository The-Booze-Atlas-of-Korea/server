package com.ssafy.sulmap.infra.mapper;

import com.ssafy.sulmap.infra.model.DrinkingScheduleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ScheduleMapper {
    /**
     * 일정 삽입 (PK 자동 생성)
     */
    int insert(DrinkingScheduleEntity entity);

    /**
     * 일정 단건 조회
     */
    DrinkingScheduleEntity selectById(@Param("id") Long id);

    /**
     * 기간별 일정 조회 (캘린더용)
     */
    List<DrinkingScheduleEntity> selectByPeriod(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 일정 수정
     */
    int update(DrinkingScheduleEntity entity);
}
