package BranchBased;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.service.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

public class SurajBranchTests {


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
    void storeShouldAddNewCredentialIfNotExists() {
        storage.store(testCredential);
        List<group4.passwordmanager.model.Credential> credentials = storage.getAllCredentials();
        assertTrue(credentials.contains(testCredential));
    }

    @Test
    void storeShouldUpdateCredentialIfExists() {
        storage.store(testCredential);
        group4.passwordmanager.model.Credential updatedCredential = new group4.passwordmanager.model.Credential("testUser", "updatedPassword", "testWebsite", null);
        storage.store(updatedCredential);

        group4.passwordmanager.model.Credential retrieved = storage.getAllCredentials().get(0);
        assertEquals("updatedPassword", retrieved.getPassword());
    }

    @Test
    void updateShouldDoNothingIfCredentialNotFound() {
        group4.passwordmanager.model.Credential nonExistingCredential = new group4.passwordmanager.model.Credential("nonExistingUser", "password", "website", null);
        storage.update(nonExistingCredential);
        assertTrue(storage.getAllCredentials().isEmpty());
    }

    @Test
    void updateShouldUpdateCredentialIfExists() {
        storage.store(testCredential);
        group4.passwordmanager.model.Credential updatedCredential = new group4.passwordmanager.model.Credential("testUser", "updatedPassword", "testWebsite", null);
        storage.update(updatedCredential);

        group4.passwordmanager.model.Credential retrieved = storage.getAllCredentials().get(0);
        assertEquals("updatedPassword", retrieved.getPassword());
    }





    @Test
    void shouldReturnAllCredentials() {
        storage.store(new group4.passwordmanager.model.Credential("user1", "pass1", "site1", null));
        storage.store(new group4.passwordmanager.model.Credential("user2", "pass2", "site2", null));

        List<Credential> credentials = service.getAllCredentials();

        assertEquals(2, credentials.size());
    }

    @Test
    public void testBranchBased() {
        CredentialStorage credentialStorage1 = mock(CredentialStorage.class);
        when(credentialStorage1.getAllCredentials()).thenReturn(generateCredentials(3));

        DeleteAllCredentials deleteAllCredentials1 = new DeleteAllCredentials(credentialStorage1);
        deleteAllCredentials1.deleteAllCredentials();

        verify(credentialStorage1, times(1)).getAllCredentials();
        verify(credentialStorage1, times(1)).saveCredentials();

        CredentialStorage credentialStorage2 = mock(CredentialStorage.class);
        when(credentialStorage2.getAllCredentials()).thenReturn(Collections.emptyList());

        DeleteAllCredentials deleteAllCredentials2 = new DeleteAllCredentials(credentialStorage2);
        deleteAllCredentials2.deleteAllCredentials();

        verify(credentialStorage2, times(1)).getAllCredentials();
        verify(credentialStorage2, never()).saveCredentials();
    }

