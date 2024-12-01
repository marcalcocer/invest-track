package com.investTrack.api.google;

import static com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport;
import static java.util.Collections.singletonList;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GoogleSheetsCredentialService {
  private final int port;

  private static final String CREDENTIALS_RES_PATH = "credentials";
  private static final String CREDENTIALS_FILE_NAME = "credentials.json";

  private static final List<String> SCOPES = singletonList(SheetsScopes.SPREADSHEETS_READONLY);

  public Sheets createSheetsService(String applicationName)
      throws GeneralSecurityException, IOException {
    var jsonFactory = GsonFactory.getDefaultInstance();
    var httpTransport = getHttpTransport();
    var credentials = getCredentials(httpTransport);

    return new Sheets.Builder(httpTransport, jsonFactory, credentials)
        .setApplicationName(applicationName)
        .build();
  }

  /**
   * Creates an authorized Credential object.
   *
   * @param HTTP_TRANSPORT The network HTTP Transport.
   * @return An authorized Credential object.
   * @throws IOException If the credentials.json file cannot be found.
   */
  protected Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
    var jsonFactory = GsonFactory.getDefaultInstance();

    var credentialsInputStreamReader = getCredentialsInputStreamReader();
    var clientSecrets = GoogleClientSecrets.load(jsonFactory, credentialsInputStreamReader);

    var resPath = getResourcesFileDataStoreFactory();

    // Build flow and trigger user authorization request.
    var flow =
        new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, jsonFactory, clientSecrets, SCOPES)
            .setDataStoreFactory(resPath)
            .setAccessType("offline")
            .build();
    var receiver = new LocalServerReceiver.Builder().setPort(port).build();
    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
  }

  private InputStreamReader getCredentialsInputStreamReader() throws FileNotFoundException {
    var credentialsFullPath = "/" + CREDENTIALS_RES_PATH + "/" + CREDENTIALS_FILE_NAME;
    var in = getCredentialsInputStream(credentialsFullPath);
    if (in == null) {
      throw new FileNotFoundException("Resource not found: " + credentialsFullPath);
    }
    return new InputStreamReader(in);
  }

  private FileDataStoreFactory getResourcesFileDataStoreFactory() throws IOException {
    var resUrl = getResourcesPathUrl();
    if (resUrl == null) {
      throw new IllegalStateException("Resources directory not found");
    }
    var resPath = resUrl.getPath();

    return getFileDataStoreFactory(resPath);
  }

  protected NetHttpTransport getHttpTransport() throws GeneralSecurityException, IOException {
    return newTrustedTransport();
  }

  protected InputStream getCredentialsInputStream(String path) {
    return getClass().getResourceAsStream(path);
  }

  protected URL getResourcesPathUrl() {
    return getClass().getClassLoader().getResource("");
  }

  protected FileDataStoreFactory getFileDataStoreFactory(String path) throws IOException {
    return new FileDataStoreFactory(new File(path));
  }
}
