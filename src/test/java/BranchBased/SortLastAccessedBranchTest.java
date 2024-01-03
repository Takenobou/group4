package BranchBased;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.service.LastAccessedListService;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.stream.IntStream;

//Unit tests here test the last_accessed and last accessed functionalities
public class SortLastAccessedBranchTest {
    private static final String TEST_FILENAME = "test_credentials.json";
    @Mock
    private CredentialStorage mockCredentialStorage;
    @InjectMocks

    private LastAccessedListService lastAccessedListService;
    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();





    @AfterAll
    public static void cleanup() {
        try {
            Path testFilePath = Paths.get(TEST_FILENAME);
            Files.deleteIfExists(testFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @BeforeEach
    void setUp() throws IOException {
        System.setOut(new PrintStream(outContent));
        // Prepare a clean test environment before each test
        Files.deleteIfExists(Paths.get(TEST_FILENAME));
        mockCredentialStorage = mock(CredentialStorage.class);
        lastAccessedListService = new LastAccessedListService(mockCredentialStorage);
    }

    @AfterEach
    void tearDown() throws IOException {
        System.setOut(originalOut);
        // Clean up after tests
        Files.deleteIfExists(Paths.get(TEST_FILENAME));
    }

    // Helper method to capture console output
    private String getConsoleOutput() {
        return outContent.toString().trim();
    }

    @Test
    void listWithEmptyStorage() {
        when(mockCredentialStorage.getAllCredentials()).thenReturn(List.of());
        lastAccessedListService.listCredentialsByLastAccessed();
        assertTrue(outContent.toString().isEmpty(), "Output should be empty when no credentials are stored.");
    }


    @Test
    void listWithVariousLastAccessedTimes() {
        List<Credential> credentials = List.of(
                new Credential("user1@example.com", "password1", "example.com", LocalDateTime.now().minusDays(1)),
                new Credential("user2@example.com", "password2", "example.org", LocalDateTime.now().minusDays(2)),
                new Credential("user3@example.com", "password3", "example.net", LocalDateTime.now())
        );
        when(mockCredentialStorage.getAllCredentials()).thenReturn(credentials);
        lastAccessedListService.listCredentialsByLastAccessed();
        String output = outContent.toString(); // Retrieve what is displayed in the terminal from the previous line
        // Assert the output is in descending order of last accessed times
        // User 3 should be first so have lowest index, followed by user 2
        int u3_index = output.indexOf("user3@example.com");
        int u2_index = output.indexOf("user2@example.com");
        int u1_index = output.indexOf("user1@example.com");
        assertTrue(u3_index >=0, "User 3 not found");
        assertTrue(u2_index >=0, "User 2 not found");
        assertTrue(u1_index >=0, "User 1 not found");
        assertTrue(u3_index < u1_index && u1_index < u2_index,"Credentials should be listed by last accessed time in descending order.");
    }


    // Test when some credentials have never been accessed (last accessed is null)
    @Test
    void listWithSomeNeverAccessedCredentials() {
        List<Credential> credentials = List.of(
                new Credential("user1@example.com", "password1", "example.com", LocalDateTime.now()),
                new Credential("user2@example.com", "password2", "example.org", null),
                new Credential("user3@example.com", "password3", "example.net", LocalDateTime.now().minusDays(1))
        );
        when(mockCredentialStorage.getAllCredentials()).thenReturn(credentials);
        lastAccessedListService.listCredentialsByLastAccessed();
        String output = getConsoleOutput();

        // Ensure that the output contains all credentials, including the one never accessed
        assertTrue(output.contains("Never Accessed"), "Output should mention never accessed for the relevant user.");
    }

    // Test when all credentials have never been accessed (last accessed is null)
    @Test
    void listWithAllNeverAccessedCredentials() {
        List<Credential> credentials = IntStream.range(0, 5)
                .mapToObj(i -> new Credential("user" + i + "@example.com", "password" + i, "website" + i, null))
                .collect(Collectors.toList());

        when(mockCredentialStorage.getAllCredentials()).thenReturn(credentials);
        lastAccessedListService.listCredentialsByLastAccessed();
        String output = getConsoleOutput();

        // Ensure that the output contains the "Never Accessed" text for each credential
        assertEquals(5, output.split("Never Accessed").length, "All credentials should be marked as 'Never Accessed'.");
    }




}
