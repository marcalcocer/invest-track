package com.investTrack.service;

import com.investTrack.model.Investment;
import com.investTrack.model.Summary;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SummaryService {

  public Summary calculateSummary(List<Investment> investments) {
    var investedAmount = 0.0;
    var obtained = 0.0;
    var benefit = 0.0;
    var realInvested = 0.0;

    for (Investment investment : investments) {
      realInvested += investment.getInitialInvestedAmount();

      if (investment.isReinvested()) {
        continue;
      }
      investedAmount += investment.getTotalInvestedAmount();
      obtained += investment.getObtained();
      benefit += investment.getBenefit();
    }

    var profitability = calculateProfitability(benefit, investedAmount);
    var realBenefit = calculateRealBenefit(obtained, realInvested);
    var realProfitability = calculateRealProfitability(realBenefit, realInvested);

    return Summary.builder()
        .investedAmount(investedAmount)
        .obtained(obtained)
        .benefit(benefit)
        .profitability(profitability)
        .realInvested(realInvested)
        .realBenefit(realBenefit)
        .realProfitability(realProfitability)
        .build();
  }

  private double calculateRealProfitability(double realBenefit, double realInvested) {
    if (realInvested == 0) {
      log.debug("Real invested amount is 0, returning 0 as real profitability");
      return 0;
    }
    return realBenefit / realInvested;
  }

  private double calculateProfitability(double benefit, double investedAmount) {
    if (investedAmount == 0) {
      log.debug("Invested amount is 0, returning 0 as profitability");
      return 0;
    }
    return benefit / investedAmount;
  }

  private double calculateRealBenefit(double obtained, double realInvested) {
    return obtained - realInvested;
  }
}
