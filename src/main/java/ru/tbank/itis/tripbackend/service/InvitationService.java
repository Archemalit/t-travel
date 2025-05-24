package ru.tbank.itis.tripbackend.service;

import ru.tbank.itis.tripbackend.dto.TripInvitationDto;
import ru.tbank.itis.tripbackend.dto.common.SimpleResponse;

import java.util.List;

public interface InvitationService {
    List<TripInvitationDto> getUserInvitations(Long userId);
    SimpleResponse acceptInvitation(Long invitationId, Long userId);
    SimpleResponse rejectInvitation(Long invitationId, Long userId);
}
