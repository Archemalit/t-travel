package ru.tbank.itis.tripbackend.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class InviteRequest {

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Номер телефона должен быть в международном формате")
    private String phone;

}