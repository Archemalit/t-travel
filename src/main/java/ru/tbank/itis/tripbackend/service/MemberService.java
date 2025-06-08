package ru.tbank.itis.tripbackend.service;

import ru.tbank.itis.tripbackend.dto.TripInvitationDto;
import ru.tbank.itis.tripbackend.dto.TripParticipantDto;
import ru.tbank.itis.tripbackend.dto.common.SimpleResponse;
import ru.tbank.itis.tripbackend.dto.request.InviteRequest;
import ru.tbank.itis.tripbackend.model.User;

import java.util.List;

public interface MemberService {
    void inviteMember(Long tripId, User creator, InviteRequest inviteRequest);
//    void removeMember(Long tripId, Long userId, User creator);
    List<TripParticipantDto> getActiveMembers(Long tripId, User currentUser);
}