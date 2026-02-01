package com.invest.track.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.invest.track.model.Investment;
import com.invest.track.model.InvestmentEntry;
import com.invest.track.model.Summary;
import com.invest.track.service.InvestmentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
   * Load investments from the Google Sheets API and save them to the database. In case of existing investments,
   * the method will try to merge the new investments with the existing ones.
   */
  @GetMapping
  public ResponseEntity<List<Investment>> getInvestments() {
    log.info("Get investment endpoint called");
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
    log.info("Create investment endpoint called");
    var savedInvestment = investmentService.createInvestment(investment);

    if (savedInvestment == null) {
      log.debug("Answering with internal server error to create investment call");
      return ResponseEntity.internalServerError().build();
    }

    return new ResponseEntity<>(savedInvestment, CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Investment> updateInvestment(
      @PathVariable Long id, @RequestBody Investment investment) {
    log.info("Update investment endpoint called");
    investment.setId(id);
    var updated = investmentService.updateInvestment(id, investment);
    if (updated == null) {
      return ResponseEntity.internalServerError().build();
    }
    return new ResponseEntity<>(updated, OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Investment> deleteInvestment(@PathVariable Long id) {
    log.info("Delete investment endpoint called");
    var deletedInvestment = investmentService.deleteInvestment(id);

    if (deletedInvestment == null) {
      log.debug("Answering with no content to delete investment call");
      return ResponseEntity.noContent().build();
    }

    return new ResponseEntity<>(deletedInvestment, OK);
  }

  @PostMapping("/entry/{id}")
  public ResponseEntity<InvestmentEntry> createInvestmentEntry(
      @RequestBody InvestmentEntry entry, @PathVariable Long id) {
    log.info("Create entry endpoint called");
    var savedEntry = investmentService.createInvestmentEntry(entry, id);

    if (savedEntry == null) {
      log.debug("Answering with internal server error to create investment entry call");
      return ResponseEntity.internalServerError().build();
    }

    return new ResponseEntity<>(savedEntry, CREATED);
  }

  @PutMapping("/entry/{investmentId}/{entryId}")
  public ResponseEntity<InvestmentEntry> updateInvestmentEntry(
      @PathVariable Long investmentId,
      @PathVariable Long entryId,
      @RequestBody InvestmentEntry entry) {
    log.info("Update entry endpoint called");
    entry.setId(entryId);
    var updatedEntry = investmentService.updateInvestmentEntry(investmentId, entry);
    if (updatedEntry == null) {
      return ResponseEntity.internalServerError().build();
    }
    return new ResponseEntity<>(updatedEntry, OK);
  }

  @DeleteMapping("/entry/{investmentId}/{entryId}")
  public ResponseEntity<InvestmentEntry> deleteInvestmentEntry(
      @PathVariable Long investmentId, @PathVariable Long entryId) {
    log.info("Delete entry endpoint called");
    var deletedEntry = investmentService.deleteInvestmentEntry(investmentId, entryId);

    if (deletedEntry == null) {
      log.debug("Answering with no content to delete investment entry call");
      return ResponseEntity.noContent().build();
    }

    return new ResponseEntity<>(deletedEntry, OK);
  }

  @GetMapping("/summary")
  public ResponseEntity<Summary> getSummary() {
    log.info("Get summary endpoint called");
    var summary = investmentService.getSummary();

    if (summary == null) {
      log.debug("Answering with internal server error to get summary call");
      return ResponseEntity.internalServerError().build();
    }

    return new ResponseEntity<>(summary, OK);
  }
}
