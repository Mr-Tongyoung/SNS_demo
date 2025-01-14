package com.example.junho.sns_demo.global.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    String jwtSchemeName = "Bearer Authentication";

    // JWT 보안 설정 추가
    SecurityScheme securityScheme = new SecurityScheme()
        .name(jwtSchemeName)
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT");

    SecurityRequirement securityRequirement = new SecurityRequirement()
        .addList(jwtSchemeName);

    return new OpenAPI()
        .components(new Components().addSecuritySchemes(jwtSchemeName, securityScheme))
        .addSecurityItem(securityRequirement)
        .info(apiInfo());
  }

  private Info apiInfo() {
    return new Info()
        .title("SNS BackEnd API")
        .description("소셜미디어 기능 API")
        .version("1.0.0");
  }
}