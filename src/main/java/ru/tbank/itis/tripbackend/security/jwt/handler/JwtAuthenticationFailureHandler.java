package ru.tbank.itis.tripbackend.security.jwt.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import ru.tbank.itis.tripbackend.dto.response.SimpleErrorResponse;
import ru.tbank.itis.tripbackend.exception.InvalidRefreshTokenException;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String message = "Unauthorized";

        if (exception instanceof InvalidRefreshTokenException) {
            message = exception.getMessage();
        }

        SimpleErrorResponse errorResponse = new SimpleErrorResponse(
                null,
                HttpStatus.UNAUTHORIZED.value(),
                "Неверный refresh-токен",
                message
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}