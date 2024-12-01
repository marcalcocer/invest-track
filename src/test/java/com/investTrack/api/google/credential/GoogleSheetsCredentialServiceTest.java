package com.investTrack.api.google.credential;

import static com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.sheets.v4.Sheets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class GoogleSheetsCredentialServiceTest {

  private final GoogleSheetsCredentialService service = new GoogleSheetsCredentialService(1234);
  private final GoogleSheetsCredentialService spy = spy(service);

  @Test
  public void testCreateSheetsService_ShouldReturnNewSheets()
      throws IOException, GeneralSecurityException {
    var credentials = mock(Credential.class);
    doReturn(credentials).when(spy).getCredentials(any());

    var sheets = spy.createSheetsService("test");

    assertInstanceOf(Sheets.class, sheets);
    assertEquals("test", sheets.getApplicationName());
  }

  public static Stream<Arguments> parametersForTestCreateSheetsService() {
    return Stream.of(
        Arguments.of(new GeneralSecurityException("test")), Arguments.of(new IOException("test")));
  }

  @ParameterizedTest
  @MethodSource("parametersForTestCreateSheetsService")
  public void testCreateSheetsService_ShouldThrowException(Exception expectedException)
      throws GeneralSecurityException, IOException {
    doThrow(expectedException).when(spy).getHttpTransport();

    var exception =
        assertThrows(expectedException.getClass(), () -> spy.createSheetsService("test"));

    assertEquals("test", exception.getMessage());
  }

  @Test
  public void testGetCredentials_ShouldThrowIllegalStateException_WhenResourceDirectoryNotFound() {
    doReturn(null).when(spy).getResourcesPathUrl();

    var exception = assertThrows(IllegalStateException.class, () -> spy.getCredentials(null));

    assertEquals("Resources directory not found", exception.getMessage());
  }

  @Test
  public void testGetCredentials_ShouldThrowIOException_WhenErrorCreatingFileDataStoreFactory()
      throws IOException {
    doThrow(new IOException("test")).when(spy).getFileDataStoreFactory(any());

    var exception = assertThrows(IOException.class, () -> spy.getCredentials(null));

    verify(spy).getFileDataStoreFactory(any());

    assertEquals("test", exception.getMessage());
  }

  @Test
  public void testGetCredentials_ShouldThrowFileNotFoundException_WhenFileNotFound() {
    doReturn(null).when(spy).getCredentialsInputStream(any());

    var exception = assertThrows(FileNotFoundException.class, () -> spy.getCredentials(null));

    assertEquals("Resource not found: /credentials/credentials.json", exception.getMessage());

    verify(spy).getCredentialsInputStream(eq("/credentials/credentials.json"));
  }

  @Test
  public void testGetCredentials_ShouldReturnACredentialObject()
      throws GeneralSecurityException, IOException {
    var transport = newTrustedTransport();

    var credential = spy.getCredentials(transport);

    assertInstanceOf(Credential.class, credential);
  }
}
