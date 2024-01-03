package RandomBased;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.service.LastAccessedListService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

//Random tests for last accessed functionality
public class SortLastAccessedRandomDates {

    private static final String TEST_FILENAME = "test_credentials.json";
    @Mock
    private CredentialStorage mockCredentialStorage;
    @InjectMocks

    private LastAccessedListService lastAccessedListService;
    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private LastAccessedListService last_accessed;

    @Mock
    private CredentialStorage credentialStorage;



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

    // Test when there are a large number of credentials
    @Test
    void listWithManyCredentialsInCorrectOrder() {
        // Generate 100 credentials with random last accessed times
        List<Credential> credentials = IntStream.range(0, 100)
                .mapToObj(i -> new Credential("user" + i + "@example.com", "password" + i, "website" + i, LocalDateTime.now().minusDays(ThreadLocalRandom.current().nextInt(1, 30))))
                .collect(Collectors.toList());

        when(mockCredentialStorage.getAllCredentials()).thenReturn(credentials);

        // Call the method under test
        lastAccessedListService.listCredentialsByLastAccessed();
        String output = getConsoleOutput();

        // Split the output into individual credentials and extract their last accessed times
        List<LocalDateTime> lastAccessedTimes = extractLastAccessedTimesFromOutput(output);

        // Ensure that the list of last accessed times is in descending order
        assertTrue(isSortedDescending(lastAccessedTimes), "Credentials should be listed by last accessed time in descending order.");
    }

    // Test when there is only one credential with random data
    @Test
    void listWithOneRandomCredential() {
        // Generate 1 credential with random data and last accessed time
        int randomId = ThreadLocalRandom.current().nextInt(1, 100);
        Credential credential = new Credential(
                "user" + randomId + "@example.com",
                "password" + randomId,
                "website" + randomId,
                LocalDateTime.now().minusDays(ThreadLocalRandom.current().nextInt(1, 30))
        );

        // Create a list containing only the single credential
        List<Credential> credentialsList = Collections.singletonList(credential);

        // Mock the behavior of CredentialStorage to return the list with the single credential
        when(mockCredentialStorage.getAllCredentials()).thenReturn(credentialsList);

        // Call the method under test
        lastAccessedListService.listCredentialsByLastAccessed();
        String output = getConsoleOutput();

        // Extract the last accessed time from the single credential
        LocalDateTime lastAccessedTime = credential.getLastAccessed();

        // Ensure that the last accessed time is not null
        assertNotNull(lastAccessedTime, "Credential should have a last accessed time.");

        // Ensure the output contains the expected details of the single credential
        assertTrue(output.contains("user" + randomId + "@example.com"), "Output should contain the email of the credential.");
        assertTrue(output.contains("website" + randomId), "Output should contain the website of the credential.");
    }




    // Helper method to extract last accessed times from the output
    private List<LocalDateTime> extractLastAccessedTimesFromOutput(String output) {
        return Pattern.compile("Last Accessed: (.+)")
                .matcher(output)
                .results()
                .map(match -> match.group(1))
                .map(this::parseLastAccessed)
                .collect(Collectors.toList());
    }

    // Helper method to parse the last accessed time
    private LocalDateTime parseLastAccessed(String lastAccessedStr) {
        if ("Never Accessed".equals(lastAccessedStr)) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(lastAccessedStr, formatter);
    }

    // Helper method to check if a list of LocalDateTime is sorted in descending order
    private boolean isSortedDescending(List<LocalDateTime> times) {
        for (int i = 0; i < times.size() - 1; i++) {
            if (times.get(i) == null || times.get(i + 1) == null) {
                continue; // Skip nulls (representing "Never Accessed")
            }
            if (times.get(i).isBefore(times.get(i + 1))) {
                return false; // Found a time that is out of order
            }
        }
        return true;
    }




}
