package ru.tbank.itis.tripbackend.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;
import ru.tbank.itis.tripbackend.dictonary.UserRole;

import static jakarta.persistence.EnumType.STRING;

@Builder
@Data
public class UserDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String phoneNumber;

}
