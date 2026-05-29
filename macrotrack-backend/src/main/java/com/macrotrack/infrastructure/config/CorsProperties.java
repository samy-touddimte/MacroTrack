package com.macrotrack.infrastructure.config;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import jakarta.annotation.PostConstruct;

import java.util.List;

@Validated
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {

    @NotEmpty
    private List<String> allowedOrigins;

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    @PostConstruct
    public void validate() {
        if (allowedOrigins != null && allowedOrigins.contains("*")) {
            throw new IllegalStateException("Erreur de configuration : app.cors.allowed-origins ne peut pas contenir '*' car allowCredentials est activé.");
        }
    }
}
