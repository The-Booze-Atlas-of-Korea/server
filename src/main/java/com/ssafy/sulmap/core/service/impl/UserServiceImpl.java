package com.ssafy.sulmap.core.service.impl;

import com.ssafy.sulmap.core.command.CreateUserCommand;
import com.ssafy.sulmap.core.command.UpdateUserCommand;
import com.ssafy.sulmap.core.model.MemberDrinkHistoryOpen;
import com.ssafy.sulmap.core.model.UserModel;
import com.ssafy.sulmap.core.model.UserUpdateModel;
import com.ssafy.sulmap.core.repository.UserRepository;
import com.ssafy.sulmap.core.service.UserService;
import com.ssafy.sulmap.share.result.Result;
import com.ssafy.sulmap.share.result.error.impl.ConflictError;
import com.ssafy.sulmap.share.result.error.impl.NotFoundError;
import com.ssafy.sulmap.share.result.error.impl.ServerError;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Result<Long> registerUser(UserModel userModel) {
        //id 중복체크
        var findResult = userRepository.findByLoginId(userModel.getLoginId());
        if(findResult != null) {
            return Result.fail(new ConflictError("이미 존재하는 로그인 아이디"));
        }

        var command = CreateUserCommand.builder()
                .loginId(userModel.getLoginId())
                .passwordHash(passwordEncoder.encode(userModel.getPassword()))
                .name(userModel.getName())
                .email(userModel.getEmail())
                .phone(userModel.getPhone())
                .gender(userModel.getGender())
                .address(userModel.getAddress())
                .birthday(userModel.getBirth())
                .build();

        var createResult = userRepository.create(command);
        if(createResult == null) {
            return Result.fail(new ServerError("userRepository.create 실패", command));
        }

        return Result.ok(createResult);
    }

    @Override
    public Result<Long> updateUser(long userId, UserUpdateModel userUpdateModel) {
        //id 존재 여부 체크
        var findResult = userRepository.findById(userId);
        if(findResult == null) {
            return Result.fail(new NotFoundError("userId", userId));
        }

        var userUpdateCommand = UpdateUserCommand.builder()
                .name(userUpdateModel.getName())
                .phone(userUpdateModel.getPhone())
                .email(userUpdateModel.getEmail())
                .birthday(userUpdateModel.getBirth())
                .address(userUpdateModel.getAddress())
                .gender(userUpdateModel.getGender())
                .build();

        var updateResult = userRepository.update(userUpdateCommand);
        if(updateResult == null) {
            return Result.fail(new ServerError("userRepository.update 실패", userUpdateCommand));
        }

        return Result.ok(updateResult);
    }

    @Override
    public Result deleteUser(long userId) {
        return null;
    }

    @Override
    public Result<Long> updateUserDrinkHistory(long userId, MemberDrinkHistoryOpen historyOpen) {
        return null;
    }

    @Override
    public Result<UserModel> findUserById(String userId) {
        return null;
    }
}
