package ru.tbank.itis.tripbackend.security.jwt.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import ru.tbank.itis.tripbackend.security.details.UserDetailsImpl;
import ru.tbank.itis.tripbackend.security.details.UserDetailsServiceImpl;
import ru.tbank.itis.tripbackend.security.jwt.JwtAuthenticationToken;
import ru.tbank.itis.tripbackend.security.jwt.service.JwtService;


@Component
@RequiredArgsConstructor
public class TokenAuthenticationProvider implements AuthenticationProvider {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String rawToken = (String) authentication.getCredentials();

        try {
            if (jwtService.isAccessToken(rawToken)) {
                throw new BadCredentialsException("Это не access-токен!");
            }
        } catch (TokenExpiredException ex) {
            throw new BadCredentialsException("Токен истёк", ex);
        } catch (JWTVerificationException ex) {
            throw new BadCredentialsException("Некорректный токен", ex);
        }


        String username = getUsername(rawToken);
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);
        return new JwtAuthenticationToken(userDetails);
    }

    private String getUsername(String rawToken) {
        try {
            return jwtService.getPhoneNumber(rawToken);
        } catch (JWTVerificationException e) {
            throw new BadCredentialsException("Invalid token");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
