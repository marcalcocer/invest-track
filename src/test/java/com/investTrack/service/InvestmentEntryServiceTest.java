package com.investTrack.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.investTrack.model.InvestmentEntry;
import com.investTrack.repository.InvestmentEntryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InvestmentEntryServiceTest {

  @Mock private InvestmentEntryRepository mockRepository;

  @InjectMocks private InvestmentEntryService service;

  @AfterEach
  public void afterTests() {
    verifyNoMoreInteractions(mockRepository);
  }

  @Test
  public void testCreateInvestmentEntry_ShouldSaveAndReturnNewInvestmentEntry() {
    InvestmentEntry entry = new InvestmentEntry();

    service.createEntry(entry);

    verify(mockRepository).save(eq(entry));
  }
}
