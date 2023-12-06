package StatementBased;

import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.model.Credential;
import group4.passwordmanager.service.CredentialService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AlexStatementTest {
    private CredentialStorage credentialStorage;
    private CredentialService service;
    private final String TEST_FILENAME = "test_credentials.json";

    @BeforeEach
    public void setUp() {
        credentialStorage = new CredentialStorage(TEST_FILENAME);
        service = new CredentialService(credentialStorage);
    }

    @AfterEach
    public void tearDown() {
        // Clean up: Delete the test file
        new File(TEST_FILENAME).delete();
    }

    @Test
    public void testConstructorWithNonExistentFile() {
        assertDoesNotThrow(() -> new CredentialStorage("non_existent_file.json"));
    }

    @Test
    public void testStoreNewCredential() {
        Credential newCredential = new Credential("user@example.com", "password123", "website.com");
        credentialStorage.store(newCredential);
        assertFalse(credentialStorage.getAllCredentials().isEmpty());
    }

    @Test
    public void testStoreNullCredential() {
        credentialStorage.store(null);
        assertTrue(credentialStorage.getAllCredentials().isEmpty());
    }

    @Test
    public void testUpdateExistingCredential() {
        Credential credential = new Credential("user@example.com", "password123", "website.com");
        credentialStorage.store(credential);
        Credential updatedCredential = new Credential("user@example.com", "newPassword123", "website.com");
        credentialStorage.update(updatedCredential);
        Credential retrievedCredential = credentialStorage.getAllCredentials().get(0);
        assertEquals("newPassword123", retrievedCredential.getPassword());
    }

    @Test
    public void testUpdateNonExistingCredential() {
        Credential nonExistingCredential = new Credential("nonexistent@example.com", "password123", "website.com");
        credentialStorage.update(nonExistingCredential);
        assertTrue(credentialStorage.getAllCredentials().isEmpty());
    }

    @Test
    public void testGetAllCredentialsEmpty() {
        assertTrue(credentialStorage.getAllCredentials().isEmpty());
    }

    @Test
    public void testLoadCredentialsWithValidData() {
        // Manually create a file with valid credential data
        Credential credential = new Credential("user@example.com", "password123", "website.com");
        credentialStorage.store(credential);
        // Reinitialise to trigger loading from file
        credentialStorage = new CredentialStorage(TEST_FILENAME);
        assertFalse(credentialStorage.getAllCredentials().isEmpty());
    }

    @Test
    public void testSaveCredentials() {
        Credential credential = new Credential("user@example.com", "password123", "website.com");
        credentialStorage.store(credential);
        // Check if file exists and is not empty
        File file = new File(TEST_FILENAME);
        assertTrue(file.exists() && file.length() > 0);
    }

    @Test
    public void testStoreExistingCredential() {
        Credential credential = new Credential("user@example.com", "password123", "website.com");
        credentialStorage.store(credential);

        Credential updatedCredential = new Credential("user@example.com", "newPassword123", "website.com");
        credentialStorage.store(updatedCredential); // This should update the existing credential

        Credential retrievedCredential = credentialStorage.getAllCredentials().get(0);
        assertEquals("newPassword123", retrievedCredential.getPassword());
    }

    @Test
    public void testLoadCredentialsIOException() {
        // Use a file path that is likely to cause an IOException
        String inaccessibleFilePath = "/root/inaccessible_file.json";
        CredentialStorage localCredentialStorage = new CredentialStorage(inaccessibleFilePath);

        // Since we cannot check console output easily in JUnit, just checking the storage is empty
        assertTrue(localCredentialStorage.getAllCredentials().isEmpty());
    }

    @Test
    public void testSaveCredentialsIOException() {
        // Use a file path that is likely to cause an IOException
        String inaccessibleFilePath = "/root/inaccessible_file.json";
        CredentialStorage localCredentialStorage = new CredentialStorage(inaccessibleFilePath);

        Credential credential = new Credential("user@example.com", "password123", "website.com");
        localCredentialStorage.store(credential);

        // Since we cannot check console output easily in JUnit, just checking the existence of the file
        File file = new File(inaccessibleFilePath);
        assertFalse(file.exists());
    }

    @Test
    void loadCredentialsWithUnreadableFileShouldHandleException() throws IOException {
        // Create a file and restrict its permissions
        File testFile = new File(TEST_FILENAME);
        assertTrue(testFile.createNewFile());
        assertTrue(testFile.setReadable(false));

        CredentialStorage storage = new CredentialStorage(TEST_FILENAME);
        assertTrue(storage.getAllCredentials().isEmpty());

        // Reset the file permissions after the test
        assertTrue(testFile.setReadable(true));
    }

    //CredentialService tests

    @Test
    void getAllCredentialsShouldReturnAll() {
        Credential credential = new Credential("user@example.com", "password", "example.com");
        service.addCredential(credential);

        List<Credential> retrieved = service.getAllCredentials();
        assertEquals(1, retrieved.size());
        assertEquals(credential, retrieved.get(0));
    }

    @Test
    void getCredentialByValidIndexShouldReturnCredential() {
        Credential credential = new Credential("user@example.com", "password", "example.com");
        service.addCredential(credential);

        Credential retrieved = service.getCredentialByIndex(0);
        assertEquals(credential, retrieved);
    }

    @Test
    void getCredentialByInvalidIndexShouldReturnNull() {
        assertNull(service.getCredentialByIndex(0));
    }

    @Test
    void addCredentialShouldAddCredential() {
        service.addCredential(new Credential("user@example.com", "password", "example.com"));
        assertEquals(1, service.getAllCredentials().size());
    }

    @Test
    void editCredentialWithValidIndexShouldUpdateCredential() {
        service.addCredential(new Credential("user@example.com", "password", "example.com"));
        service.editCredential(0, "newuser@example.com", "newpassword", "newexample.com");

        Credential updated = service.getAllCredentials().get(0);
        assertEquals("newuser@example.com", updated.getEmailOrUsername());
        assertEquals("newpassword", updated.getPassword());
        assertEquals("newexample.com", updated.getWebsite());
    }

    @Test
    void editCredentialWithInvalidIndexShouldNotUpdate() {
        service.editCredential(0, "newuser@example.com", "newpassword", "newexample.com");
        assertTrue(service.getAllCredentials().isEmpty());
    }

    @Test
    void updateCredentialShouldUpdateCredential() {
        Credential credential = new Credential("user@example.com", "password", "example.com");
        service.addCredential(credential);
        Credential updatedCredential = new Credential("user@example.com", "newpassword", "example.com");
        service.updateCredential(updatedCredential);

        Credential retrieved = service.getAllCredentials().get(0);
        assertEquals("newpassword", retrieved.getPassword());
    }
}
