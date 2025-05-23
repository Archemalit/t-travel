package ru.tbank.itis.tripbackend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.tbank.itis.tripbackend.repository.RefreshTokenRepository;
import ru.tbank.itis.tripbackend.security.jwt.SkipPathRequestMatcher;
import ru.tbank.itis.tripbackend.security.jwt.filter.TokenAuthenticationFilter;
import ru.tbank.itis.tripbackend.security.jwt.login.LoginAuthenticationFilter;
import ru.tbank.itis.tripbackend.security.jwt.login.RefreshTokenAuthenticationFilter;
import ru.tbank.itis.tripbackend.security.jwt.service.JwtService;

import java.util.List;


@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    private final List<String> ANONYMOUS_PATHS = List.of(
            "/api/v1/login", "/api/v1/register", "/api/v1/check", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/api/v1/refresh");

    @Bean
    public SecurityFilterChain apiSecurityFilterChain(
            HttpSecurity http,
            LoginAuthenticationFilter loginAuthenticationFilter,
            TokenAuthenticationFilter tokenAuthenticationFilter,
            RefreshTokenAuthenticationFilter refreshTokenAuthenticationFilter
    )
            throws Exception {

        HttpSecurity httpSecurity = http.securityMatcher("/api/**")
                .addFilterAt(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(loginAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(refreshTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/login", "/api/v1/register", "/api/v1/check").permitAll()
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    @Bean
    public Algorithm algorithm(@Value("${jwt.secret}") String secret) {
        return Algorithm.HMAC256(secret);
    }

    @Bean
    public LoginAuthenticationFilter loginAuthenticationFilter(
            AuthenticationManager authenticationManager,
            @Qualifier("tokenAuthenticationSuccessHandler") AuthenticationSuccessHandler successHandler,
            @Qualifier("authenticationFailureHandler") AuthenticationFailureHandler failureHandler
    ) {
        return new LoginAuthenticationFilter(
                "/api/v1/login", authenticationManager, successHandler, failureHandler);
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter(
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            @Qualifier("authenticationFailureHandler") AuthenticationFailureHandler failureHandler) {
        SkipPathRequestMatcher requestMatcher = new SkipPathRequestMatcher(ANONYMOUS_PATHS);

        return new TokenAuthenticationFilter(
                requestMatcher, jwtService, authenticationManager, failureHandler);
    }

    @Bean
    public RefreshTokenAuthenticationFilter refreshTokenAuthenticationFilter(
            AuthenticationManager authenticationManager,
            @Qualifier("refreshTokenAuthenticationSuccessHandler") AuthenticationSuccessHandler successHandler,
            @Qualifier("jwtAuthenticationFailureHandler") AuthenticationFailureHandler failureHandler,
            RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService) {
        return new RefreshTokenAuthenticationFilter(
                "/api/v1/refresh", authenticationManager, successHandler, failureHandler, refreshTokenRepository, jwtService);
    }

    @Bean
    public AuthenticationManager providerManager(List<AuthenticationProvider> providers) {
        return new ProviderManager(providers);
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(
            PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService) {
        var provider = new DaoAuthenticationProvider(passwordEncoder);

        provider.setUserDetailsService(userDetailsService);

        return provider;
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler();
    }

    @Bean
    public JWTVerifier jwtVerifier(Algorithm algorithm) {
        return JWT.require(algorithm).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
