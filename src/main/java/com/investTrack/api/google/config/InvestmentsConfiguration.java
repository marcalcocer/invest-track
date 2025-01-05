package com.investTrack.api.google.config;

import com.investTrack.service.SummaryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InvestmentsConfiguration {

  @Bean
  public SummaryService summaryService() {
    return new SummaryService();
  }
}
