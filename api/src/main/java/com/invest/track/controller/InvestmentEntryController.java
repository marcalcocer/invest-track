package com.invest.track.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.invest.track.model.InvestmentEntry;
import com.invest.track.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/investments/entry")
public class InvestmentEntryController {
  private final InvestmentService investmentService;

  @PostMapping("/{id}")
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

  @PutMapping("/{investmentId}/{entryId}")
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

  @DeleteMapping("/{investmentId}/{entryId}")
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
}
