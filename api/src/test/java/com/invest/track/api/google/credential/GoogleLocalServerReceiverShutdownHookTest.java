package com.invest.track.api.google.credential;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GoogleLocalServerReceiverShutdownHookTest {

  @Mock private LocalServerReceiver receiver;

  @InjectMocks private GoogleLocalServerReceiverShutdownHook hook;

  @Test
  public void testRunCallsReceiverStop() throws Exception {
    hook.run();

    verify(receiver).stop();
    verifyNoMoreInteractions(receiver);
  }
}
