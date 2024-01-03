package BranchBased;

import group4.passwordmanager.manager.StrengthDisplayManager;
import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.service.CredentialService;
import group4.passwordmanager.service.PasswordStrengthListingService;
import group4.passwordmanager.service.PasswordStrengthSortService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

//Unit tests here test the strength functionalities
public class PasswordStrengthTests {
    @Mock
    private CredentialStorage mockCredentialStorage;
    @Mock
    private CredentialService mockCredentialService;


    private StrengthDisplayManager strengthDisplayManager;

    @InjectMocks
    private PasswordStrengthSortService strengthSortService;

    @InjectMocks
    private PasswordStrengthListingService strengthListingService;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private final PrintStream originalOut = System.out;

    private static final String TEST_FILENAME = "test_credentials.json";


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        strengthDisplayManager = new StrengthDisplayManager(mockCredentialService);
        System.setOut(new PrintStream(outContent));
        // Prepare test data with varying strengths
        List<Credential> mixedStrengthCredentials = List.of(
                new Credential("user1@example.com", "WeakPass", "example.com", null),
                new Credential("user2@example.com", "StrongPass123!", "example.org", null),
                new Credential("user3@example.com", "GoodPass12", "example.net", null),
                new Credential("user4@example.com", "GoodPass12", "example.net", null),
                new Credential("user5@example.com", "WeakPass", "example.com", null),
                new Credential("user6@example.com", "StrongPass123!", "example.org", null)
        );
        when(mockCredentialStorage.getAllCredentials()).thenReturn(mixedStrengthCredentials);
    }

    @AfterEach
    void tearDown() throws IOException {
        System.setOut(originalOut);
        // Clean up after tests
        Files.deleteIfExists(Paths.get(TEST_FILENAME));
    }

    @AfterAll
    public static void cleanup() {
        try {
            Path testFilePath = Paths.get(TEST_FILENAME);
            Files.deleteIfExists(testFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to capture console output
    private String getConsoleOutput() {
        return outContent.toString().trim();
    }

    @Test
    public void testSortCredentialsWeakestToStrongest() {
        strengthSortService.listCredentialsByStrength(true);
        String output = getConsoleOutput();

        System.out.println("Sorted Credentials (Ascending): " + output);

        // Assert the expected order and content of the output
        assertTrue(output.indexOf("Weak") < output.indexOf("Good") && output.indexOf("Good") < output.indexOf("Strong"),
                "Credentials should be listed by strength in ascending order.");
    }

    @Test
    public void testSortCredentialsStrongestToWeakest() {
        strengthSortService.listCredentialsByStrength(false);
        String output = getConsoleOutput();

        System.out.println("Sorted Credentials (Descending): " + output);

        // Assert the expected order and content of the output
        assertTrue(output.indexOf("Strong") < output.indexOf("Good") && output.indexOf("Good") < output.indexOf("Weak"),
                "Credentials should be listed by strength in descending order.");
    }

    @Test
    public void testListCredentialsByCategoryWeak() {
        String category = "Weak";
        strengthSortService.listCredentialsByCategory(category);
        String output = getConsoleOutput();

        // Print the filtered credentials for manual verification
        System.out.println("Filtered Credentials by Category '" + category + "': " + output);

        // Assert that only credentials with "Weak" strength are listed
        assertTrue(output.contains("Weak") && !output.contains("Good") && !output.contains("Strong"),
                "Only credentials with 'Weak' strength should be listed.");
    }

    @Test
    public void testListCredentialsByCategoryGood() {
        String category = "Good";
        strengthSortService.listCredentialsByCategory(category);
        String output = getConsoleOutput();

        // Print the filtered credentials for manual verification
        System.out.println("Filtered Credentials by Category '" + category + "': " + output);

        // Assert that only credentials with "Good" strength are listed
        assertTrue(output.contains("Good") && !output.contains("Weak") && !output.contains("Strong"),
                "Only credentials with 'Good' strength should be listed.");
    }

    @Test
    public void testListCredentialsByCategoryStrong() {
        String category = "Strong";
        strengthSortService.listCredentialsByCategory(category);
        String output = getConsoleOutput();

        // Print the filtered credentials for manual verification
        System.out.println("Filtered Credentials by Category '" + category + "': " + output);

        // Assert that only credentials with "Strong" strength are listed
        assertTrue(output.contains("Strong") && !output.contains("Weak") && !output.contains("Good"),
                "Only credentials with 'Strong' strength should be listed.");
    }

    @Test
    public void testSortWithEmptyCredentialList() {
        when(mockCredentialStorage.getAllCredentials()).thenReturn(Collections.emptyList());
        strengthSortService.listCredentialsByStrength(true);
        String output = getConsoleOutput();
        assertTrue(output.isEmpty(), "Output should be empty when there are no credentials");
    }


    @Test
    public void testSortWithNullCredentialList() {
        when(mockCredentialStorage.getAllCredentials()).thenReturn(null);
        strengthSortService.listCredentialsByStrength(true);
        String output = getConsoleOutput();
        assertTrue(output.contains("No credentials available."), "Output should contain 'No credentials available.' when credential list is null");
    }



    @Test
    public void testListCredentialsByNullCategory() {
        String category = null;
        strengthSortService.listCredentialsByCategory(category);
        String output = getConsoleOutput();

        assertTrue(output.isEmpty(), "Output should be empty when category is null");
    }

    @Test
    public void testListCredentialsByInvalidCategory() {
        String category = "InvalidCategory";
        strengthSortService.listCredentialsByCategory(category);
        String output = getConsoleOutput();

        assertTrue(output.isEmpty(), "Output should be empty when category is invalid");
    }

    @Test
    public void testListCredentialsByEmptyCategory() {
        String category = "";
        strengthSortService.listCredentialsByCategory(category);
        String output = getConsoleOutput();

        assertTrue(output.isEmpty(), "Output should be empty when category is empty");
    }

    @Test
    public void testListCredentialsBySpecificStrengthWithInvalidCategory() {
        String strengthCategory = "InvalidCategory";
        strengthListingService.listCredentialsBySpecificStrength(strengthCategory);
        String output = getConsoleOutput();

        assertTrue(output.isEmpty(), "Output should be empty when strength category is invalid");
    }

    @Test
    public void testListCredentialsBySpecificStrengthWithEmptyCategory() {
        String strengthCategory = "";
        strengthListingService.listCredentialsBySpecificStrength(strengthCategory);
        String output = getConsoleOutput();

        assertTrue(output.isEmpty(), "Output should be empty when strength category is empty");
    }

    @Test
    public void testListCredentialsBySpecificStrengthWithNullCategory() {
        String strengthCategory = null;
        strengthListingService.listCredentialsBySpecificStrength(strengthCategory);
        String output = getConsoleOutput();

        assertTrue(output.isEmpty(), "Output should be empty when strength category is null");
    }


    @Test
    public void testDisplayStrengthsWhenStrengthTyped() {
        // Arrange
        List<Credential> testCredentials = List.of(
                new Credential("user1@example.com", "WeakPassword", "example.com", null),
                new Credential("user2@example.com", "GoodPass123", "example.org", null),
                new Credential("user3@example.com", "StrongPass123!@#", "example.net", null)
        );

        when(mockCredentialService.getAllCredentials()).thenReturn(testCredentials);

        StrengthDisplayManager strengthDisplayManager = new StrengthDisplayManager(mockCredentialService);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        strengthDisplayManager.displayStrengths();

        // Restore the original console output
        System.setOut(originalOut);

        String output = outContent.toString().trim();
        assertTrue(output.contains("Weak") && output.contains("Good") && output.contains("Strong"),
                "Output should display each password strength correctly.");
    }

    @Test
    public void testDisplayStrengthsWithNoTags() {
        List<Credential> testCredentials = List.of(
                new Credential("user1@example.com", "WeakPassword", "example.com", null)
        );
        when(mockCredentialService.getAllCredentials()).thenReturn(testCredentials);

        strengthDisplayManager.displayStrengths();

        String output = getConsoleOutput();
        assertTrue(output.contains("No Tags"), "Output should display 'No Tags' when credentials have no tags.");
    }


    @Test
    public void testDisplayStrengthsWithEmptyCredentialList() {
        // Arrange
        when(mockCredentialService.getAllCredentials()).thenReturn(Collections.emptyList());

        // Act
        strengthDisplayManager.displayStrengths();

        // Assert
        String output = getConsoleOutput();
        assertTrue(output.contains("No credentials available."),
                "Output should indicate no credentials are available.");
    }

    @Test
    public void testDisplayStrengthsWhenCredentialServiceReturnsNull() {
        // Arrange
        when(mockCredentialService.getAllCredentials()).thenReturn(null);

        // Act
        strengthDisplayManager.displayStrengths();

        // Assert
        String output = getConsoleOutput();
        assertTrue(output.contains("No credentials available."),
                "Output should indicate no credentials are available when service returns null.");
    }

    @Test
    public void testDisplayStrengthsWithNullFieldsInCredentials() {
        // Arrange
        List<Credential> testCredentials = List.of(
                new Credential(null, "WeakPassword", null, null)
        );
        when(mockCredentialService.getAllCredentials()).thenReturn(testCredentials);

        // Act
        strengthDisplayManager.displayStrengths();

        // Assert
        String output = getConsoleOutput();
        assertTrue(output.contains("No Tags") && output.contains("Weak"),
                "Output should handle null fields in credentials appropriately.");
    }

}
