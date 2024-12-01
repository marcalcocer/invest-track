package com.investTrack.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

public class HeartBeatControllerTest {

  private final HeartbeatController controller = new HeartbeatController();

  @Test
  public void testHeartbeat_ShouldReturnOkResponseEntity() {
    var response = controller.heartbeat();
    assertEquals(ResponseEntity.ok().build(), response);
  }
}
