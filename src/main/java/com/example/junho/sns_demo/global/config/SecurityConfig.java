package com.example.junho.sns_demo.global.config;

import com.example.junho.sns_demo.global.jwt.JWTFilter;
import com.example.junho.sns_demo.global.jwt.JWTUtil;
import com.example.junho.sns_demo.global.jwt.LoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final AuthenticationConfiguration authenticationConfiguration;
  private final JWTUtil jwtUtil;


  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http)
      throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable);
    http
        .formLogin(AbstractHttpConfigurer::disable);
    http
        .httpBasic(AbstractHttpConfigurer::disable);
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/swagger-resources/**",
                "/webjars/**",
                "/actuator/**",
                "/user/**",
                "/comment/**",
                "/follow/**",
                "/post/**",
                "/newsfeed/**",
                "/api/s3/**"
            ).permitAll() // Swagger 및 관련 리소스 허용
//            .requestMatchers("/post/create").hasRole("USER")
                .requestMatchers("/like").hasRole("USER")
            .requestMatchers("/admin").hasRole("ADMIN")
            .anyRequest().authenticated() // 나머지 요청은 인증 필요
        );

    http
        .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

    http
        .addFilterAt(
            new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil),
            UsernamePasswordAuthenticationFilter.class);
    // session management
    http
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 미사용
        );
    // logout disable
    http
        .logout(AbstractHttpConfigurer::disable);
    http
        .cors(AbstractHttpConfigurer::disable); // 테스트를 위해 CORS 비활성화

    return http.build();
  }
}
