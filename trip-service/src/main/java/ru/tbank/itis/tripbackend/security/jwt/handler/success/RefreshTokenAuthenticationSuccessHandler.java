package ru.tbank.itis.tripbackend.security.jwt.handler.success;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ru.tbank.itis.tripbackend.dto.JwtTokenPairDto;
import ru.tbank.itis.tripbackend.util.JsonUtil;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RefreshTokenAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        JwtTokenPairDto jwtPair = (JwtTokenPairDto) request.getAttribute("jwt");

        response.setStatus(200);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(JsonUtil.write(jwtPair));
    }
}
