package BranchBased;
import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.service.AccessHistoryTracker;
import group4.passwordmanager.service.CredentialService;
import group4.passwordmanager.service.PasswordGenerator;
import group4.passwordmanager.service.SearchService;
import org.junit.jupiter.api.*;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(originalSystemOut));
        System.setOut(new PrintStream(outputStreamCaptor));
    }
    @AfterEach
    public void tearDown() {
        System.setOut(originalSystemOut);
        System.setIn(originalSystemIn);
        System.setOut(originalOutput);
    }
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
        private static class TestCredentialStorage extends CredentialStorage {

            public TestCredentialStorage(List<Credential> credentials) {
                super("test-file.json");
                for (Credential credential : credentials) {
                    super.store(credential);
                }
            }
        }
    }
    @Test
    public void testSearchCredentialsAndPrintDetails_NoMatchingCredentials() {
        CredentialService credentialService = new TestCredentialService(Collections.emptyList());
        SearchService searchService = new SearchService(credentialService);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("\n".getBytes());
        Scanner scanner = new Scanner(inputStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        searchService.searchCredentialsAndPrintDetails(scanner, "nonexistent");

        assertTrue(outputStream.toString().contains("No matching credentials found."));

        System.setOut(originalSystemOut);
    }

    @Test
    public void testSearchCredentialsAndPrintDetails_MatchingCredentials_ValidSelection() {
        CredentialService credentialService = new TestCredentialService(createCredentials());
        SearchService searchService = new SearchService(credentialService);

        String userInput = "1\nyes\n";
        simulateUserInput(userInput, scanner -> searchService.searchCredentialsAndPrintDetails(scanner, "user"));
    }

    @Test
    public void testSearchCredentialsAndPrintDetails_MatchingCredentials_InvalidSelection() {
        CredentialService credentialService = new TestCredentialService(createCredentials());
        SearchService searchService = new SearchService(credentialService);

        String userInput = "invalid\n";
        assertThrows(NumberFormatException.class,
                () -> simulateUserInput(userInput, scanner -> searchService.searchCredentialsAndPrintDetails(scanner, "user"))
        );
    }
    @Test
    public void testViewPasswordAndCopyToClipboard_CopyOptionYes() {
        Credential credential = new Credential("user", "pass", "website");

        String userInput = "yes\n";
        simulateUserInput(userInput, scanner -> SearchService.viewPasswordAndCopyToClipboard(scanner, credential));
    }
    @Test
    public void testViewPasswordAndCopyToClipboard_CopyOptionNo() {
        Credential credential = new Credential("user", "pass", "website");

        String userInput = "no\n";
        simulateUserInput(userInput, scanner -> SearchService.viewPasswordAndCopyToClipboard(scanner, credential));
    }
    @Test
    public void testSearchCredentials_WebsiteMatch() {
        Credential credential = new Credential("user1", "pass1", "example.com");
        List<Credential> credentials = List.of(credential);
        SearchService searchService = new SearchService(new TestCredentialService(credentials));

        List<Credential> result = searchService.searchCredentials("example.com");

        assertTrue(result.contains(credential), "Credential with matching website should be found");
    }
    @Test
    public void testSearchCredentials_TagsMatch() {
        Credential credential = new Credential("user1", "pass1", "example.com");
        credential.setTags(Arrays.asList("tag1", "tag2"));
        List<Credential> credentials = List.of(credential);
        SearchService searchService = new SearchService(new TestCredentialService(credentials));

        List<Credential> result = searchService.searchCredentials("tag1");

        assertTrue(result.contains(credential), "Credential with matching tag should be found");
    }
    @Test
    public void testSearchCredentials_NoMatch() {
        Credential credential = new Credential("user1", "pass1", "example.com");
        credential.setTags(Arrays.asList("tag1", "tag2"));
        List<Credential> credentials = List.of(credential);
        SearchService searchService = new SearchService(new TestCredentialService(credentials));

        List<Credential> result = searchService.searchCredentials("nonexistent");

        assertFalse(result.contains(credential), "Credential with non-matching criteria should not be found");
    }
    @Test
    public void testGenerateRandomPassword() throws NoSuchFieldException, IllegalAccessException {
        Field field = PasswordGenerator.class.getDeclaredField("CHARACTERS");
        field.setAccessible(true);
        String characters = (String) field.get(null);

        for (int i = 0; i < 1000; i++) {
            String generatedPassword = PasswordGenerator.generateRandomPassword();
            assertEquals(8, generatedPassword.length());

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
        Assertions.assertFalse(result.contains(expectedOutput));
    }
    @Test
    void testEditPasswordChangeOption1() {
        String currentPassword = "currentPassword";
        String userInput = "0\n";

        String result = simulateUserInput(userInput, scanner -> editPassword(scanner, currentPassword));

        assertFalse(result.contains("Invalid option. Keeping the current password"));
    }
    @Test
    void testEditPasswordChangeOption2() {
        String currentPassword = "currentPassword";
        String userInput = "2\n";

        String result = simulateUserInput(userInput, scanner -> editPassword(scanner, currentPassword));

        assertFalse(result.contains("Returned: currentPassword"));
    }
    @Test
    void testEditPasswordInvalidChangeOption() {
        String currentPassword = "currentPassword";
        String userInput = "invalid\nmyNewPassword\n";

        String result = simulateUserInput(userInput, scanner -> editPassword(scanner, currentPassword));

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
        String userInput = "1\n\n";
        String expectedOutput = "Invalid option. Keeping the current password.";

        String result = simulateUserInput(userInput, scanner -> editPassword(scanner, currentPassword));

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
    }
    @Test
    void testCopyToClipboardWithEmptyText() {
        String text = "";
        assertDoesNotThrow(() -> copyToClipboard(text));
    }
    @Test
    void testTrackAccessHistory_FirstTimeAccess() {
        CredentialService credentialService = new MockCredentialService(new TestCredentialService.TestCredentialStorage(Collections.emptyList()));
        AccessHistoryTracker accessHistoryTracker = new AccessHistoryTracker(credentialService);
        Credential credential = new Credential();

        System.setOut(new PrintStream(outputStreamCaptor));

        accessHistoryTracker.trackAccessHistory(credential);

        assertNotNull(credential.getLastAccessed());
        assertTrue(outputStreamCaptor.toString().trim().contains("Last accessed: This is the first time it is accessed"));

        System.setOut(originalSystemOut);
    }
    @Test
    void testTrackAccessHistory_SubsequentAccess() {
        CredentialService credentialService = new MockCredentialService(new TestCredentialService.TestCredentialStorage(Collections.emptyList()));
        AccessHistoryTracker accessHistoryTracker = new AccessHistoryTracker(credentialService);
        Credential credential = new Credential();
        LocalDateTime lastAccessed = LocalDateTime.now().minusDays(1);
        credential.setLastAccessed(lastAccessed);

        System.setOut(new PrintStream(outputStreamCaptor));

        accessHistoryTracker.trackAccessHistory(credential);

        LocalDateTime currentTimestamp = credential.getLastAccessed();
        assertTrue(currentTimestamp.isAfter(lastAccessed));
        assertTrue(outputStreamCaptor.toString().trim().contains("Last accessed:"));

        System.setOut(originalSystemOut);
    }
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
        }
    }
}

