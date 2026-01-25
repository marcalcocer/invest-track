package com.invest.track.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    // For test purposes, allow localhost:4321 to access the API
    // TODO: Improve this before publishing it to production and work with profiles
    registry
        .addMapping("/**")
        .allowedOrigins("http://localhost:4321", "http://172.21.154.192:4321/") // Frontend origin
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true);
  }
}
