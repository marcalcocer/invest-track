package com.invest.track.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Slf4j
public class HeartbeatController {
  @GetMapping("/heartbeat")
  public ResponseEntity<String> heartbeat() {
    log.debug("Heartbeat sent!");
    return ResponseEntity.ok().build();
  }
}
