package RandomBased;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import group4.passwordmanager.service.SearchService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import group4.passwordmanager.manager.CredentialManager;
import group4.passwordmanager.model.Credential;
import group4.passwordmanager.service.AccessHistoryTracker;
import group4.passwordmanager.service.CredentialService;
import group4.passwordmanager.manager.TagManager;

//Random tests for last modified functionality.
public class LastModifiedRandomTests {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final Scanner scanner = new Scanner(System.in);

    @Mock
    private CredentialService mockCredentialService;

    @Mock
    private TagManager mockTagManager;

    @Mock
    private AccessHistoryTracker mockAccessHistoryTracker;

    @Mock
    private SearchService mockSearchService;

    @InjectMocks
    private CredentialManager credentialManager;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(outContent));

        // Set up the CredentialManager with mocked services
        credentialManager = new CredentialManager(mockCredentialService, mockTagManager,
                mockAccessHistoryTracker, mockSearchService); // Include mockSearchService
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void testViewCredentialDisplaysNeverUpdatedWarning() {
        // Create a mock credential that's more than 30 days old
        Credential outdatedCredential = new Credential("oldUser@example.com", "oldPassword", "oldWebsite",
                LocalDateTime.now().minusDays(40));

        // Mock the CredentialService to return the outdated credential
        when(mockCredentialService.getCredentialByIndex(0)).thenReturn(outdatedCredential);

        // Call the viewCredential method
        credentialManager.viewCredential(mockCredentialService, 1, mockAccessHistoryTracker);

        // Capture and assert the output
        String output = outContent.toString();
        assertTrue(output.contains("Last Modified: This credential has never been updated."),
                "Output should contain a message telling the user that their password has never been updated");
    }

    @Test
    public void testEditCredentialAndCheckLastModified() {
        Credential oldCredential = new Credential("user@example.com", "password", "website", LocalDateTime.now());

        for (int i = 0; i < 100; i++) {

            // Define the range
            int min = 31;
            int max = 90;

            // Generate random number within the range
            int randomNumber = (int)(Math.random() * (max - min + 1)) + min;


            // Create a credential that's more than 30 days old and ensure that the code considers it modified already
            oldCredential.setLastModified(LocalDateTime.now().minusDays(randomNumber));
            when(mockCredentialService.getCredentialByIndex(0)).thenReturn(oldCredential);

            // Call the viewCredential method
            credentialManager.viewCredential(mockCredentialService, 1, mockAccessHistoryTracker);

            // Capture and assert the output
            String output = outContent.toString();
            assertTrue(output.contains("Recommendation: Consider updating this password."),
                    "Output should contain a message telling the user that their password needs to be updated");
        }
    }

}
