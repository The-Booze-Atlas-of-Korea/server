package com.ssafy.sulmap.infra.mapper;

import com.ssafy.sulmap.infra.model.MemoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 메모 MyBatis Mapper
 */
@Mapper
public interface MemoMapper {
    int upsert(MemoEntity entity);

    MemoEntity selectByUserIdAndBarId(
            @Param("userId") Long userId,
            @Param("barId") Long barId);
}
