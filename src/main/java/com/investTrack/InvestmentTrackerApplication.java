package com.investTrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

// We need to exclude DataSourceAutoConfiguration to avoid an error initializing the project
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class InvestmentTrackerApplication {

  public static void main(String[] args) {
    SpringApplication.run(InvestmentTrackerApplication.class, args);
  }
}