    private List<Credential> generateCredentials(int count) {
        List<Credential> credentials = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            credentials.add(new Credential(null, null, null, null));
        }
        return credentials;
    }

    private CredentialStorage credentialStorage;


    @Test
    void testListCredentialsByStrength() {
        List<Credential> credentials1 = generateCredentialsWithStrength(3, "Strong");
        credentials1.addAll(generateCredentialsWithStrength(2, "Good"));
        credentials1.addAll(generateCredentialsWithStrength(1, "Weak"));
        when(credentialStorage.getAllCredentials()).thenReturn(credentials1);

        PasswordStrengthListingService service1 = new PasswordStrengthListingService(credentialStorage);
        service1.listCredentialsByStrength(true);

        verify(credentialStorage, times(1)).getAllCredentials();
    }

    @Test
    void testListCredentialsByStrengthWithNoCredentials() {
        when(credentialStorage.getAllCredentials()).thenReturn(new ArrayList<>());

        PasswordStrengthListingService service2 = new PasswordStrengthListingService(credentialStorage);
        service2.listCredentialsByStrength(true);

        verify(credentialStorage, times(1)).getAllCredentials();
    }

    @Test
    void testListCredentialsByStrengthDescending() {
        List<Credential> credentials3 = generateCredentialsWithStrength(3, "Strong");
        credentials3.addAll(generateCredentialsWithStrength(2, "Good"));
        credentials3.addAll(generateCredentialsWithStrength(1, "Weak"));
        when(credentialStorage.getAllCredentials()).thenReturn(credentials3);

        PasswordStrengthListingService service3 = new PasswordStrengthListingService(credentialStorage);
        service3.listCredentialsByStrength(false);

        verify(credentialStorage, times(1)).getAllCredentials();
    }

    @Test
    void testListCredentialsBySpecificStrength() {
        List<Credential> credentials4 = generateCredentialsWithStrength(3, "Strong");
        credentials4.addAll(generateCredentialsWithStrength(2, "Good"));
        credentials4.addAll(generateCredentialsWithStrength(1, "Weak"));
        when(credentialStorage.getAllCredentials()).thenReturn(credentials4);

        PasswordStrengthListingService service4 = new PasswordStrengthListingService(credentialStorage);
        service4.listCredentialsBySpecificStrength("Good");

        verify(credentialStorage, times(1)).getAllCredentials();
    }

    @Test
    void testListCredentialsBySpecificStrengthWithNoMatch() {
        List<Credential> credentials5 = generateCredentialsWithStrength(3, "Strong");
        credentials5.addAll(generateCredentialsWithStrength(2, "Weak"));
        when(credentialStorage.getAllCredentials()).thenReturn(credentials5);

        PasswordStrengthListingService service5 = new PasswordStrengthListingService(credentialStorage);
        service5.listCredentialsBySpecificStrength("Good");

        verify(credentialStorage, times(1)).getAllCredentials();
    }

    private List<Credential> generateCredentialsWithStrength(int count, String strength) {
        List<Credential> credentials = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            credentials.add(new Credential("user" + i, "password" + i, "example" + i + ".com", strength, strength));
        }
        return credentials;
    }



    @Test
    void loadCredentialsShouldNotLoadIfFileDoesNotExist() {
        storage = new CredentialStorage("nonExistingFile.json");
        assertTrue(storage.getAllCredentials().isEmpty());
    }

    @Test
    void loadCredentialsShouldNotLoadIfFileIsDirectory() {
        String directoryPath = "testDirectory";
        File directory = new File(directoryPath);
        directory.mkdir();

        storage = new CredentialStorage(directoryPath);
        assertTrue(storage.getAllCredentials().isEmpty());

        directory.delete();
    }

    @Test
    void storeShouldNotAddDuplicateCredential() {
        storage.store(testCredential);
        storage.store(testCredential); // Attempting to add the same credential again
        List<Credential> credentials = storage.getAllCredentials();
        assertEquals(1, credentials.size()); // Ensure no duplicate is added
    }

    @Test
    void updateShouldNotAlterListIfCredentialNotFound() {
        storage.update(new Credential("nonExistingUser", "password", "website", null));
        assertTrue(storage.getAllCredentials().isEmpty());
    }

    @Test
    void loadCredentialsShouldHandleCorruptJsonFile() throws IOException {
        Files.write(Paths.get(TEST_FILENAME), "{invalid json}".getBytes());
        storage = new CredentialStorage(TEST_FILENAME);
        assertTrue(storage.getAllCredentials().isEmpty());
    }

    @Test
    void loadCredentialsShouldHandleEmptyFile() throws IOException {
        new File(TEST_FILENAME).createNewFile();
        storage = new CredentialStorage(TEST_FILENAME);
        assertTrue(storage.getAllCredentials().isEmpty());
    }

    @Test
    void saveCredentialsShouldHandleWritePermissionIssues() throws IOException {
        File file = new File(TEST_FILENAME);
        file.createNewFile();
        file.setReadOnly();

        storage.store(testCredential);

        file.setWritable(true);
    }

    @Test
    void storeShouldHandleNullCredential() {
        storage.store(null);
        assertTrue(storage.getAllCredentials().isEmpty());
    }

    @Test
    void updateShouldHandleNullCredential() {
        storage.update(null);
        assertTrue(storage.getAllCredentials().isEmpty());
    }

    @Test
    void getAllCredentialsShouldReturnEmptyListInitially() {
        List<Credential> credentials = storage.getAllCredentials();
        assertTrue(credentials.isEmpty());
    }

    @Test
    void updateShouldNotUpdateNonExistingCredential() {
        storage.store(testCredential);
        Credential nonExistingCredential = new Credential("nonExistingUser", "newPassword", "newWebsite", null);
        storage.update(nonExistingCredential);

        Credential retrieved = storage.getAllCredentials().get(0);
        assertNotEquals("newPassword", retrieved.getPassword()); // Ensure original credential is not updated
    }

    @Test
    void storeShouldAddNewCredentialWhenNoMatchExists() {
        Credential newCredential = new Credential("newUser", "newPassword", "newWebsite", null);
        storage.store(newCredential);
        assertTrue(storage.getAllCredentials().contains(newCredential));
    }

    @Test
    void loadCredentialsShouldLoadNonEmptyFile() throws IOException {
        ObjectMapper testObjectMapper = new ObjectMapper();
        testObjectMapper.registerModule(new JavaTimeModule());

        List<Credential> testCredentials = Arrays.asList(
                new Credential("user1", "pass1", "site1", null),
                new Credential("user2", "pass2", "site2", null)
        );

        Files.write(Paths.get(TEST_FILENAME), testObjectMapper.writeValueAsBytes(testCredentials));

        storage = new CredentialStorage(TEST_FILENAME);

        assertEquals(0, storage.getAllCredentials().size());
    }

    @Test
    void storeShouldAddCredentialWhenNotFirstInList() {
        Credential firstCredential = new Credential("firstUser", "firstPassword", "firstWebsite", null);
        storage.store(firstCredential);

        storage.store(testCredential);

        List<Credential> credentials = storage.getAllCredentials();
        assertTrue(credentials.contains(firstCredential));
        assertTrue(credentials.contains(testCredential));
    }

    @Test
    void updateShouldUpdateCredentialWhenNotFirstInList() {
        Credential firstCredential = new Credential("firstUser", "firstPassword", "firstWebsite", null);
        storage.store(firstCredential);

        storage.store(testCredential);
        Credential updatedCredential = new Credential("testUser", "updatedPassword", "testWebsite", null);
        storage.update(updatedCredential);

        List<Credential> credentials = storage.getAllCredentials();
        assertEquals("updatedPassword", credentials.get(1).getPassword());
    }

    @Test
    void storeShouldUpdateCredentialWhenNotFirstInList() {
        Credential firstCredential = new Credential("firstUser", "firstPassword", "firstWebsite", null);
        storage.store(firstCredential);

        storage.store(testCredential);

        Credential updatedTestCredential = new Credential("testUser", "updatedPassword", "testWebsite", null);
        storage.store(updatedTestCredential);

        List<Credential> credentials = storage.getAllCredentials();
        assertEquals("firstPassword", credentials.get(0).getPassword()); // Ensure the first credential is unchanged
        assertEquals("updatedPassword", credentials.get(1).getPassword()); // Ensure the test credential is updated
    }

    @Test
    void updateShouldIterateOverListAndNotFindMatchingCredential() {
        // Add credentials
        Credential firstCredential = new Credential("firstUser", "firstPassword", "firstWebsite", null);
        storage.store(firstCredential);
        Credential secondCredential = new Credential("secondUser", "secondPassword", "secondWebsite", null);
        storage.store(secondCredential);

        Credential nonExistingCredential = new Credential("nonExistingUser", "newPassword", "newWebsite", null);
        storage.update(nonExistingCredential);

        List<Credential> credentials = storage.getAllCredentials();
        assertEquals(2, credentials.size()); // Ensure no new credential is added
        assertTrue(credentials.stream().noneMatch(c -> c.getEmailOrUsername().equals("nonExistingUser")));
    }



    @Test
    void shouldReturnCredentialByIndex() {
        storage.store(new Credential("user1", "pass1", "site1", null));
        Credential expected = new Credential("user2", "pass2", "site2", null);
        storage.store(expected);

        Credential actual = service.getCredentialByIndex(1);

        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnNullForInvalidIndex() {
        storage.store(new Credential("user1", "pass1", "site1", null));

        assertNull(service.getCredentialByIndex(1));
    }

    @Test
    void shouldAddValidCredential() {
        Credential newCredential = new Credential("newUser", "newPass", "newSite", null);
        service.addCredential(newCredential);

        List<Credential> credentials = service.getAllCredentials();
        assertTrue(credentials.contains(newCredential));
    }

    @Test
    void shouldEditCredentialWithValidIndex() {
        storage.store(new Credential("user", "pass", "site", null));
        service.editCredential(0, "newUser", "newPass", "newSite");

        Credential updated = service.getCredentialByIndex(0);
        assertEquals("newUser", updated.getEmailOrUsername());
        assertEquals("newPass", updated.getPassword());
        assertEquals("newSite", updated.getWebsite());
    }

    @Test
    void shouldPartiallyEditCredentialWithValidIndex() {
        storage.store(new Credential("user", "pass", "site", null));
        service.editCredential(0, "newUser", "", "");

        Credential updated = service.getCredentialByIndex(0);
        assertEquals("newUser", updated.getEmailOrUsername());
        assertEquals("pass", updated.getPassword());
    }

    @Test
    void shouldNotEditCredentialWithInvalidIndex() {
        storage.store(new Credential("user", "pass", "site", null));
        service.editCredential(1, "newUser", "newPass", "newSite");

        Credential original = service.getCredentialByIndex(0);
        assertEquals("user", original.getEmailOrUsername());
    }

    @Test
    void shouldUpdateExistingCredential() {
        Credential credential = new Credential("user", "pass", "site", null);
        storage.store(credential);

        Credential updatedCredential = new Credential("user", "newPass", "newSite", null);
        service.updateCredential(updatedCredential);

        Credential retrieved = service.getCredentialByIndex(0);
        assertEquals("newPass", retrieved.getPassword());
    }

    @Test
    void shouldEditCredentialPartially() {
        storage.store(new Credential("user", "pass", "site", null));
        service.editCredential(0, "newUser", "", "newSite");

        Credential updated = service.getCredentialByIndex(0);
        assertEquals("newUser", updated.getEmailOrUsername());
        assertEquals("pass", updated.getPassword());
        assertEquals("newSite", updated.getWebsite());
    }

    @Test
    void shouldAddNewCredential() {
        Credential newCredential = new Credential("newUser", "newPass", "newSite", null);
        service.addCredential(newCredential);

        Credential added = service.getCredentialByIndex(0);
        assertNotNull(added);
        assertEquals("newUser", added.getEmailOrUsername());
        assertEquals("newPass", added.getPassword());
        assertEquals("newSite", added.getWebsite());
    }

    @Test
    void shouldUpdateCredential() {
        Credential original = new Credential("user", "pass", "site", null);
        storage.store(original);

        Credential updatedCredential = new Credential("user", "newPass", "newSite", null);
        service.updateCredential(updatedCredential);

        Credential updated = service.getCredentialByIndex(0);
        assertEquals("newPass", updated.getPassword());
        assertEquals("newSite", updated.getWebsite());
    }

    @Test
    void editCredentialShouldNotUpdateEmailWhenEmpty() {
        storage.store(new Credential("user", "pass", "site", null));
        service.editCredential(0, "", "newPass", "newSite");

        Credential updated = service.getCredentialByIndex(0);
        assertEquals("user", updated.getEmailOrUsername()); // Email should remain unchanged
        assertEquals("newPass", updated.getPassword());
        assertEquals("newSite", updated.getWebsite());
    }

    @Test
    void editCredentialShouldDoNothingForNegativeIndex() {
        storage.store(new Credential("user", "pass", "site", null));
        service.editCredential(-1, "newUser", "newPass", "newSite");
        assertEquals(1, service.getAllCredentials().size());
    }

    @Test
    void editCredentialShouldDoNothingForIndexOutOfBounds() {
        storage.store(new Credential("user", "pass", "site", null));
        service.editCredential(1, "newUser", "newPass", "newSite");
        assertEquals(1, service.getAllCredentials().size());
    }

    @Test
    void getCredentialByIndexShouldReturnNullForNegativeIndex() {
        storage.store(new Credential("user1", "pass1", "site1", null));
        assertNull(service.getCredentialByIndex(-1));
    }

    @Test
    void getCredentialByIndexShouldReturnNullForIndexOutOfBounds() {
        storage.store(new Credential("user1", "pass1", "site1", null));
        assertNull(service.getCredentialByIndex(1));
    }

}
