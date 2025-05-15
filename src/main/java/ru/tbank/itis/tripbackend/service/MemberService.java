package ru.tbank.itis.tripbackend.service;

import ru.tbank.itis.tripbackend.dto.TripInvitationDto;
import ru.tbank.itis.tripbackend.dto.common.SimpleResponse;
import ru.tbank.itis.tripbackend.dto.request.InviteRequest;

import java.util.List;

public interface MemberService {
    SimpleResponse inviteMember(Long tripId, Long inviterId, InviteRequest inviteRequest);
    SimpleResponse removeMember(Long tripId, Long userId, Long requesterId);
    List<TripInvitationDto> getUserInvitations(Long userId);
    SimpleResponse acceptInvitation(Long invitationId, Long userId);
    SimpleResponse rejectInvitation(Long invitationId, Long userId);
}