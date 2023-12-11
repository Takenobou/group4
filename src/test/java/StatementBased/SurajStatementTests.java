package StatementBased;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.service.CredentialService;
import group4.passwordmanager.service.LastAccessedListService;
import group4.passwordmanager.service.PasswordStrengthSortService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class SurajStatementTests {

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

    @Test
    public void testStatementBased() {
        CredentialStorage credentialStorage = mock(CredentialStorage.class); // Initialize credentialStorage with a mock object
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
    void listCredentialsByStrength_AscendingOrder_ShouldReturnSortedCredentials() {
        CredentialStorage storage = new CredentialStorage(null);
        PasswordStrengthSortService strengthSortService = new PasswordStrengthSortService(storage);

        Credential weakCredential = new Credential("user1", "pass1", "website1", null);
        Credential goodCredential = new Credential("user2", "Pass123", "website2", null);
        Credential strongCredential = new Credential("user3", "Strong!123", "website3", null);

        storage.store(weakCredential);
        storage.store(goodCredential);
        storage.store(strongCredential);

        strengthSortService.listCredentialsByStrength(true);

        List<Credential> sortedCredentials = storage.getAllCredentials();
        assertEquals(Arrays.asList(weakCredential, goodCredential, strongCredential), sortedCredentials);
    }

    @Test
    void listCredentialsByStrength_DescendingOrder_ShouldReturnSortedCredentials() {
        CredentialStorage storage = new CredentialStorage(null);
        PasswordStrengthSortService strengthSortService = new PasswordStrengthSortService(storage);

        Credential weakCredential = new Credential("user1", "pass1", "website1", null);
        Credential goodCredential = new Credential("user2", "Pass123", "website2", null);
        Credential strongCredential = new Credential("user3", "Strong!123", "website3", null);

        storage.store(weakCredential);
        storage.store(goodCredential);
        storage.store(strongCredential);

        strengthSortService.listCredentialsByStrength(false);

        List<Credential> sortedCredentials = storage.getAllCredentials();
        assertEquals(Arrays.asList(strongCredential, goodCredential, weakCredential), sortedCredentials);
    }

    @Test
    void listCredentialsByCategory_ShouldReturnFilteredCredentials() {
        CredentialStorage storage = new CredentialStorage(null);
        PasswordStrengthSortService strengthSortService = new PasswordStrengthSortService(storage);

        Credential weakCredential = new Credential("user1", "pass1", "website1", null);
        Credential goodCredential = new Credential("user2", "Pass123", "website2", null);
        Credential strongCredential = new Credential("user3", "Strong!123", "website3", null);


        storage.store(weakCredential);
        storage.store(goodCredential);
        storage.store(strongCredential);


        strengthSortService.listCredentialsByCategory("Good");

        List<Credential> filteredCredentials = storage.getAllCredentials();
        assertEquals(Arrays.asList(goodCredential), filteredCredentials);
    }










}
