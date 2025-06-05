package ru.tbank.itis.tripbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.itis.tripbackend.dto.ActualExpenseDto;
import ru.tbank.itis.tripbackend.dto.UserDto;
import ru.tbank.itis.tripbackend.mapper.UserMapper;
import ru.tbank.itis.tripbackend.model.ActualExpense;
import ru.tbank.itis.tripbackend.model.User;
import ru.tbank.itis.tripbackend.repository.UserRepository;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Set<User> getUserSetByUserDtoSet(Set<UserDto> members) {
        return members.stream()
                .map(member -> findUserById(member.getId()))
                .collect(Collectors.toSet());
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь с id: " + userId + " не найден"));
    }

}
