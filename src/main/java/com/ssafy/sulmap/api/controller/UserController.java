package com.ssafy.sulmap.api.controller;

import com.ssafy.sulmap.api.security.model.UserDetail;
import com.ssafy.sulmap.api.dto.request.UpdateUserRequest;
import com.ssafy.sulmap.api.dto.response.GetUserResponse;
import com.ssafy.sulmap.core.model.command.UpdateUserProfileCommand;
import com.ssafy.sulmap.core.model.enums.UserGender;
import com.ssafy.sulmap.core.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static com.ssafy.sulmap.api.security.SecurityUtils.getrefreshedAuthentication;

@RestController
@RequiredArgsConstructor
@RequestMapping("users")
public class UserController {
    private final UserService userService;

    @GetMapping("me")
    public ResponseEntity<?> getUser(@AuthenticationPrincipal UserDetail userDetail) throws Exception
    {
        return ResponseEntity.ok(GetUserResponse.fromModel(userDetail.userModel()));
    }

    @PatchMapping("me")
    public ResponseEntity<?> updateUserProfile(@Valid UpdateUserRequest updateUserRequest,
                                        @AuthenticationPrincipal UserDetail userDetail) throws Exception
    {
        var userId = userDetail.userModel().getId();
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

        var refreshedUserResult = userService.findUserById(userId);
        var userModel = refreshedUserResult.getOrThrow();
        Authentication newAuth = getrefreshedAuthentication(userModel);
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        // HttpSession에 저장은 SecurityFilterChain이 요청 끝나면서 자동으로 해줌

        return ResponseEntity.ok(updateResult.getOrThrow());
    }

    @DeleteMapping("me")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal UserDetail userDetail) throws Exception
    {
        var userId = userDetail.userModel().getId();
        var deleteResult = userService.softDeleteUser(userId);

        if(deleteResult.isFailure()){
            return new ResponseEntity<>(deleteResult.getSingleErrorOrThrow().getStatus());
        }

        return ResponseEntity.ok(deleteResult.getOrThrow());
    }

    @GetMapping("user/{userid}")
    public ResponseEntity<?> getUserByUserId(@PathVariable("userid") Long UserId) throws Exception
    {
        var result = userService.findUserByIdForViewer(UserId);
        if(result.isFailure()){
            return new ResponseEntity<>(result.getSingleErrorOrThrow().getStatus());
        }
        var userModel = result.getOrThrow();
        var response = GetUserResponse.fromModel(userModel);
        return ResponseEntity.ok(response);
    }

}
