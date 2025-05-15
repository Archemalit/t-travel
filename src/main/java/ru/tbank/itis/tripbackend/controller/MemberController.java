package ru.tbank.itis.tripbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.tbank.itis.tripbackend.dto.*;
import ru.tbank.itis.tripbackend.dto.common.SimpleResponse;
import ru.tbank.itis.tripbackend.dto.request.InviteRequest;
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
            @PathVariable Long tripId,
            @RequestHeader("X-User-Id") Long inviterId,
            @Valid @RequestBody InviteRequest inviteRequest
    ) {
        return memberService.inviteMember(tripId, inviterId, inviteRequest);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(
            @PathVariable Long tripId,
            @PathVariable Long userId,
            @RequestHeader("X-User-Id") Long requesterId
    ) {
        memberService.removeMember(tripId, userId, requesterId);
    }

    @GetMapping("/invitations")
    public List<TripInvitationDto> getUserInvitations(
            @RequestHeader("X-User-Id") Long userId
    ) {
        return memberService.getUserInvitations(userId);
    }

    @PostMapping("/invitations/{invitationId}/accept")
    @ResponseStatus(HttpStatus.OK)
    public SimpleResponse acceptInvitation(
            @PathVariable Long invitationId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        return memberService.acceptInvitation(invitationId, userId);
    }

    @PostMapping("/invitations/{invitationId}/reject")
    @ResponseStatus(HttpStatus.OK)
    public SimpleResponse rejectInvitation(
            @PathVariable Long invitationId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        return memberService.rejectInvitation(invitationId, userId);
    }
}