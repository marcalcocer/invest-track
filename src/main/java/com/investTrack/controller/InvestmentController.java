package com.investTrack.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.investTrack.model.Investment;
import com.investTrack.service.InvestmentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/investments")
public class InvestmentController {
  private final InvestmentService investmentService;

  /*
   * Reload investments from the Google Sheets API and save them to the database. In case of existing investments,
   * the method will try to merge the new investments with the existing ones.
   */
  @GetMapping("/reload")
  public ResponseEntity<List<Investment>> loadInvestments() {
    log.debug("Load investment endpoint called");
    List<Investment> investments = investmentService.getInvestments();

    if (investments == null) {
      log.debug("Answering with internal server error to load investment call");
      return ResponseEntity.internalServerError().build();
    }

    if (investments.isEmpty()) {
      log.debug("Answering with no content to load investment call");
      return ResponseEntity.noContent().build();
    }

    return new ResponseEntity<>(investments, OK);
  }

  @PostMapping
  public ResponseEntity<Investment> createInvestment(@RequestBody Investment investment) {
    log.debug("Create investment endpoint called");
    var savedInvestment = investmentService.createInvestment(investment);

    if (savedInvestment == null) {
      log.debug("Answering with internal server error to create investment call");
      return ResponseEntity.internalServerError().build();
    }

    return new ResponseEntity<>(savedInvestment, CREATED);
  }
}
