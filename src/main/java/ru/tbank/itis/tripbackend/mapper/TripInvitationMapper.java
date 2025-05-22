package ru.tbank.itis.tripbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tbank.itis.tripbackend.dto.TripInvitationDto;
import ru.tbank.itis.tripbackend.model.TripInvitation;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface TripInvitationMapper {
    TripInvitationDto tripInvitationToTripInvitationDto(TripInvitation tripInvitation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trip", ignore = true)
    @Mapping(target = "invitedUser", ignore = true)
    @Mapping(target = "inviter", ignore = true)
    TripInvitation tripInvitationDtoToTripInvitation(TripInvitationDto tripInvitationDto);
}