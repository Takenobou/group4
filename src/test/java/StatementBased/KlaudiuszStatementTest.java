package StatementBased;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.service.AccessHistoryTracker;
import group4.passwordmanager.service.CredentialService;
import group4.passwordmanager.service.PasswordGenerator;
import group4.passwordmanager.service.SearchService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;


public class KlaudiuszStatementTest {
    private SearchService searchService;

    private AccessHistoryTracker accessHistoryTracker;

    private static final String TEST_FILE_PATH = "test-file.json";

    @AfterAll
    public static void cleanup() {
        try {
            Path testFilePath = Paths.get(TEST_FILE_PATH);
            Files.deleteIfExists(testFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void simulateUserInput(String input, Consumer<Scanner> test) {
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(inputStream);

        try {
            test.accept(scanner);
        } finally {
            scanner.close();
        }
    }

    @BeforeEach
    public void setup() {
        CredentialService credentialService = new CredentialService(new CredentialStorage("test-file.json"));
        List<Credential> mockCredentials = new ArrayList<>();
        mockCredentials.add(new Credential("user1", "password1", "example.com"));
        mockCredentials.add(new Credential("user2", "password2", "example.org"));
        credentialService.addCredential(new Credential("user3", "password3", "example.net"));

        searchService = new SearchService(credentialService);
        accessHistoryTracker = new AccessHistoryTracker(credentialService);
    }

    @Test
    public void testSearchCredentialsAndPrintDetails_NoMatchingCredentials() {
        // Simulate user input with an empty line
        simulateUserInput("\n", scanner -> {
            searchService.searchCredentialsAndPrintDetails(scanner, "nonexistent");
        });
    }

    @Test
    public void testSearchCredentialsAndPrintDetails_MatchingCredentials() {
        // Simulate user input "1\nyes\n"
        simulateUserInput("1\nyes\n", scanner -> {
            searchService.searchCredentialsAndPrintDetails(scanner, "user");
        });
    }

    @Test
    public void testViewPasswordAndCopyToClipboard_CopyPassword() {
        // Simulate user input "yes\n"
        simulateUserInput("yes\n", scanner -> {
            Credential credential = new Credential();
            SearchService.viewPasswordAndCopyToClipboard(scanner, credential);
        });
    }

    @Test
    public void testViewPasswordAndCopyToClipboard_NoCopy() {
        // Simulate user input "no\n"
        simulateUserInput("no\n", scanner -> {
            Credential credential = new Credential();
            SearchService.viewPasswordAndCopyToClipboard(scanner, credential);
        });
    }

    @Test
    public void testSearchCredentials_InvalidSelection() {
        assertThrows(NumberFormatException.class, () -> simulateUserInput("invalid\n", scanner -> {
            searchService.searchCredentialsAndPrintDetails(scanner, "user");
        }));
    }
    @Test
    public void testGenerateRandomPassword() {
        String password = PasswordGenerator.generateRandomPassword();
        assertEquals(8, password.length());
    }

    @Test
    public void testEnterPassword_EnterOwnPassword() {
        // Simulate user input "1\nuserPassword\n"
        simulateUserInput("1\nuserPassword\n", scanner -> {
            String result = PasswordGenerator.enterPassword(scanner);
            assertEquals("userPassword", result);
        });
    }

    @Test
    public void testEnterPassword_GenerateRandomPassword() {
        // Simulate user input "2\n"
        simulateUserInput("2\n", scanner -> {
            String result = PasswordGenerator.enterPassword(scanner);
            assertEquals(8, result.length());
        });
    }

    @Test
    public void testEnterPassword_InvalidOption() {
        // Simulate user input "invalid\nuserPassword\n"
        simulateUserInput("invalid\nuserPassword\n", scanner -> {
            String result = PasswordGenerator.enterPassword(scanner);
            assertEquals("userPassword", result);
        });
    }

    @Test
    public void testEditPassword_ChangePassword_EnterNewPassword() {
        // Simulate user input "1\n1\nnewPassword\n"
        simulateUserInput("1\n1\nnewPassword\n", scanner -> {
            String result = PasswordGenerator.editPassword(scanner, "currentPassword");
            assertEquals("newPassword", result);
        });
    }

    @Test
    public void testEditPassword_ChangePassword_GenerateNewPassword() {
        // Simulate user input "1\n2\n"
        simulateUserInput("1\n2\n", scanner -> {
            String result = PasswordGenerator.editPassword(scanner, "currentPassword");
            assertEquals(8, result.length());
        });
    }

    @Test
    public void testEditPassword_ChangePassword_InvalidOption() {
        // Simulate user input "1\ninvalid\ninvalid\n"
        simulateUserInput("1\ninvalid\ninvalid\n", scanner -> {
            String result = PasswordGenerator.editPassword(scanner, "currentPassword");
            assertEquals("currentPassword", result);
        });
    }

    @Test
    public void testEditPassword_KeepCurrentPassword() {
        // Simulate user input "2\n"
        simulateUserInput("2\n", scanner -> {
            String result = PasswordGenerator.editPassword(scanner, "currentPassword");
            assertEquals("currentPassword", result);
        });
    }

    @Test
    public void testEditPassword_InvalidChangeOption() {
        // Simulate user input "invalid\n"
        simulateUserInput("invalid\n", scanner -> {
            String result = PasswordGenerator.editPassword(scanner, "currentPassword");
            assertEquals("currentPassword", result);
        });
    }
    @Test
    public void testTrackAccessHistory_FirstAccess() {
        // Simulate user input with an empty line
        simulateUserInput("\n", inputStream -> {
            Credential credential = new Credential();
            accessHistoryTracker.trackAccessHistory(credential);
        });
    }

    @Test
    public void testTrackAccessHistory_SubsequentAccess() {
        // Simulate user input "1\n"
        simulateUserInput("1\n", inputStream -> {
            Credential credential = new Credential();
            credential.setLastAccessed(LocalDateTime.now().minusDays(1));  // Set a past lastAccessed time
            accessHistoryTracker.trackAccessHistory(credential);
        });
    }

    @Test
    public void testTrackAccessHistory_NullLastAccessed() {
        // Simulate user input "1\n"
        simulateUserInput("1\n", inputStream -> {
            Credential credential = new Credential();
            credential.setLastAccessed(null);
            accessHistoryTracker.trackAccessHistory(credential);
        });
    }
}
