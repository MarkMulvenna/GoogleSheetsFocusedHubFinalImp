import java.io.*;
import java.util.concurrent.TimeUnit;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.*;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

    public class GoogleSheetsIntegration {
        private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
        private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
        private static final String TOKENS_DIRECTORY_PATH = "tokens";
        private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/spreadsheets");
        private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
        private static int rateCount;
        private static int rateCountHitCount = 0;

        public GoogleSheetsIntegration() {
        }

        private static Credential getCredentials(NetHttpTransport HTTP_TRANSPORT) throws IOException {
            InputStream in = GoogleSheetsIntegration.class.getResourceAsStream("/credentials.json");
            if (in == null) {
                throw new FileNotFoundException("Resource not found: /credentials.json");
            } else {
                GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
                GoogleAuthorizationCodeFlow flow = (new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)).setDataStoreFactory(new FileDataStoreFactory(new File("tokens"))).setAccessType("offline").build();
                LocalServerReceiver receiver = (new com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver.Builder()).setPort(8888).build();
                return (new AuthorizationCodeInstalledApp(flow, receiver)).authorize("user");
            }
        }

        public static void googleSheetsMailDropHandler() throws IOException, GeneralSecurityException, SQLException {
            ArrayList<MailDropObj> mailDropObjs = (new DatabaseFunctions()).gatheredRequiredDataFromMailDropTable();
            Ranges_SpreadsheetIDs ranges_spreadsheetIDs = new Ranges_SpreadsheetIDs();
            Iterator var2 = mailDropObjs.iterator();

            while(var2.hasNext()) {
                if (rateLimit()) {
                    MailDropObj mailDropObj = (MailDropObj) var2.next();
                    Sheets service = OpenGoogleSheets();
                    List<List<Object>> values = Collections.singletonList(Arrays.asList(mailDropObj.getMailDropID(), mailDropObj.getMailDropBusinessName(), mailDropObj.getMailDropAddress(), mailDropObj.getNotes()));
                    ValueRange body = (new ValueRange()).setValues(values);
                    AppendValuesResponse result = service.spreadsheets().values().append(ranges_spreadsheetIDs.SPREADHSEETID, ranges_spreadsheetIDs.MAIL_DROP_RANGE, body).setValueInputOption("RAW").execute();
                }
            }

            DatabaseFunctions databaseFunctions = new DatabaseFunctions();
            databaseFunctions.setIsPassedToGoogleSheetsTrue("MailDrop");
        }

        public static boolean rateLimit() {
            if (rateCount < 60)
            {
                System.out.println("Rate limit not met, appending. Current Requests this minute: " + rateCount);
                rateCount++;
            }
            else {
                try {
                    rateCountHitCount = rateCountHitCount + 1;
                    if (rateCountHitCount == 5) {
                        System.out.println("Sleeping two minutes.");
                        TimeUnit.MINUTES.sleep(2);
                    }
                    else{
                        System.out.println("Sleeping one minute.");
                        TimeUnit.MINUTES.sleep(1);
                    }
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                rateCount = 0;
                rateCountHitCount = 0;

            }
            return true;

        }

        public static void googleSheetsPlacesHandler() throws IOException, GeneralSecurityException, SQLException {

            ArrayList<Places> googleSheetsObjs = (new DatabaseFunctions()).gatherRequiredDataFromPlacesTable();
            Ranges_SpreadsheetIDs ranges_spreadsheetIDs = new Ranges_SpreadsheetIDs();
            Iterator var2 = googleSheetsObjs.iterator();

            while(var2.hasNext())
            {
                if (rateLimit()) {
                    Places placesObjs = (Places) var2.next();
                    Sheets service = OpenGoogleSheets();
                    List<List<Object>> values = Collections.singletonList(Arrays.asList(placesObjs.getPlacesID(), placesObjs.getBusinessName(), placesObjs.getVicinity(), placesObjs.getLocation(), placesObjs.getTypes(), placesObjs.getOverallRating(), placesObjs.getTotalUserRating(), placesObjs.getDateAdded()));
                    ValueRange body = (new ValueRange()).setValues(values);
                    AppendValuesResponse result = service.spreadsheets().values().append(ranges_spreadsheetIDs.SPREADHSEETID, ranges_spreadsheetIDs.PLACES_RANGE, body).setValueInputOption("RAW").execute();
                    System.out.printf("%d cells appended.", result.getUpdates().getUpdatedCells());
                }
            }
            DatabaseFunctions databaseFunctions = new DatabaseFunctions();
            databaseFunctions.setIsPassedToGoogleSheetsTrue("Places");
        }

        public static void main(String... args) throws IOException, GeneralSecurityException, SQLException {

            System.out.println("Running");
            googleSheetsMailDropHandler();
            googleSheetsPlacesHandler();



        }

        private static Sheets OpenGoogleSheets() throws GeneralSecurityException, IOException {
            NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            return (new com.google.api.services.sheets.v4.Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))).setApplicationName("Google Sheets API Java Quickstart").build();
        }
    }


