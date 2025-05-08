package ru.tbank.itis.tripbackend.config;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenApiCustomizer openApiSecurityCustomizer() {
        return openApi -> openApi.addSecurityItem(
                        new SecurityRequirement()
                                .addList("Authentication Bearer"))
                .getComponents()
                .addSecuritySchemes("Authentication Bearer", createSecurityScheme());
    }
    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
    }
}
