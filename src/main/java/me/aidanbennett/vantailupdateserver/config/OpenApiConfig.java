package me.aidanbennett.vantailupdateserver.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Vantail Update Server API",
                version = "v1",
                description = "API for publishing and retrieving application updates."
        )
)
public class OpenApiConfig {
}
