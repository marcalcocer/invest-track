package com.invest.track.service;

import com.invest.track.model.Investment;
import com.invest.track.model.Summary;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SummaryService {

  public Summary calculateSummary(List<Investment> investments) {
    var investedAmount = 0.0;
    var obtained = 0.0;
    var benefit = 0.0;

    for (Investment investment : investments) {
      if (!investment.isActive() || investment.getLastEntry() == null) {
        continue;
      }
      var lastEntry = investment.getLastEntry();

      investedAmount += lastEntry.getTotalInvestedAmount();
      obtained += lastEntry.getObtained();
      benefit += lastEntry.getBenefit();
    }

    var profitability = calculateProfitability(benefit, investedAmount);

    return Summary.builder()
        .investedAmount(investedAmount)
        .obtained(obtained)
        .benefit(benefit)
        .profitability(profitability)
        .build();
  }

  private double calculateProfitability(double benefit, double investedAmount) {
    if (investedAmount == 0) {
      log.debug("Invested amount is 0, returning 0 as profitability");
      return 0;
    }
    return benefit / investedAmount;
  }
}
