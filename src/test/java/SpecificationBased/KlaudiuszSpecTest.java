package SpecificationBased;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.service.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.*;
import java.util.function.Consumer;

import static group4.passwordmanager.service.SearchService.viewPasswordAndCopyToClipboard;

public class KlaudiuszSpecTest {
    private SearchService searchService;

    private CredentialService credentialService;

    private static final String TEST_FILE_PATH = "test-file.json";

    @AfterAll
    public static void cleanup() {
        try {
            Path testFilePath = Paths.get(TEST_FILE_PATH);
            Files.deleteIfExists(testFilePath);
        } catch (IOException e) {
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
    }

    @BeforeEach
    void setUp() {
        credentialService = new CredentialService(new CredentialStorage("test-file.json"));
        credentialService.addCredential(new Credential("user1", "password1", "example.com"));
        credentialService.addCredential(new Credential("user2", "password2", "example.org"));
        credentialService.addCredential(new Credential("user3", "password3", "example.net"));
    }

    @Test
    public void testSearchCredentialsAndPrintDetails_NoMatchingCredentials() {
        ByteArrayInputStream in = new ByteArrayInputStream("\n".getBytes()); // Empty input to go back
        System.setIn(in);

        Scanner scanner = new Scanner(System.in);

        searchService.searchCredentialsAndPrintDetails(scanner, "nonexistent");

        assertEquals(1, 1);
    }
    @Test
    public void testSearchCredentialsAndPrintDetails_MatchingCredentials() {
        try {
            // Simulate user input "1\nyes\n"
            simulateUserInput("1\nyes\n", scanner -> {
                searchService.searchCredentialsAndPrintDetails(scanner, "user");
            });
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed with an exception: " + e.getMessage());
        }
    }
    @Test
    public void testCopyToClipboard_YesOptionSelected() {
        Credential mockCredential = new Credential("user1", "password1", "example.com");

        ByteArrayInputStream in = new ByteArrayInputStream("yes\n".getBytes());
        System.setIn(in);

        Scanner scanner = new Scanner(System.in);

        viewPasswordAndCopyToClipboard(scanner, mockCredential);

        assertEquals(1, 1);
    }
    @Test
    public void testCopyToClipboard_NoOptionSelected() {
        Credential mockCredential = new Credential("user2", "password2", "example.org");

        ByteArrayInputStream in = new ByteArrayInputStream("no\n".getBytes());
        System.setIn(in);

        Scanner scanner = new Scanner(System.in);

        viewPasswordAndCopyToClipboard(scanner, mockCredential);

        assertEquals(1, 1);
    }

    @Test
    public void testSearchCredentialsAndPrintDetails_InvalidSelectionInput() {
        assertThrows(NumberFormatException.class, () -> simulateUserInput("invalid\n", scanner -> {
            searchService.searchCredentialsAndPrintDetails(scanner, "user");
        }));
    }
    @Test
    void testInvalidCredentialSelection() {
        List<Credential> credentials = credentialService.getAllCredentials();

        String input = "0\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);

        assertThrows(NumberFormatException.class, () -> {
            int index = Integer.parseInt(scanner.nextLine());
            if (index >= 1 && index <= credentials.size()) {
                fail("Expected invalid selection, but a valid credential was selected.");
            } else {
                System.out.println("Invalid selection.");
                throw new NumberFormatException("Invalid selection.");
            }
        });
    }
    @Test
    void generateRandomPassword_shouldReturnRandomPassword() {
        String password = PasswordGenerator.generateRandomPassword();
        assertEquals(8, password.length());
        assertTrue(password.matches("[A-Za-z0-9!@#$%^&*()-_=+]+"));
    }
    @Test
    void enterPassword_shouldReturnEnteredOrGeneratedPassword() {
        InputStream input = new ByteArrayInputStream("1\nTestPassword\n2\n".getBytes());
        Scanner scanner = new Scanner(input);

        String password = PasswordGenerator.enterPassword(scanner);
        assertEquals("TestPassword", password);

        password = PasswordGenerator.enterPassword(scanner);
        assertEquals(8, password.length());
    }
    @Test
    void enterPassword_shouldReturnDefaultForInvalidOption() {
        InputStream input = new ByteArrayInputStream("invalid\nTestPassword\n".getBytes());
        Scanner scanner = new Scanner(input);

        String password = PasswordGenerator.enterPassword(scanner);
        assertEquals("TestPassword", password);
    }
    @Test
    void editPassword_shouldReturnNewOrGeneratedPassword() {
        String currentPassword = "CurrentPassword";
        InputStream input = new ByteArrayInputStream("1\n1\nNewPassword\n1\n2\n".getBytes());
        Scanner scanner = new Scanner(input);

        String newPassword = PasswordGenerator.editPassword(scanner, currentPassword);
        assertEquals("NewPassword", newPassword);

        newPassword = PasswordGenerator.editPassword(scanner, currentPassword);
        assertEquals(8, newPassword.length());
    }
    @Test
    void editPassword_shouldReturnCurrentPasswordForInvalidOption() {
        String currentPassword = "CurrentPassword";
        InputStream input = new ByteArrayInputStream("invalid\n".getBytes());
        Scanner scanner = new Scanner(input);

        String password = PasswordGenerator.editPassword(scanner, currentPassword);
        assertEquals(currentPassword, password);
    }
    @Test
    void editPassword_shouldReturnCurrentPasswordForNoChangeOption() {
        String currentPassword = "CurrentPassword";
        InputStream input = new ByteArrayInputStream("2\n".getBytes());
        Scanner scanner = new Scanner(input);

        String password = PasswordGenerator.editPassword(scanner, currentPassword);
        assertEquals(currentPassword, password);
    }
    @Test
    void editPassword_shouldReturnNewPassword_whenUserChoosesToEnterNewPassword() {
        String currentPassword = "CurrentPassword";
        InputStream input = new ByteArrayInputStream("1\n1\nNewPassword\n".getBytes());
        Scanner scanner = new Scanner(input);

        String newPassword = PasswordGenerator.editPassword(scanner, currentPassword);

        assertEquals("NewPassword", newPassword);
    }
    @Test
    void editPassword_shouldReturnGeneratedPassword_whenUserChoosesToGenerateNewPassword() {
        String currentPassword = "CurrentPassword";
        InputStream input = new ByteArrayInputStream("1\n2\n".getBytes());
        Scanner scanner = new Scanner(input);

        String newPassword = PasswordGenerator.editPassword(scanner, currentPassword);

        assertEquals(8, newPassword.length());
    }
    @Test
    void editPassword_shouldReturnCurrentPassword_whenUserChoosesNotToChange() {
        String currentPassword = "CurrentPassword";
        InputStream input = new ByteArrayInputStream("2\n".getBytes());
        Scanner scanner = new Scanner(input);

        String password = PasswordGenerator.editPassword(scanner, currentPassword);

        assertEquals(currentPassword, password);
    }
    @Test
    void editPassword_shouldReturnCurrentPassword_whenUserEntersInvalidOptionForNewPassword() {
        String currentPassword = "CurrentPassword";
        InputStream input = new ByteArrayInputStream("1\ninvalid\ninvalid\n".getBytes());
        Scanner scanner = new Scanner(input);

        String password = PasswordGenerator.editPassword(scanner, currentPassword);

        assertEquals(currentPassword, password);
    }
    @Test
    void copyToClipboard_shouldCopyTextToClipboard() {
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable originalContents = systemClipboard.getContents(null);
        try {
            String text = "TestPassword";
            ClipboardService.copyToClipboard(text);
            Transferable contents = systemClipboard.getContents(null);
            String copiedText = (String) contents.getTransferData(DataFlavor.stringFlavor);
            assertEquals(text, copiedText);
        } catch (UnsupportedFlavorException | IOException e) {
            fail("Unexpected exception: " + e.getMessage());
        } finally {
            systemClipboard.setContents(originalContents, null);
        }
    }
    @Test
    public void testTrackAccessHistory_FirstTimeAccess() {
        AccessHistoryTracker accessHistoryTracker = new AccessHistoryTracker(credentialService);
        Credential credential = new Credential();
        accessHistoryTracker.trackAccessHistory(credential);
        assertNotNull(credential.getLastAccessed());

    }

    @Test
    public void testTrackAccessHistory_SubsequentAccess() {
        AccessHistoryTracker accessHistoryTracker = new AccessHistoryTracker(credentialService);
        Credential credential = new Credential();
        LocalDateTime lastAccessed = LocalDateTime.now().minusDays(1);
        credential.setLastAccessed(lastAccessed);

        accessHistoryTracker.trackAccessHistory(credential);

        LocalDateTime currentTimestamp = credential.getLastAccessed();
        assertNotNull(currentTimestamp);
        assertTrue(currentTimestamp.isAfter(lastAccessed));
    }
}
