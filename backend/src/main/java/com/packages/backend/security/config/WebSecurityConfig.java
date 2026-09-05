package com.packages.backend.security.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(CorsProperties.class)
public class WebSecurityConfig {

  // Endpoints reachable without being authenticated. The health probe is public because the host
  // polls it before the application holds a session, and it only answers with a status.
  private static final String[] PUBLIC_ENDPOINTS = {"/registration/**", "/logout", "/actuator/health"};

  private final CorsProperties corsProperties;

  public WebSecurityConfig(CorsProperties corsProperties) {
    this.corsProperties = corsProperties;
  }

  /**
   * Declares the security rules of the API: stateful HTTP Basic authentication, CSRF disabled
   * because the front end is a separate single page application authenticating on every call, and
   * a fine grained authorization handled by the {@code @PreAuthorize} annotations of the
   * controllers.
   *
   * @param http security builder
   * @return the filter chain applied to every request
   * @throws Exception when the chain cannot be built
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http, RestAuthenticationEntryPoint authenticationEntryPoint) throws Exception {
    return http
      .cors(Customizer.withDefaults())
      .csrf(AbstractHttpConfigurer::disable)
      .authorizeHttpRequests(requests -> requests
        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
        .anyRequest().authenticated())
      // The front end sends the credentials on the login call only, then relies on the session
      // cookie. Since Spring Security 6 the basic filter keeps the authentication for the current
      // request only, so the session repository has to be declared explicitly.
      .httpBasic(basic -> basic
        .authenticationEntryPoint(authenticationEntryPoint)
        .securityContextRepository(new HttpSessionSecurityContextRepository()))
      .logout(logout -> logout
        .invalidateHttpSession(true)
        .clearAuthentication(true)
        .deleteCookies("JSESSIONID"))
      .build();
  }

  /**
   * Hashes the passwords. The credentials of the HTTP Basic header are checked against the
   * accounts of the database by the provider Spring Security builds from this encoder and from
   * {@link com.packages.backend.service.UserService}, the only {@code UserDetailsService} of the
   * application.
   *
   * @return the encoder used to store and to verify a password
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Allows the front end applications declared in the configuration to call the API with the
   * session cookie.
   *
   * @return the CORS rules applied to every endpoint
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowCredentials(true);
    configuration.setAllowedOrigins(corsProperties.allowedOrigins());
    configuration.setAllowedHeaders(List.of(CorsConfiguration.ALL));
    configuration.setAllowedMethods(List.of(CorsConfiguration.ALL));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
