package com.invest.track.api.google.config;

import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleReceiverConfig {
  @Value("${google.api.credentials.receiver-port}")
  private int receiverPort;

  @Bean(name = "googleLocalServerReceiver")
  public LocalServerReceiver googleLocalServerReceiver() {
    return new LocalServerReceiver.Builder().setPort(receiverPort).build();
  }
}
