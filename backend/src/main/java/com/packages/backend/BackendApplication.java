package com.packages.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class BackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(BackendApplication.class, args);
  }

  @Component
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public class AjaxCorsFilter extends CorsFilter {
    public AjaxCorsFilter() {
      super(configurationSource());
    }

    private static UrlBasedCorsConfigurationSource configurationSource() {
      CorsConfiguration corsConfig = new CorsConfiguration();
      List<String> allowedHeaders = Arrays.asList("X-Auth-Token", "Content-Type", "X-Requested-With", "XMLHttpRequest", "Access-Control-Allow-Origin", "Authorization", "File-Name");
      List<String> exposedHeaders = Arrays.asList("X-Auth-Token", "Content-Type", "X-Requested-With", "XMLHttpRequest", "File-Name");
      List<String> allowedMethods = Arrays.asList("POST", "GET", "DELETE", "PUT", "OPTIONS", "PATCH");
      List<String> allowedOrigins = Arrays.asList("http://localhost:4200", "https://pickme-production-187d.up.railway.app");
      corsConfig.setAllowedHeaders(allowedHeaders);
      corsConfig.setAllowedMethods(allowedMethods);
      corsConfig.setAllowedOrigins(allowedOrigins);
      corsConfig.setExposedHeaders(exposedHeaders);
      corsConfig.setMaxAge(86400L);
      corsConfig.setAllowCredentials(true);

      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", corsConfig);
      return source;
    }
  }
}
