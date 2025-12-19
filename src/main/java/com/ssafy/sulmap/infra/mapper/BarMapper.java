package com.ssafy.sulmap.infra.mapper;

import com.ssafy.sulmap.infra.model.BarEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface BarMapper {
    Optional<BarEntity> selectById(@Param("id") long id);
}
