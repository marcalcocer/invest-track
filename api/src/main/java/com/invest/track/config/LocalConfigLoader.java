package com.invest.track.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:application-local.properties", ignoreResourceNotFound = true)
@Slf4j
public class LocalConfigLoader {

  private static final String CONFIG_FILE = "application-local.properties";

  @PostConstruct
  public void checkConfigFile() {
    // Since classpath files are not standard Files, we check if the resource can be loaded
    // However, for simplicity and to match previous logs, we check existence in the classpath
    var resource = getClass().getClassLoader().getResource(CONFIG_FILE);
    if (resource == null) {
      log.warn(
          "Local configuration file '{}' not found in classpath. Default settings will be used.",
          CONFIG_FILE);
    } else {
      log.info("Local configuration file '{}' loaded successfully from classpath.", CONFIG_FILE);
    }
  }
}
