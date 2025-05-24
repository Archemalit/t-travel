package ru.tbank.itis.tripbackend.security.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import ru.tbank.itis.tripbackend.security.jwt.JwtAuthenticationToken;
import ru.tbank.itis.tripbackend.security.jwt.service.JwtService;

import java.io.IOException;


public class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {


    private final JwtService jwtService;
    private final AuthenticationFailureHandler failureHandler;


    public TokenAuthenticationFilter(
            RequestMatcher requestMatcher,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            AuthenticationFailureHandler failureHandler) {
        super(requestMatcher, authenticationManager);
        this.jwtService = jwtService;
        this.failureHandler = failureHandler;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        String rawToken = jwtService.getRawToken(request);

        JwtAuthenticationToken token = new JwtAuthenticationToken(rawToken);

        return getAuthenticationManager().authenticate(token);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        SecurityContextHolder.getContext()
                .setAuthentication(authResult);

        chain.doFilter(request, response);
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {

        failureHandler.onAuthenticationFailure(request, response, failed);
    }
}
