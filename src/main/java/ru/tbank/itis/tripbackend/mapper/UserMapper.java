package ru.tbank.itis.tripbackend.mapper;

import org.mapstruct.Mapper;
import ru.tbank.itis.tripbackend.dto.UserDto;
import ru.tbank.itis.tripbackend.model.User;

import java.util.Set;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface UserMapper {
    UserDto mapUserToUserDto(User user);
    User mapUserDtoToUser(UserDto userDto);
    Set<UserDto> mapUserSetToUserDtoSet(Set<User> userSet);
    Set<User> mapUserDtoSetToUserSet(Set<UserDto> userSet);
}
