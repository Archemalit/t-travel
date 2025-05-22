package ru.tbank.itis.tripbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.tbank.itis.tripbackend.dto.TripInvitationDto;
import ru.tbank.itis.tripbackend.dto.common.SimpleResponse;
import ru.tbank.itis.tripbackend.security.details.UserDetailsImpl;
import ru.tbank.itis.tripbackend.service.InvitationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invitations")
@RequiredArgsConstructor
public class InvitationController {
    private final InvitationService invitationService;

    @GetMapping
    public List<TripInvitationDto> getUserInvitations(
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ) {
        return invitationService.getUserInvitations(userDetails.getId());
    }

    @PostMapping("/invitations/{invitationId}/accept")
    @ResponseStatus(HttpStatus.OK)
    public SimpleResponse acceptInvitation(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long invitationId
    ) {
        return invitationService.acceptInvitation(invitationId, userDetails.getId());
    }

    @PostMapping("/invitations/{invitationId}/reject")
    @ResponseStatus(HttpStatus.OK)
    public SimpleResponse rejectInvitation(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long invitationId
    ) {
        return invitationService.rejectInvitation(invitationId, userDetails.getId());
    }
}
