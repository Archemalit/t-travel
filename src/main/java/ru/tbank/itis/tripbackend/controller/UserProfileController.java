package ru.tbank.itis.tripbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.tbank.itis.tripbackend.dto.request.UserUpdateProfileRequest;
import ru.tbank.itis.tripbackend.dto.response.UserProfileResponse;
import ru.tbank.itis.tripbackend.security.details.UserDetailsImpl;
import ru.tbank.itis.tripbackend.service.UserService;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;

    @GetMapping()
    public UserProfileResponse getUserProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getUserProfile(userDetails.getUser().getId());
    }

    @PatchMapping
    public UserProfileResponse updateProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UserUpdateProfileRequest request) {
        return userService.updateProfile(userDetails.getUser().getId(), request);
    }

}
