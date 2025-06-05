package ru.tbank.itis.tripbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tbank.itis.tripbackend.dto.UserDto;
import ru.tbank.itis.tripbackend.model.User;

import java.util.Set;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface UserMapper {
    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdTrips", ignore = true)
    @Mapping(target = "paidExpenses", ignore = true)
//    @Mapping(target = "notifications", ignore = true)
    User toEntity(UserDto userDto);

}
