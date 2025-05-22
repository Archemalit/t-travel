package ru.tbank.itis.tripbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.tbank.itis.tripbackend.dto.*;
import ru.tbank.itis.tripbackend.dto.common.SimpleResponse;
import ru.tbank.itis.tripbackend.dto.request.InviteRequest;
import ru.tbank.itis.tripbackend.security.details.UserDetailsImpl;
import ru.tbank.itis.tripbackend.service.MemberService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trips/{tripId}/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SimpleResponse inviteMember(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long tripId,
            @Valid @RequestBody InviteRequest inviteRequest
    ) {
        return memberService.inviteMember(tripId, userDetails.getUser(), inviteRequest);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public SimpleResponse removeMember(
            @PathVariable Long tripId,
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return memberService.removeMember(tripId, userId, userDetails.getUser());
    }
}