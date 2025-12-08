package com.ssafy.sulmap.infra.repository;

import com.ssafy.sulmap.core.model.UserModel;
import com.ssafy.sulmap.core.repository.UserRepository;
import java.util.Optional;

import com.ssafy.sulmap.infra.mapper.UserMapper;
import com.ssafy.sulmap.infra.model.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserMapper userMapper;

    @Override
    @Transactional
    public Long save(UserModel user) {
        var entity = UserEntity.fromUserModel(user);
        if(user.getId()==null){
            userMapper.insert(entity);
        }
        else{
            userMapper.update(entity);
        }
        return entity.getId();
    }

    @Override
    public Optional<UserModel> findById(long userId) {
        UserEntity entity = userMapper.selectById(userId);
        return Optional.ofNullable(entity).map(UserEntity::toUserModel);
    }

    @Override
    public Optional<UserModel> findByLoginId(String userLoginId) {
        UserEntity entity = userMapper.selectByLoginId(userLoginId);
        return Optional.ofNullable(entity).map(UserEntity::toUserModel);
    }
}
