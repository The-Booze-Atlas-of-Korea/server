package com.ssafy.sulmap.infra.mapper;

import com.ssafy.sulmap.infra.model.UserEntity;
import java.util.Date;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    UserEntity selectById(@Param("id") Long id);
    UserEntity selectByLoginId(@Param("loginId") String loginId);
    int insert(UserEntity entity);
    int update(UserEntity entity);
}
