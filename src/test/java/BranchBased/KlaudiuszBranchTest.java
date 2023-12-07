package BranchBased;
import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.service.AccessHistoryTracker;
import group4.passwordmanager.service.CredentialService;
import group4.passwordmanager.service.PasswordGenerator;
import group4.passwordmanager.service.SearchService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

import static group4.passwordmanager.service.ClipboardService.copyToClipboard;
import static group4.passwordmanager.service.PasswordGenerator.editPassword;
import static group4.passwordmanager.service.PasswordGenerator.enterPassword;
import static org.junit.jupiter.api.Assertions.*;

public class KlaudiuszBranchTest {
    private final PrintStream originalSystemOut = System.out;
    private final InputStream originalSystemIn = System.in;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOutput = System.out;
    @BeforeEach
    public void setUp() {
        // Redirect System.out to capture the output
        System.setOut(new PrintStream(originalSystemOut));
        System.setOut(new PrintStream(outputStreamCaptor));
    }
    @AfterEach
    public void tearDown() {
        // Reset System.out to its original state
        System.setOut(originalSystemOut);
        System.setIn(originalSystemIn);

        System.setOut(originalOutput);
    }

    // Helper method to simulate user input using Scanner
    private String simulateUserInput(String input, Consumer<Scanner> test) {
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());

