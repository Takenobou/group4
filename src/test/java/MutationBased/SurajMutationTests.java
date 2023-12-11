package MutationBased;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.service.CredentialService;
import group4.passwordmanager.service.LastAccessedListService;
import group4.passwordmanager.service.PasswordStrengthListingService;
import group4.passwordmanager.service.StrengthEvaluatorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class SurajMutationTests {

    private CredentialStorage storage;
    private static final String TEST_FILENAME = "test_credentials.json";
    private group4.passwordmanager.model.Credential testCredential;
    private CredentialService service;

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILENAME));
        storage = new CredentialStorage(TEST_FILENAME);
        testCredential = new group4.passwordmanager.model.Credential("testUser", "testPassword", "testWebsite", null);
        service = new CredentialService(storage);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILENAME));
    }


    private CredentialStorage credentialStorage;

    public void LastAccessedListService(CredentialStorage storage) {
        this.credentialStorage = storage;
    }

    public void listCredentialsByLastAccessed() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<Credential> sortedCredentials = credentialStorage.getAllCredentials()
                .stream()
                .sorted(Comparator.comparing(Credential::getLastAccessed, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        for (int i = 0; i < sortedCredentials.size(); i++) {
            Credential credential = sortedCredentials.get(i);

            String lastAccessedFormatted = credential.getLastAccessed().format(formatter);
            System.out.println((i + 1) + ": Email/Username: " + credential.getEmailOrUsername() + ", Website: " + credential.getWebsite() + ", Last Accessed: " + lastAccessedFormatted);

            System.out.println((i + 1) + ": Email/Username: " + credential.getEmailOrUsername() + ", Website: " + credential.getWebsite() + ", Last Accessed: Never Accessed");
        }
    }

    @Test
    public void testMutationBased() {
        CredentialStorage credentialStorage = mock(CredentialStorage.class);

        List<Credential> credentials = generateCredentialsWithLastAccessed(5);
        when(credentialStorage.getAllCredentials()).thenReturn(credentials);

        LastAccessedListService lastAccessedListService = new LastAccessedListService(credentialStorage);
        lastAccessedListService.listCredentialsByLastAccessed();

        verify(credentialStorage, times(1)).getAllCredentials();
    }

    private List<Credential> generateCredentialsWithLastAccessed(int count) {
        List<Credential> credentials = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < count; i++) {
            LocalDateTime lastAccessed = now.minusDays(i);
            credentials.add(new Credential("user" + i, "password" + i, "example" + i + ".com", lastAccessed));
        }

        return credentials;
    }


    @Test
    public void testSpecificationBased() {
        CredentialStorage credentialStorage1 = mock(CredentialStorage.class);
        when(credentialStorage1.getAllCredentials()).thenReturn(new ArrayList<>());

        PasswordStrengthListingService passwordStrengthListingService1 = new PasswordStrengthListingService(credentialStorage1);
        passwordStrengthListingService1.listCredentialsByStrength(true);

        verify(credentialStorage1, times(1)).getAllCredentials();

        CredentialStorage credentialStorage2 = mock(CredentialStorage.class);
        List<Credential> credentials2 = generateCredentialsWithStrength(5);
        when(credentialStorage2.getAllCredentials()).thenReturn(credentials2);

        PasswordStrengthListingService passwordStrengthListingService2 = new PasswordStrengthListingService(credentialStorage2);
        passwordStrengthListingService2.listCredentialsByStrength(true);

        verify(credentialStorage2, times(1)).getAllCredentials();
    }

    private List<Credential> generateCredentialsWithStrength(int count) {
        List<Credential> credentials = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            credentials.add(new Credential("user" + i, "password" + i, "example" + i + ".com", "", "Strength" + i));
        }

        return credentials;
    }

    @Test
    public void testStatementBased() {
        String result1 = StrengthEvaluatorService.evaluatePasswordStrength(""); // Empty password
        String result2 = StrengthEvaluatorService.evaluatePasswordStrength("StrongPassword123!"); // Strong password
        String result3 = StrengthEvaluatorService.evaluatePasswordStrength("GoodPassword123"); // Good password
        String result4 = StrengthEvaluatorService.evaluatePasswordStrength("WeakPassword"); // Weak password

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);
        assertNotNull(result4);
    }

}
