package SpecificationBased;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.service.CredentialService;
import group4.passwordmanager.service.DeleteCredentialService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SurajSpecTests {

    private CredentialStorage storage;
    private static final String TEST_FILENAME = "test_credentials.json";
    private group4.passwordmanager.model.Credential testCredential;
    private CredentialService service;


    @Test
    public void testSpecificationBased() {
        CredentialStorage credentialStorage1 = mock(CredentialStorage.class); // Initialize credentialStorage1 with a mock object
        when(credentialStorage1.getAllCredentials()).thenReturn(new ArrayList<>());

        DeleteCredentialService deleteCredentialService1 = new DeleteCredentialService(credentialStorage1);
        Scanner scanner1 = new Scanner(""); // Simulate user input (empty input)
        deleteCredentialService1.deleteSpecificCredential(scanner1);

        verify(credentialStorage1, times(1)).getAllCredentials();
        verify(credentialStorage1, times(0)).saveCredentials();

        CredentialStorage credentialStorage2 = mock(CredentialStorage.class); // Initialize credentialStorage2 with a mock object
        List<Credential> credentials2 = generateCredentials(3);
        when(credentialStorage2.getAllCredentials()).thenReturn(credentials2);

        DeleteCredentialService deleteCredentialService2 = new DeleteCredentialService(credentialStorage2);
        Scanner scanner2 = new Scanner("2\n"); // Simulate user input selecting the second credential
        deleteCredentialService2.deleteSpecificCredential(scanner2);

        verify(credentialStorage2, times(1)).getAllCredentials();
        verify(credentialStorage2, times(1)).saveCredentials();

    }

    private List<Credential> generateCredentials(int count) {
        List<Credential> credentials = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            credentials.add(new Credential("user" + i, "password" + i, "example" + i + ".com", null, null));
        }

        return credentials;
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



    private List<Credential> generateCredentialsWithStrength(int count) {
        List<Credential> credentials = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            credentials.add(new Credential("user" + i, "password" + i, "example" + i + ".com", "", "Strength" + i));
        }

        return credentials;
    }

    private CredentialService credentialService;

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILENAME));
        storage = new CredentialStorage(TEST_FILENAME);
        credentialService = new CredentialService(storage);
        testCredential = new Credential("testUser", "testPassword", "testWebsite", null);
    }

    @AfterEach
    void tearDown() throws IOException {
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
        Credential updatedCredential = new Credential("testUser", "newPassword", "newWebsite", null);
        storage.update(updatedCredential);
        Credential retrievedCredential = storage.getAllCredentials().get(0);
        assertEquals("newPassword", retrievedCredential.getPassword());
    }

    @Test
    void getAllCredentialsShouldReturnAllStoredCredentials() {
        storage.store(testCredential);
        Credential anotherCredential = new Credential("anotherUser", "anotherPassword", "anotherWebsite", null);
        storage.store(anotherCredential);
        List<Credential> credentials = storage.getAllCredentials();
        assertEquals(2, credentials.size());
        assertTrue(credentials.contains(testCredential));
        assertTrue(credentials.contains(anotherCredential));
    }

    @Test
    void loadCredentialsShouldLoadFromFile() throws IOException {
        storage.store(testCredential);
        storage.saveCredentials();

        assertTrue(Files.exists(Paths.get(TEST_FILENAME)));

        CredentialStorage postStorage = new CredentialStorage(TEST_FILENAME);
        List<Credential> credentials = postStorage.getAllCredentials();

        assertTrue(credentials.contains(testCredential));
    }

    @Test
    void loadCredentialsWithUnreadableFileShouldHandleException() throws IOException {
        File testFile = new File(TEST_FILENAME);
        assertTrue(testFile.createNewFile());
        assertTrue(testFile.setReadable(false));

        CredentialStorage storage = new CredentialStorage(TEST_FILENAME);
        assertTrue(storage.getAllCredentials().isEmpty());

        assertTrue(testFile.setReadable(true));
    }

    @Test
    void loadCredentialsWithCorruptedFileShouldHandleException() throws IOException {
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
        Credential newCredential = new Credential("newUser", "newPassword", "newWebsite", null);
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
        Credential updatedCredential = new Credential("testUser", "updatedPassword", "testWebsite", null);
        credentialService.updateCredential(updatedCredential);

        Credential retrievedCredential = credentialService.getAllCredentials().get(0);
        assertEquals("updatedPassword", retrievedCredential.getPassword());
    }




}

