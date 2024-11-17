package com.investTrack.controller;

import com.investTrack.model.Investment;
import com.investTrack.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/investments")
public class InvestmentController {
  private final InvestmentService investmentService;

  @PostMapping
  public ResponseEntity<Investment> createInvestment(@RequestBody Investment investment) {
    Investment savedInvestment = investmentService.createInvestment(investment);
    return new ResponseEntity<>(savedInvestment, HttpStatus.CREATED);
  }
}
