package ru.tbank.itis.tripbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripInvitationDto {

    private Long id;
    
    @NotNull(message = "ID поездки обязательно")
    private Long tripId;
    
    @NotNull(message = "ID приглашенного пользователя обязательно")
    private Long invitedUserId;
    
    @NotNull(message = "ID приглашающего пользователя обязательно")
    private Long inviterId;
    
    private String comment;
    
    @NotBlank(message = "Статус приглашения обязателен")
    private String status;

}