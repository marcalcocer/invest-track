package com.investTrack.api.google.config;

import com.google.api.services.sheets.v4.Sheets;
import com.investTrack.api.google.GoogleSheetsClient;
import com.investTrack.api.google.GoogleSheetsInvestmentAdapter;
import com.investTrack.api.google.GoogleSheetsInvestmentEntryAdapter;
import com.investTrack.api.google.GoogleSheetsService;
import com.investTrack.api.google.credential.GoogleSheetsCredentialService;
import com.investTrack.model.adapter.AdapterUtils;
import com.investTrack.model.adapter.InvestmentAdapter;
import com.investTrack.model.adapter.InvestmentEntryAdapter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleSheetsConfiguration {

  @Value("${google.api.spread-sheet-id}")
  private String spreadSheetId;

  @Value("${google.api.credentials.receiver-port}")
  private int credentialsPort;

  private static final String applicationName = "InvestTrack API";

  @Bean
  public Sheets sheets() throws GeneralSecurityException, IOException {
    var credentialsService = new GoogleSheetsCredentialService(credentialsPort);
    return credentialsService.createSheetsService(applicationName);
  }

  @Bean
  public GoogleSheetsService googleSheetService(Sheets sheets) {
    var googleSheetsClient = new GoogleSheetsClient(sheets);

    var googleSheetsInvestmentAdapter = new GoogleSheetsInvestmentAdapter();
    var googleSheetsInvestmentEntryAdapter = new GoogleSheetsInvestmentEntryAdapter();

    var adapterUtils = new AdapterUtils();
    var investmentAdapter = new InvestmentAdapter(adapterUtils);
    var investmentEntryAdapter = new InvestmentEntryAdapter(adapterUtils);

    return new GoogleSheetsService(
        spreadSheetId,
        googleSheetsClient,
        googleSheetsInvestmentAdapter,
        googleSheetsInvestmentEntryAdapter,
        investmentAdapter,
        investmentEntryAdapter);
  }
}
