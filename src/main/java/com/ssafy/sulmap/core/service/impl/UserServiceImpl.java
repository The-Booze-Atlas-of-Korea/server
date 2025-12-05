package com.ssafy.sulmap.core.service.impl;

import com.ssafy.sulmap.core.model.command.CreateUserCommand;
import com.ssafy.sulmap.core.model.command.LoginUserCommand;
import com.ssafy.sulmap.core.model.command.UpdateUserProfileCommand;
import com.ssafy.sulmap.core.model.UserModel;
import com.ssafy.sulmap.core.model.enums.UserProfileVisitVisibility;
import com.ssafy.sulmap.core.model.enums.UserStatus;
import com.ssafy.sulmap.core.repository.UserRepository;
import com.ssafy.sulmap.core.service.UserService;
import com.ssafy.sulmap.share.result.Result;
import com.ssafy.sulmap.share.result.error.impl.ConflictError;
import com.ssafy.sulmap.share.result.error.impl.NotFoundError;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository _userRepository;
    private final PasswordEncoder _passwordEncoder;

    @Override
    public Result<Long> CreateUser(CreateUserCommand command) {
        var userOpt = _userRepository.findByLoginId(command.getLoginId());
        if (userOpt.isPresent()) {
            return Result.fail(new ConflictError("로그인 아이디가 이미 존재합니다."));
        }

        var userModel = UserModel.builder()
                .loginId(command.getLoginId())
                .passwordHash(_passwordEncoder.encode(command.getPassword()))
                .name(command.getName())
                .email(command.getEmail())
                .phone(command.getPhone())
                .address(command.getAddress())
                .birthday(command.getBirthday())
                .gender(command.getGender())
                .build();

        var result = _userRepository.save(userModel);

        return Result.ok(result);
    }

    @Override
    public Result<Long> updateUserProfile(UpdateUserProfileCommand command) {
        var userOpt = _userRepository.findById(command.getUserId());
        if (userOpt.isEmpty()) {
            return Result.fail(new NotFoundError("userId", command.getUserId()));
        }

        var userModel = userOpt.get();
        userModel.setName(command.getName());
        userModel.setEmail(command.getEmail());
        userModel.setAddress(command.getAddress());
        userModel.setGender(command.getGender());
        userModel.setProfileImageUrl(command.getProfileImageUrl());
        userModel.setBirthday(command.getBirthday());
        userModel.setGender(command.getGender());
        userModel.setPhone(command.getPhone());

        var result = _userRepository.save(userModel);
        return Result.ok(result);
    }

    @Override
    public Result<Long> LoginUser(LoginUserCommand command) {
        var userOpt = _userRepository.findByLoginId(command.getLoginId());
        if (userOpt.isEmpty()) {
            return Result.fail(new NotFoundError("loginId", command.getLoginId()));
        }

        var userModel = userOpt.get();
        if(!_passwordEncoder.matches(command.getPassword(), userModel.getPasswordHash())){
            return Result.fail(new NotFoundError("password", command.getPassword()));
        }

        userModel.setLastLoginAt(new Date());
        var result = _userRepository.save(userModel);

        return Result.ok(result);
    }

    @Override
    public Result<Long> softDeleteUser(long userId) {
        var userOpt = _userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return Result.fail(new NotFoundError("userId", userId));
        }

        var userModel = userOpt.get();
        userModel.setStatus(UserStatus.WITHDRAWN);
        userModel.setDeletedAt(new Date());

        var result = _userRepository.save(userModel);
        return Result.ok(result);
    }

    @Override
    public Result<Long> updateUserProfileVisitVisibility(long userId, UserProfileVisitVisibility visibility) {
        var userOpt = _userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return Result.fail(new NotFoundError("userId", userId));
        }

        var userModel = userOpt.get();
        userModel.setVisitVisibilitySetting(visibility);

        var result = _userRepository.save(userModel);
        return Result.ok(result);
    }

    @Override
    public Result<UserModel> findUserByLoginId(String userLoginId) {
        var userOpt = _userRepository.findByLoginId(userLoginId);
        return userOpt.map(Result::ok).orElseGet(() -> Result.fail(new NotFoundError("userLoginId", userLoginId)));

    }

    @Override
    public Result<UserModel> findUserById(Long userId) {
        var userOpt = _userRepository.findById(userId);
        return userOpt.map(Result::ok).orElseGet(() -> Result.fail(new NotFoundError("userId", userId)));
    }
}
