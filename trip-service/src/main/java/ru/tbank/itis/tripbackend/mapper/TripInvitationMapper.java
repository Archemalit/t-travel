package ru.tbank.itis.tripbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tbank.itis.tripbackend.dto.TripInvitationDto;
import ru.tbank.itis.tripbackend.model.TripInvitation;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface TripInvitationMapper {
    @Mapping(target = "tripId", source = "trip.id")
    @Mapping(target = "invitedUserId", source = "invitedUser.id")
    @Mapping(target = "inviterId", source = "inviter.id")
    TripInvitationDto toDto(TripInvitation tripInvitation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trip", ignore = true)
    @Mapping(target = "invitedUser", ignore = true)
    @Mapping(target = "inviter", ignore = true)
    TripInvitation toEntity(TripInvitationDto tripInvitationDto);
}