        try (Scanner scanner = new Scanner(inputStream)) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outputStream));

            test.accept(scanner);

            return outputStream.toString().trim();
        }
    }
    private static class TestCredentialService extends CredentialService {

        public TestCredentialService(List<Credential> credentials) {
            super(new TestCredentialStorage(credentials));
        }

        // Custom implementation of CredentialStorage for testing
        private static class TestCredentialStorage extends CredentialStorage {

            public TestCredentialStorage(List<Credential> credentials) {
                super("test-file.json"); // Provide a valid path for testing
                // Initialize the storage with the provided credentials
                for (Credential credential : credentials) {
                    super.store(credential);
                }
            }


            // Override other methods as needed for testing
        }
    }
    @Test
    public void testSearchCredentialsAndPrintDetails_NoMatchingCredentials() {
        // Arrange
        CredentialService credentialService = new TestCredentialService(Collections.emptyList());
        SearchService searchService = new SearchService(credentialService);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("\n".getBytes());
        Scanner scanner = new Scanner(inputStream);

        // Redirect System.out to capture the output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // Act
        searchService.searchCredentialsAndPrintDetails(scanner, "nonexistent");

        // Assert
        assertTrue(outputStream.toString().contains("No matching credentials found."));

        // Reset System.out to its original state
        System.setOut(originalSystemOut);
    }

    @Test
    public void testSearchCredentialsAndPrintDetails_MatchingCredentials_ValidSelection() {
        // Arrange
        CredentialService credentialService = new TestCredentialService(createCredentials());
        SearchService searchService = new SearchService(credentialService);

        // Act
        String userInput = "1\nyes\n";
        simulateUserInput(userInput, scanner -> searchService.searchCredentialsAndPrintDetails(scanner, "user"));
    }

    @Test
    public void testSearchCredentialsAndPrintDetails_MatchingCredentials_InvalidSelection() {
        // Arrange
        CredentialService credentialService = new TestCredentialService(createCredentials());
        SearchService searchService = new SearchService(credentialService);

        // Act
        String userInput = "invalid\n";
        assertThrows(NumberFormatException.class,
                () -> simulateUserInput(userInput, scanner -> searchService.searchCredentialsAndPrintDetails(scanner, "user"))
        );
    }
    @Test
    public void testViewPasswordAndCopyToClipboard_CopyOptionYes() {
        // Arrange
        Credential credential = new Credential("user", "pass", "website");

        // Act
        String userInput = "yes\n";
        simulateUserInput(userInput, scanner -> SearchService.viewPasswordAndCopyToClipboard(scanner, credential));
    }
    @Test
    public void testViewPasswordAndCopyToClipboard_CopyOptionNo() {
        // Arrange
        Credential credential = new Credential("user", "pass", "website");

        // Act
        String userInput = "no\n";
        simulateUserInput(userInput, scanner -> SearchService.viewPasswordAndCopyToClipboard(scanner, credential));
    }
    @Test
    public void testSearchCredentials_WebsiteMatch() {
        // Arrange
        Credential credential = new Credential("user1", "pass1", "example.com");
        List<Credential> credentials = List.of(credential);
        SearchService searchService = new SearchService(new TestCredentialService(credentials));

        // Act
        List<Credential> result = searchService.searchCredentials("example.com");

        // Assert
        assertTrue(result.contains(credential), "Credential with matching website should be found");
    }
    @Test
    public void testSearchCredentials_TagsMatch() {
        // Arrange
        Credential credential = new Credential("user1", "pass1", "example.com");
        credential.setTags(Arrays.asList("tag1", "tag2"));
        List<Credential> credentials = List.of(credential);
        SearchService searchService = new SearchService(new TestCredentialService(credentials));

        // Act
        List<Credential> result = searchService.searchCredentials("tag1");

        // Assert
        assertTrue(result.contains(credential), "Credential with matching tag should be found");
    }
    @Test
    public void testSearchCredentials_NoMatch() {
        // Arrange
        Credential credential = new Credential("user1", "pass1", "example.com");
        credential.setTags(Arrays.asList("tag1", "tag2"));
        List<Credential> credentials = List.of(credential);
        SearchService searchService = new SearchService(new TestCredentialService(credentials));

        // Act
        List<Credential> result = searchService.searchCredentials("nonexistent");

        // Assert
        assertFalse(result.contains(credential), "Credential with non-matching criteria should not be found");
    }
    @Test
    public void testGenerateRandomPassword() throws NoSuchFieldException, IllegalAccessException {
        // Mocking the Scanner since it's not used in this method

        // Accessing the private field using reflection
        Field field = PasswordGenerator.class.getDeclaredField("CHARACTERS");
        field.setAccessible(true);
        String characters = (String) field.get(null);

        // Running the method multiple times to cover different random indices
        for (int i = 0; i < 1000; i++) {
            String generatedPassword = PasswordGenerator.generateRandomPassword();
            assertEquals(8, generatedPassword.length());  // Ensure the correct length

            // Check that all characters in the generated password are from the specified set
            Set<Character> validCharacters = new HashSet<>();
            for (char c : characters.toCharArray()) {
                validCharacters.add(c);
            }

            for (char c : generatedPassword.toCharArray()) {
                assertTrue(validCharacters.contains(c));
            }
        }
    }
    @Test
    void testEnterPasswordOption1() {
        Scanner scanner = new Scanner("1\nmyPassword\n");
        String password = enterPassword(scanner);
        assertEquals("myPassword", password);
    }
    @Test
    void testEnterPasswordOption2() {
        Scanner scanner = new Scanner("2\n");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        enterPassword(scanner);

        String consoleOutput = output.toString().trim();
        assertFalse(consoleOutput.startsWith("Generated Password: "));
    }
    @Test
    void testEnterPasswordInvalidOption() {
        String userInput = "invalid\nmyPassword\n";
        String expectedOutput = "Invalid option. Defaulting to your own password.\nEnter Password: myPassword";

        String result = simulateUserInput(userInput, PasswordGenerator::enterPassword);

        // Assuming enterPassword method writes to System.out.println, you can assert against it
        Assertions.assertFalse(result.contains(expectedOutput));
    }
    @Test
    void testEditPasswordChangeOption1() {
        String currentPassword = "currentPassword";
        String userInput = "0\n";

        // Modify simulateUserInput to return the output string
        String result = simulateUserInput(userInput, scanner -> editPassword(scanner, currentPassword));

        assertFalse(result.contains("Invalid option. Keeping the current password"));
    }
    @Test
    void testEditPasswordChangeOption2() {
        String currentPassword = "currentPassword";
        String userInput = "2\n";

        // Modify simulateUserInput to return the output string
        String result = simulateUserInput(userInput, scanner -> editPassword(scanner, currentPassword));

        // Check if the output contains the expected part
        assertFalse(result.contains("Returned: currentPassword"));
    }
    @Test
    void testEditPasswordInvalidChangeOption() {
        String currentPassword = "currentPassword";
        String userInput = "invalid\nmyNewPassword\n";

        // Modify simulateUserInput to return the output string
        String result = simulateUserInput(userInput, scanner -> editPassword(scanner, currentPassword));

        // Check if the output contains the expected part
        assertFalse(result.contains("Returned: currentPassword"));
    }
    @Test
    void testEditPasswordNoChangeOption() {
        String currentPassword = "currentPassword";
        String userInput = "2\n";
        String expectedOutput = "Do you want to change the password? (1) Yes, (2) No\nReturned: currentPassword";

        String result = simulateUserInput(userInput, scanner -> editPassword(scanner, currentPassword));

        Assertions.assertFalse(result.contains(expectedOutput));
    }
    @Test
    void testEditPasswordChangeOption1GenerateRandomPassword() {
        Scanner scanner = new Scanner("1\n2\n");
        String currentPassword = "existingPassword";
        String newPassword = editPassword(scanner, currentPassword);

        assertNotEquals(currentPassword, newPassword);
        assertFalse(newPassword.startsWith("Generated Password: "));
    }
    @Test
    void testEditPasswordChangeOption1EnterNewPasswordEmptyInput() {
        String currentPassword = "";
        String userInput = "1\n\n";  // Option 1, followed by Enter for empty input
        String expectedOutput = "Invalid option. Keeping the current password.";

        String result = simulateUserInput(userInput, scanner -> editPassword(scanner, currentPassword));

        // Assuming editPassword method writes to System.out.println, you can assert against it
        Assertions.assertTrue(result.contains(expectedOutput));
    }
    @Test
    void testEditPasswordChangeOption1EnterNewPasswordOutput() {
        Scanner scanner = new Scanner("1\n1\nnewPassword\n");
        String currentPassword = "existingPassword";
        String newPassword = editPassword(scanner, currentPassword);

        assertEquals("newPassword", newPassword);
    }
    @Test
    void testCopyToClipboard() {
        String text = "Test Text";
        assertDoesNotThrow(() -> copyToClipboard(text));
        // You might want to add additional verification if there's a way to check the clipboard content.
    }
    @Test
    void testCopyToClipboardWithEmptyText() {
        String text = "";
        assertDoesNotThrow(() -> copyToClipboard(text));
        // Verify that the clipboard content remains unchanged.
    }
    @Test
    void testTrackAccessHistory_FirstTimeAccess() {
        // Arrange
        CredentialService credentialService = new MockCredentialService(new TestCredentialService.TestCredentialStorage(Collections.emptyList()));
        AccessHistoryTracker accessHistoryTracker = new AccessHistoryTracker(credentialService);
        Credential credential = new Credential();

        // Redirect System.out to capture the output
        System.setOut(new PrintStream(outputStreamCaptor));

        // Act
        accessHistoryTracker.trackAccessHistory(credential);

        // Assert
        assertNotNull(credential.getLastAccessed());
        assertTrue(outputStreamCaptor.toString().trim().contains("Last accessed: This is the first time it is accessed"));

        // Reset System.out to its original state
        System.setOut(originalSystemOut);
    }
    @Test
    void testTrackAccessHistory_SubsequentAccess() {
        // Arrange
        CredentialService credentialService = new MockCredentialService(new TestCredentialService.TestCredentialStorage(Collections.emptyList()));
        AccessHistoryTracker accessHistoryTracker = new AccessHistoryTracker(credentialService);
        Credential credential = new Credential();
        LocalDateTime lastAccessed = LocalDateTime.now().minusDays(1);
        credential.setLastAccessed(lastAccessed);

        // Redirect System.out to capture the output
        System.setOut(new PrintStream(outputStreamCaptor));

        // Act
        accessHistoryTracker.trackAccessHistory(credential);

        // Assert
        LocalDateTime currentTimestamp = credential.getLastAccessed();
        assertTrue(currentTimestamp.isAfter(lastAccessed));
        assertTrue(outputStreamCaptor.toString().trim().contains("Last accessed:"));

        // Reset System.out to its original state
        System.setOut(originalSystemOut);
    }
    // Helper method to create a list of test credentials
    private List<Credential> createCredentials() {
        List<Credential> credentials = new ArrayList<>();
        credentials.add(new Credential("user1", "pass1", "website1"));
        credentials.add(new Credential("user2", "pass2", "website2"));
        return credentials;
    }

    private static class MockCredentialService extends CredentialService {
        public MockCredentialService(CredentialStorage storage) {
            super(storage);
        }
        @Override
        public void updateCredential(Credential credential) {
            // Override the updateCredential method to do nothing in the mock
        }
    }
}

