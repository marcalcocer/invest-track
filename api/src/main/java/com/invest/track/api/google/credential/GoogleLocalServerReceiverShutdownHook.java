package com.invest.track.api.google.credential;

import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class GoogleLocalServerReceiverShutdownHook extends Thread {
  private final LocalServerReceiver receiver;

  @Override
  public void run() {
    try {
      receiver.stop();
    } catch (Exception e) {
      log.warn("Failed to stop LocalServerReceiver on shutdown", e);
    }
  }
}
