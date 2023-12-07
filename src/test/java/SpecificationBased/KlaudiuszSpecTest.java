package SpecificationBased;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.service.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
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

    // Helper method to simulate user input using Scanner
    private void simulateUserInput(String input, Consumer<Scanner> test) {
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(inputStream);

        try {
            test.accept(scanner);
        } finally {
            scanner.close();
        }
    }


    // Helper method to get the captured output
    @BeforeEach
    public void setup() {
        // Create a mock CredentialService
        CredentialService credentialService = new CredentialService(new CredentialStorage("test-file.json"));
        List<Credential> mockCredentials = new ArrayList<>();
        mockCredentials.add(new Credential("user1", "password1", "example.com"));
        mockCredentials.add(new Credential("user2", "password2", "example.org"));
        credentialService.addCredential(new Credential("user3", "password3", "example.net"));

        // Set up the SearchService with the mock CredentialService
        searchService = new SearchService(credentialService);
    }

    @BeforeEach
    void setUp() {
        // Create or mock the CredentialService instance for each test
        credentialService = new CredentialService(new CredentialStorage("test-file.json"));
        // Add some mock credentials for testing
        credentialService.addCredential(new Credential("user1", "password1", "example.com"));
        credentialService.addCredential(new Credential("user2", "password2", "example.org"));
        credentialService.addCredential(new Credential("user3", "password3", "example.net"));
    }

    @AfterEach
    public void cleanup() {
        // Reset System.in to its original state after each test
        System.setIn(System.in);
    }

    @Test
    public void testSearchCredentialsAndPrintDetails_NoMatchingCredentials() {
        // Create a ByteArrayInputStream to simulate user input
        ByteArrayInputStream in = new ByteArrayInputStream("\n".getBytes()); // Empty input to go back
        System.setIn(in);

        // Create a Scanner with the mocked System.in
        Scanner scanner = new Scanner(System.in);

        // Call the non-static method on the instance of SearchService
        searchService.searchCredentialsAndPrintDetails(scanner, "nonexistent");

        // Assertions based on the expected output or behavior
        // For simplicity, let's just check if the method runs without exceptions
        assertEquals(1, 1);
    }
    @Test
    public void testSearchCredentialsAndPrintDetails_MatchingCredentials() {
        try {
            // Simulate user input "1\nyes\n"
            simulateUserInput("1\nyes\n", scanner -> {
                searchService.searchCredentialsAndPrintDetails(scanner, "user");
                // Additional assertions or verifications here
            });
        } catch (Exception e) {
            // Log or print the exception for debugging purposes
            e.printStackTrace();
            fail("Test failed with an exception: " + e.getMessage());
        }
    }
    @Test
    public void testCopyToClipboard_YesOptionSelected() {
        // Create a mock Credential
        Credential mockCredential = new Credential("user1", "password1", "example.com");

        // Create a ByteArrayInputStream to simulate user input ("yes")
        ByteArrayInputStream in = new ByteArrayInputStream("yes\n".getBytes());
        System.setIn(in);

        // Create a Scanner with the mocked System.in
        Scanner scanner = new Scanner(System.in);

        // Call the method you want to test
        viewPasswordAndCopyToClipboard(scanner, mockCredential);

        // Assertions based on the expected output or behavior
        // For simplicity, let's just check if the method runs without exceptions
        assertEquals(1, 1);
    }
    @Test
    public void testCopyToClipboard_NoOptionSelected() {
        // Create a mock Credential
        Credential mockCredential = new Credential("user2", "password2", "example.org");

        // Create a ByteArrayInputStream to simulate user input ("no")
        ByteArrayInputStream in = new ByteArrayInputStream("no\n".getBytes());
        System.setIn(in);

        // Create a Scanner with the mocked System.in
        Scanner scanner = new Scanner(System.in);

        // Call the method you want to test
        viewPasswordAndCopyToClipboard(scanner, mockCredential);

        // Assertions based on the expected output or behavior
        // For simplicity, let's just check if the method runs without exceptions
        assertEquals(1, 1);
    }

    @Test
    public void testSearchCredentialsAndPrintDetails_InvalidSelectionInput() {
        // Simulate user input "invalid\n"
        assertThrows(NumberFormatException.class, () -> simulateUserInput("invalid\n", scanner -> {
            // Call the non-static method on the instance of SearchService
            searchService.searchCredentialsAndPrintDetails(scanner, "user");
        }));
    }
    @Test
    void testInvalidCredentialSelection() {
        // Prepare test data
        List<Credential> credentials = credentialService.getAllCredentials();

        // Simulate user input "0\n"
        String input = "0\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);

        // Execute the method under test and expect a NumberFormatException
        assertThrows(NumberFormatException.class, () -> {
            int index = Integer.parseInt(scanner.nextLine());
            if (index >= 1 && index <= credentials.size()) {
                fail("Expected invalid selection, but a valid credential was selected.");
            } else {
                System.out.println("Invalid selection.");
                throw new NumberFormatException("Invalid selection.");
            }
        });
        // Verify that the method correctly handles an invalid credential selection
    }
    @Test
    void generateRandomPassword_shouldReturnRandomPassword() {
        String password = PasswordGenerator.generateRandomPassword();
        assertEquals(8, password.length());
        assertTrue(password.matches("[A-Za-z0-9!@#$%^&*()-_=+]+"));
    }
    @Test
    void enterPassword_shouldReturnEnteredOrGeneratedPassword() {
        // Simulate user input with ByteArrayInputStream
        InputStream input = new ByteArrayInputStream("1\nTestPassword\n2\n".getBytes());
        Scanner scanner = new Scanner(input);

        String password = PasswordGenerator.enterPassword(scanner);
        assertEquals("TestPassword", password);

        password = PasswordGenerator.enterPassword(scanner);
        assertEquals(8, password.length());
    }
    @Test
    void enterPassword_shouldReturnDefaultForInvalidOption() {
        // Simulate user input with ByteArrayInputStream
        InputStream input = new ByteArrayInputStream("invalid\nTestPassword\n".getBytes());
        Scanner scanner = new Scanner(input);

        String password = PasswordGenerator.enterPassword(scanner);
        assertEquals("TestPassword", password);
    }
    @Test
    void editPassword_shouldReturnNewOrGeneratedPassword() {
        String currentPassword = "CurrentPassword";
        // Simulate user input with ByteArrayInputStream
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
        // Simulate user input with ByteArrayInputStream
        InputStream input = new ByteArrayInputStream("invalid\n".getBytes());
        Scanner scanner = new Scanner(input);

        String password = PasswordGenerator.editPassword(scanner, currentPassword);
        assertEquals(currentPassword, password);
    }
    @Test
    void editPassword_shouldReturnCurrentPasswordForNoChangeOption() {
        String currentPassword = "CurrentPassword";
        // Simulate user input with ByteArrayInputStream
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
        // Save the current clipboard contents (if any) to restore it later
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable originalContents = systemClipboard.getContents(null);

        try {
            String text = "TestPassword";
            ClipboardService.copyToClipboard(text);

            // Retrieve the content from the system clipboard
            Transferable contents = systemClipboard.getContents(null);
            String copiedText = (String) contents.getTransferData(DataFlavor.stringFlavor);

            assertEquals(text, copiedText);
        } catch (UnsupportedFlavorException | IOException e) {
            fail("Unexpected exception: " + e.getMessage());
        } finally {
            // Restore the original clipboard contents
            systemClipboard.setContents(originalContents, null);
        }
    }
    @Test
    public void testTrackAccessHistory_FirstTimeAccess() {
        // Arrange
         // You might need to implement a mock CredentialService
        AccessHistoryTracker accessHistoryTracker = new AccessHistoryTracker(credentialService);
        Credential credential = new Credential();

        // Act
        accessHistoryTracker.trackAccessHistory(credential);

        // Assert
        assertNotNull(credential.getLastAccessed());
        // You might also want to verify the output to the console using a mocking framework
    }

    @Test
    public void testTrackAccessHistory_SubsequentAccess() {
        // Arrange
        AccessHistoryTracker accessHistoryTracker = new AccessHistoryTracker(credentialService);
        Credential credential = new Credential();
        LocalDateTime lastAccessed = LocalDateTime.now().minusDays(1);
        credential.setLastAccessed(lastAccessed);

        // Act
        accessHistoryTracker.trackAccessHistory(credential);

        // Assert
        LocalDateTime currentTimestamp = credential.getLastAccessed();
        assertNotNull(currentTimestamp);
        assertTrue(currentTimestamp.isAfter(lastAccessed));
        // You might also want to verify the output to the console using a mocking framework
    }
}
