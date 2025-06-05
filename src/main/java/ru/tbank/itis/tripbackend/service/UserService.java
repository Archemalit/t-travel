package ru.tbank.itis.tripbackend.service;

import ru.tbank.itis.tripbackend.dto.UserDto;
import ru.tbank.itis.tripbackend.model.User;

import java.util.Set;

public interface UserService {
    Set<User> getUserSetByUserDtoSet(Set<UserDto> members);
}
