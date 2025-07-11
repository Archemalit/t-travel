package ru.tbank.itis.tripbackend.security.jwt.handler.failure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
public class RefreshTokenAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String message = "Неверный refresh-токен";

        if (exception instanceof InvalidRefreshTokenException) {
            message = exception.getMessage();
        }

        SimpleErrorResponse errorResponse = new SimpleErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                message
        );
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}