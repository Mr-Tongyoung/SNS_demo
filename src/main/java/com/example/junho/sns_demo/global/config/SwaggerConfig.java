package com.example.junho.sns_demo.global.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .components(new Components())
        .info(apiInfo());
  }

  private Info apiInfo() {
    return new Info()
        .title("SNS BackEnd API")
        .description("소셜미디어 기능 API")
        .version("1.0.0");
  }
}