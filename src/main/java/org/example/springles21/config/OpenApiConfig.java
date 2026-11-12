package org.example.springles21.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.example.springles21.controller.GlobalExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static jakarta.servlet.http.HttpServletRequest.BASIC_AUTH;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(GlobalExceptionHandler globalExceptionHandler) {
        return new OpenAPI()
                .info(new Info()
                        .title("Speaker & Talk Management API")
                        .version("1.0")
                        .description("REST API documentation for managing speakers and talks"))
                .components(new Components()
                        .addSecuritySchemes(BASIC_AUTH, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")))
                .addSecurityItem(new SecurityRequirement().addList(BASIC_AUTH));
    }
}