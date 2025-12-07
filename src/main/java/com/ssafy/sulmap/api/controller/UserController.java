package com.ssafy.sulmap.api.controller;

import com.ssafy.sulmap.api.dto.LoginUserDetail;
import com.ssafy.sulmap.api.dto.request.UpdateUserRequest;
import com.ssafy.sulmap.api.dto.response.GetUserResponse;
import com.ssafy.sulmap.core.model.command.UpdateUserProfileCommand;
import com.ssafy.sulmap.core.model.enums.UserGender;
import com.ssafy.sulmap.core.model.enums.UserStatus;
import com.ssafy.sulmap.core.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("users")
public class UserController {
    private final UserService userService;

    @GetMapping("me")
    public ResponseEntity<?> getUser(@AuthenticationPrincipal LoginUserDetail userDetail) throws Exception
    {
        return ResponseEntity.ok(GetUserResponse.fromModel(userDetail.getUserModel()));
    }

    @PatchMapping("me")
    public ResponseEntity<?> updateUserProfile(@Valid UpdateUserRequest updateUserRequest,
                                        @AuthenticationPrincipal LoginUserDetail userDetail) throws Exception
    {
        var userId = userDetail.getUserModel().getId();
        var command = UpdateUserProfileCommand.builder()
                .userId(userId)
                .name(updateUserRequest.name())
                .email(updateUserRequest.email())
                .phone(updateUserRequest.phone())
                .address(updateUserRequest.address())
                .birthday(updateUserRequest.birthday())
                .gender(UserGender.fromString(updateUserRequest.gender()))
                .profileImageUrl(updateUserRequest.profileImageUrl())
                .build();

        var updateResult = userService.updateUserProfile(command);
        if(updateResult.isFailure()){
            return new ResponseEntity<>(updateResult.getSingleErrorOrThrow().getStatus());
        }

        return ResponseEntity.ok(updateResult.getOrThrow());
    }

    @DeleteMapping("me")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal LoginUserDetail userDetail) throws Exception
    {
        var userId = userDetail.getUserModel().getId();
        var deleteResult = userService.softDeleteUser(userId);

        if(deleteResult.isFailure()){
            return new ResponseEntity<>(deleteResult.getSingleErrorOrThrow().getStatus());
        }

        return ResponseEntity.ok(deleteResult.getOrThrow());
    }

    @GetMapping("user/{userid}")
    public ResponseEntity<?> getUserByUserId(@PathVariable("userid") Long UserId) throws Exception
    {
        var result = userService.findUserById(UserId);
        if(result.isFailure()){
            return new ResponseEntity<>(result.getSingleErrorOrThrow().getStatus());
        }
        var userModel = result.getOrThrow();
        var response = GetUserResponse.fromModel(userModel);
        return ResponseEntity.ok(response);
    }
}
