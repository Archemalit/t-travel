package ru.tbank.itis.tripbackend.service;

import ru.tbank.itis.tripbackend.dto.TripInvitationDto;
import ru.tbank.itis.tripbackend.dto.common.SimpleResponse;
import ru.tbank.itis.tripbackend.dto.request.InviteRequest;
import ru.tbank.itis.tripbackend.model.User;

import java.util.List;

public interface MemberService {
    SimpleResponse inviteMember(Long tripId, User creator, InviteRequest inviteRequest);
    SimpleResponse removeMember(Long tripId, Long userId, User creator);
}