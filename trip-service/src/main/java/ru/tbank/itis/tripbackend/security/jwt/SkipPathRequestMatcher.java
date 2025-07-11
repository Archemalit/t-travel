package ru.tbank.itis.tripbackend.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;
import java.util.stream.Collectors;


public class SkipPathRequestMatcher implements RequestMatcher {

    private final OrRequestMatcher requestMatcher;

    public SkipPathRequestMatcher(String... excludePaths) {
        this(List.of(excludePaths));
    }

    public SkipPathRequestMatcher(List<String> excludePaths) {

        List<RequestMatcher> requestMatchers = excludePaths.stream()
                .map(AntPathRequestMatcher::new)
                .collect(Collectors.toList());

        this.requestMatcher = new OrRequestMatcher(requestMatchers);
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return !requestMatcher.matches(request);
    }

}
