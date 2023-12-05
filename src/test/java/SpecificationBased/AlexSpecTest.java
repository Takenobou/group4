package SpecificationBased;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.service.CredentialService;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AlexSpecTest {

    private CredentialStorage storage;
    private CredentialService credentialService;
    private static final String TEST_FILENAME = "test_credentials.json";
    private Credential testCredential;

    @BeforeEach
    void setUp() throws IOException {
        // Prepare a clean test environment before each test
        Files.deleteIfExists(Paths.get(TEST_FILENAME));
        storage = new CredentialStorage(TEST_FILENAME);
        credentialService = new CredentialService(storage);
        testCredential = new Credential("testUser", "testPassword", "testWebsite");
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up after tests
        Files.deleteIfExists(Paths.get(TEST_FILENAME));
    }

    @Test
    void constructorShouldInitializeEmptyListIfFileDoesNotExist() {
        assertTrue(storage.getAllCredentials().isEmpty());
    }

    @Test
    void storeShouldAddNewCredential() {
        storage.store(testCredential);
        List<Credential> credentials = storage.getAllCredentials();
        assertTrue(credentials.contains(testCredential));
    }

    @Test
    void storeShouldNotDuplicateExistingCredential() {
        storage.store(testCredential);
        storage.store(testCredential);
        List<Credential> credentials = storage.getAllCredentials();
        assertEquals(1, credentials.size());
    }

    @Test
    void updateShouldModifyExistingCredential() {
        storage.store(testCredential);
        Credential updatedCredential = new Credential("testUser", "newPassword", "newWebsite");
        storage.update(updatedCredential);
        Credential retrievedCredential = storage.getAllCredentials().get(0);
        assertEquals("newPassword", retrievedCredential.getPassword());
    }

    @Test
    void getAllCredentialsShouldReturnAllStoredCredentials() {
        storage.store(testCredential);
        Credential anotherCredential = new Credential("anotherUser", "anotherPassword", "anotherWebsite");
        storage.store(anotherCredential);
        List<Credential> credentials = storage.getAllCredentials();
        assertEquals(2, credentials.size());
        assertTrue(credentials.contains(testCredential));
        assertTrue(credentials.contains(anotherCredential));
    }

    @Test
    void loadCredentialsShouldLoadFromFile() throws IOException {
        // Pre-populate the file
        CredentialStorage preStorage = new CredentialStorage(TEST_FILENAME);
        preStorage.store(testCredential);
        preStorage.saveCredentials();

        assertTrue(Files.exists(Paths.get(TEST_FILENAME))); // Verify file existence

        // Read file content directly for verification
        String fileContent = new String(Files.readAllBytes(Paths.get(TEST_FILENAME)));
        assertFalse(fileContent.isEmpty()); // Ensure file is not empty

        // Create a new instance to load from the file
        CredentialStorage postStorage = new CredentialStorage(TEST_FILENAME);
        List<Credential> credentials = postStorage.getAllCredentials();

        // Replace the direct comparison with a more detailed check
        boolean containsCredential = credentials.stream()
                .anyMatch(c -> c.getEmailOrUsername().equals(testCredential.getEmailOrUsername()) &&
                        c.getPassword().equals(testCredential.getPassword()) &&
                        c.getWebsite().equals(testCredential.getWebsite()));
        assertTrue(containsCredential);
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

    @Test
    void loadCredentialsWithCorruptedFileShouldHandleException() throws IOException {
        // Write invalid data to the file
        Files.write(Paths.get(TEST_FILENAME), "Invalid Data".getBytes());

        CredentialStorage storage = new CredentialStorage(TEST_FILENAME);
        assertTrue(storage.getAllCredentials().isEmpty());
    }

    @Test
    void saveCredentialsShouldWriteToFile() {
        storage.store(testCredential);
        storage.saveCredentials();
        assertTrue(new File(TEST_FILENAME).exists());
    }

    @Test
    void saveCredentialsWithUnwritableFileShouldHandleException() throws IOException {
        File unwritableDir = new File("unwritable_directory");
        if (!unwritableDir.exists()) {
            unwritableDir.mkdir();
        }
        unwritableDir.setWritable(false);

        File testFile = new File(unwritableDir, "test_credentials.json");
        CredentialStorage unwritableStorage = new CredentialStorage(testFile.getAbsolutePath());
        unwritableStorage.store(testCredential);

        try {
            unwritableStorage.saveCredentials();
        } finally {
            // Clean up: Set the directory back to writable and delete it
            unwritableDir.setWritable(true);
            testFile.delete();
            unwritableDir.delete();
        }
    }

    @Test
    void getCredentialByIndexShouldReturnCorrectCredential() {
        storage.store(testCredential);
        Credential retrievedCredential = credentialService.getCredentialByIndex(0);
        assertEquals(testCredential, retrievedCredential);
    }

    @Test
    void getCredentialByIndexWithInvalidIndexShouldReturnNull() {
        storage.store(testCredential);
        assertNull(credentialService.getCredentialByIndex(1));
    }

    @Test
    void addCredentialShouldAddCredential() {
        Credential newCredential = new Credential("newUser", "newPassword", "newWebsite");
        credentialService.addCredential(newCredential);
        assertTrue(credentialService.getAllCredentials().contains(newCredential));
    }

    @Test
    void editCredentialShouldUpdateCredential() {
        storage.store(testCredential);
        credentialService.editCredential(0, "editedUser", "editedPassword", "editedWebsite");

        Credential editedCredential = credentialService.getAllCredentials().get(0);
        assertEquals("editedUser", editedCredential.getEmailOrUsername());
        assertEquals("editedPassword", editedCredential.getPassword());
        assertEquals("editedWebsite", editedCredential.getWebsite());
    }

    @Test
    void updateCredentialShouldUpdateCredential() {
        storage.store(testCredential);
        Credential updatedCredential = new Credential("testUser", "updatedPassword", "testWebsite");
        credentialService.updateCredential(updatedCredential);

        Credential retrievedCredential = credentialService.getAllCredentials().get(0);
        assertEquals("updatedPassword", retrievedCredential.getPassword());
    }

}
