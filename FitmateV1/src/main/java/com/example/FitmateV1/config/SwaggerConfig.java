package com.example.FitmateV1.config;




import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.*;

@Configuration
public class SwaggerConfig {

  
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                    .addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                    ))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .info(new Info().title("FitMate API").version("v1.0"));
    }

}
