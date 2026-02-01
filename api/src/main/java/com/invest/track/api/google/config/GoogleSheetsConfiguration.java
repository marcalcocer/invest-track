package com.invest.track.api.google.config;

import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.services.sheets.v4.Sheets;
import com.invest.track.api.google.GoogleSheetsClient;
import com.invest.track.api.google.GoogleSheetsForecastService;
import com.invest.track.api.google.GoogleSheetsInvestmentAdapter;
import com.invest.track.api.google.GoogleSheetsInvestmentEntryAdapter;
import com.invest.track.api.google.GoogleSheetsInvestmentService;
import com.invest.track.api.google.credential.GoogleSheetsCredentialService;
import com.invest.track.model.adapter.ForecastAdapter;
import com.invest.track.model.adapter.InvestmentAdapter;
import com.invest.track.model.adapter.InvestmentEntryAdapter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleSheetsConfiguration {

  @Value("${google.api.spread-sheet-id}")
  private String spreadSheetId;

  @Value("${google.api.credentials.receiver-port}")
  private int credentialsPort;

  @Value("${sheets.allowlist}")
  private String allowlistSheetsConfig;

  private static final String applicationName = "InvestTrack API";

  @Bean
  public Sheets sheets(@Qualifier("googleLocalServerReceiver") LocalServerReceiver receiver)
      throws GeneralSecurityException, IOException {
    var credentialsService = new GoogleSheetsCredentialService(receiver);
    return credentialsService.createSheetsService(applicationName);
  }

  @Bean
  public GoogleSheetsInvestmentService googleSheetService(
      GoogleSheetsClient googleSheetsClient,
      GoogleSheetsInvestmentAdapter googleSheetsInvestmentAdapter,
      GoogleSheetsInvestmentEntryAdapter googleSheetsInvestmentEntryAdapter,
      InvestmentAdapter investmentAdapter,
      InvestmentEntryAdapter investmentEntryAdapter) {
    return new GoogleSheetsInvestmentService(
        spreadSheetId,
        googleSheetsClient,
        googleSheetsInvestmentAdapter,
        googleSheetsInvestmentEntryAdapter,
        investmentAdapter,
        investmentEntryAdapter,
        allowlistSheetsConfig);
  }

  @Bean
  public GoogleSheetsForecastService googleSheetsForecastService(
      GoogleSheetsClient googleSheetsClient, ForecastAdapter forecastAdapter) {
    return new GoogleSheetsForecastService(spreadSheetId, googleSheetsClient, forecastAdapter);
  }
}
