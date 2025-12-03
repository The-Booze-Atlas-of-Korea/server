package com.ssafy.sulmap.infra.repository;

import com.ssafy.sulmap.core.command.CreatUserCommand;
import com.ssafy.sulmap.core.command.UpdateUserCommand;
import com.ssafy.sulmap.core.query.FindUserResult;
import com.ssafy.sulmap.core.repository.UserRepository;
import com.ssafy.sulmap.infra.mapper.UserMapper;
import com.ssafy.sulmap.infra.model.UserEntity;
import com.ssafy.sulmap.share.result.error.exception.ResultException;
import com.ssafy.sulmap.share.result.error.impl.ValidationError;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    @Override
    @Transactional
    public Long create(CreatUserCommand createUserCommand) {
        UserEntity entity = toEntity(createUserCommand);
        userMapper.insert(entity);
        return entity.getId();
    }

    @Override
    @Transactional
    public Long update(UpdateUserCommand updateUserCommand) {
        if (updateUserCommand.getId() == null) {
            throw new ResultException(
                    List.of(new ValidationError("User id is required for update"))
            );
        }
        UserEntity entity = toEntity(updateUserCommand);
        int updated = userMapper.update(entity);
        return updated > 0 ? updateUserCommand.getId() : null;
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        return userMapper.softDelete(id) > 0;
    }

    @Override
    public FindUserResult findById(long id) {
        UserEntity entity = userMapper.selectById(id);
        return toResult(entity);
    }

    @Override
    public FindUserResult findByLoginId(long id) {
        return null;
    }
    private UserEntity toEntity(CreatUserCommand command) {
        Date now = new Date();
        return UserEntity.builder()
                .loginId(command.getLoginId())
                .passwordHash(command.getPasswordHash())
                .name(command.getName())
                .email(command.getEmail())
                .phone(command.getPhone())
                .address(command.getAddress())
                .birthday(command.getBirthday())
                .gender(command.getGender())
                .profileImageUrl(null)
                .authProvider("LOCAL")
                .status("ACTIVE")
                .visitVisibilitySetting("PRIVATE")
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private UserEntity toEntity(UpdateUserCommand command) {
        return UserEntity.builder()
                .id(command.getId())
                .name(command.getName())
                .email(command.getEmail())
                .phone(command.getPhone())
                .address(command.getAddress())
                .birthday(command.getBirthday())
                .gender(command.getGender())
                .updatedAt(new Date())
                .build();
    }

    private FindUserResult toResult(UserEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }
        return FindUserResult.builder()
                .loginId(entity.getLoginId())
                .passwordHash(entity.getPasswordHash())
                .name(entity.getName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .address(entity.getAddress())
                .birthday(entity.getBirthday())
                .gender(entity.getGender())
                .profileImageUrl(entity.getProfileImageUrl())
                .status(entity.getStatus())
                .visit_visibility(entity.getVisitVisibilitySetting())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .lastLogin(entity.getLastLoginAt())
                .build();
    }
}
