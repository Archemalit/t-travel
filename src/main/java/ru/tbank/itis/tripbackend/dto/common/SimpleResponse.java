package ru.tbank.itis.tripbackend.dto.common;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleResponse {
    private boolean success;
    private String message;
}