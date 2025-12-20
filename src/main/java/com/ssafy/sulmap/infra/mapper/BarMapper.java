package com.ssafy.sulmap.infra.mapper;

import com.ssafy.sulmap.infra.model.BarCategoryEntity;
import com.ssafy.sulmap.infra.model.BarEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BarMapper {
    Optional<BarEntity> selectById(@Param("id") long id);
    List<BarCategoryEntity> selectBarCategory(@Param("bar_id") long barId);
}
