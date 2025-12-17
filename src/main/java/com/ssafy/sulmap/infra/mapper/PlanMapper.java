package com.ssafy.sulmap.infra.mapper;

import com.ssafy.sulmap.infra.model.DrinkingPlanEntity;
import com.ssafy.sulmap.infra.model.DrinkingPlanSpotEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlanMapper {
    /**
     * 플랜 삽입 (PK 자동 생성)
     */
    int insertPlan(DrinkingPlanEntity entity);

    /**
     * 플랜 스팟 삽입 (PK 자동 생성)
     */
    int insertPlanSpot(DrinkingPlanSpotEntity spot);

    /**
     * 플랜 단건 조회
     */
    DrinkingPlanEntity selectById(@Param("id") Long id);

    /**
     * 특정 플랜의 스팟 목록 조회
     */
    List<DrinkingPlanSpotEntity> selectSpotsByPlanId(@Param("planId") Long planId);

    /**
     * 플랜 수정
     */
    int updatePlan(DrinkingPlanEntity entity);

    /**
     * 특정 플랜의 모든 스팟 삭제 (플랜 수정 시 스팟 재구성용)
     */
    int deleteSpotsByPlanId(@Param("planId") Long planId);

    /**
     * 플랜 삭제
     */
    int deletePlan(@Param("id") Long id);
}
