package ru.tbank.itis.tripbackend.security.jwt.login;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import ru.tbank.itis.tripbackend.dto.JwtTokenPairDto;
import ru.tbank.itis.tripbackend.dto.request.RefreshTokenRequest;
import ru.tbank.itis.tripbackend.exception.InvalidRefreshTokenException;
import ru.tbank.itis.tripbackend.repository.RefreshTokenRepository;
import ru.tbank.itis.tripbackend.repository.UserRepository;
import ru.tbank.itis.tripbackend.security.exception.AuthMethodNotSupportedException;
import ru.tbank.itis.tripbackend.security.jwt.JwtAuthenticationToken;
import ru.tbank.itis.tripbackend.security.jwt.service.JwtService;

import java.io.IOException;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

public class RefreshTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failureHandler;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    public RefreshTokenAuthenticationFilter(
            String defaultFilterProcessesUrl,
            AuthenticationManager authenticationManager,
            AuthenticationSuccessHandler successHandler,
            AuthenticationFailureHandler failureHandler,
            RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService
    ) {
        super(defaultFilterProcessesUrl, authenticationManager);
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (!POST.asHttpMethod().matches(request.getMethod())) {
            throw new AuthMethodNotSupportedException("Этот метод не поддерживается!");
        }

        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(jwtService.getRawToken(request));

        if (refreshTokenRepository.deleteByToken(refreshTokenRequest.refreshToken()) == 0) {
            throw new InvalidRefreshTokenException("Данный refresh-токен не актуален!");
        }

        JwtTokenPairDto jwtPair = jwtService.getTokenPair(jwtService.getPhoneNumber(refreshTokenRequest.refreshToken()));
        request.setAttribute("jwt", jwtPair);

        JwtAuthenticationToken authToken = new JwtAuthenticationToken(jwtPair.accessToken());
        return getAuthenticationManager().authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {

        failureHandler.onAuthenticationFailure(request, response, failed);
    }
}
