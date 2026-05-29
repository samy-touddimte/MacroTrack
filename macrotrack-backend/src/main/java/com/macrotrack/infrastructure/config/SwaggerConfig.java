package com.macrotrack.infrastructure.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI macrotrackOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Macrotrack Core API v1")
                        .description("Core engine for metabolic forecasting, nutritional tracking, and body composition analytics (v1).")
                        .version("v1.0")
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth")); 
    }
}
