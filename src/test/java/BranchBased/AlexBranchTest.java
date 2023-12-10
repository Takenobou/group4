//package BranchBased;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import group4.passwordmanager.model.Credential;
//import group4.passwordmanager.model.CredentialStorage;
//import group4.passwordmanager.service.CredentialService;
//import org.junit.jupiter.api.*;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.Arrays;
//import java.util.List;
//import static org.junit.jupiter.api.Assertions.*;
//
//public class AlexBranchTest {
//    private CredentialStorage storage;
//    private static final String TEST_FILENAME = "test_credentials.json";
//    private Credential testCredential;
//    private CredentialService service;
//
//    @BeforeEach
//    void setUp() throws IOException {
//        // Prepare a clean test environment before each test
//        Files.deleteIfExists(Paths.get(TEST_FILENAME));
//        storage = new CredentialStorage(TEST_FILENAME);
//        testCredential = new Credential("testUser", "testPassword", "testWebsite");
//        service = new CredentialService(storage);
//    }
//
//    @AfterEach
//    void tearDown() throws IOException {
//        // Clean up after tests
//        Files.deleteIfExists(Paths.get(TEST_FILENAME));
//    }
//
//    // CredentialStorage.java tests
//
//    @Test
//    void storeShouldAddNewCredentialIfNotExists() {
//        storage.store(testCredential);
//        List<Credential> credentials = storage.getAllCredentials();
//        assertTrue(credentials.contains(testCredential));
//    }
//
//    @Test
//    void storeShouldUpdateCredentialIfExists() {
//        storage.store(testCredential);
//        Credential updatedCredential = new Credential("testUser", "updatedPassword", "testWebsite");
//        storage.store(updatedCredential);
//
//        Credential retrieved = storage.getAllCredentials().get(0);
//        assertEquals("updatedPassword", retrieved.getPassword());
//    }
//
//    @Test
//    void updateShouldDoNothingIfCredentialNotFound() {
//        Credential nonExistingCredential = new Credential("nonExistingUser", "password", "website");
//        storage.update(nonExistingCredential);
//        assertTrue(storage.getAllCredentials().isEmpty());
//    }
//
//    @Test
//    void updateShouldUpdateCredentialIfExists() {
//        storage.store(testCredential);
//        Credential updatedCredential = new Credential("testUser", "updatedPassword", "testWebsite");
//        storage.update(updatedCredential);
//
//        Credential retrieved = storage.getAllCredentials().get(0);
//        assertEquals("updatedPassword", retrieved.getPassword());
//    }
//
//    @Test
//    void loadCredentialsShouldNotLoadIfFileDoesNotExist() {
//        // Assuming the file does not exist at this point
//        storage = new CredentialStorage("nonExistingFile.json");
//        assertTrue(storage.getAllCredentials().isEmpty());
//    }
//
//    @Test
//    void loadCredentialsShouldNotLoadIfFileIsDirectory() {
//        String directoryPath = "testDirectory";
//        File directory = new File(directoryPath);
//        directory.mkdir();
//
//        storage = new CredentialStorage(directoryPath);
//        assertTrue(storage.getAllCredentials().isEmpty());
//
//        // Cleanup
//        directory.delete();
//    }
//
//    @Test
//    void storeShouldNotAddDuplicateCredential() {
//        storage.store(testCredential);
//        storage.store(testCredential); // Attempting to add the same credential again
//        List<Credential> credentials = storage.getAllCredentials();
//        assertEquals(1, credentials.size()); // Ensure no duplicate is added
//    }
//
//    @Test
//    void updateShouldNotAlterListIfCredentialNotFound() {
//        storage.update(new Credential("nonExistingUser", "password", "website"));
//        assertTrue(storage.getAllCredentials().isEmpty());
//    }
//
//    @Test
//    void loadCredentialsShouldHandleCorruptJsonFile() throws IOException {
//        Files.write(Paths.get(TEST_FILENAME), "{invalid json}".getBytes());
//        storage = new CredentialStorage(TEST_FILENAME);
//        assertTrue(storage.getAllCredentials().isEmpty());
//    }
//
//    @Test
//    void loadCredentialsShouldHandleEmptyFile() throws IOException {
//        new File(TEST_FILENAME).createNewFile();
//        storage = new CredentialStorage(TEST_FILENAME);
//        assertTrue(storage.getAllCredentials().isEmpty());
//    }
//
//    @Test
//    void saveCredentialsShouldHandleWritePermissionIssues() throws IOException {
//        File file = new File(TEST_FILENAME);
//        file.createNewFile();
//        file.setReadOnly();
//
//        storage.store(testCredential);
//        // No straightforward way to assert an exception in the method
//        // but you can check the log for the expected error message
//
//        file.setWritable(true); // Reset file permissions
//    }
//
//    @Test
//    void storeShouldHandleNullCredential() {
//        storage.store(null);
//        assertTrue(storage.getAllCredentials().isEmpty());
//    }
//
//    @Test
//    void updateShouldHandleNullCredential() {
//        storage.update(null);
//        assertTrue(storage.getAllCredentials().isEmpty());
//    }
//
//    @Test
//    void getAllCredentialsShouldReturnEmptyListInitially() {
//        List<Credential> credentials = storage.getAllCredentials();
//        assertTrue(credentials.isEmpty());
//    }
//
//    @Test
//    void updateShouldNotUpdateNonExistingCredential() {
//        storage.store(testCredential);
//        Credential nonExistingCredential = new Credential("nonExistingUser", "newPassword", "newWebsite");
//        storage.update(nonExistingCredential);
//
//        Credential retrieved = storage.getAllCredentials().get(0);
//        assertNotEquals("newPassword", retrieved.getPassword()); // Ensure original credential is not updated
//    }
//
//    @Test
//    void storeShouldAddNewCredentialWhenNoMatchExists() {
//        Credential newCredential = new Credential("newUser", "newPassword", "newWebsite");
//        storage.store(newCredential);
//        assertTrue(storage.getAllCredentials().contains(newCredential));
//    }
//
//    @Test
//    void loadCredentialsShouldLoadNonEmptyFile() throws IOException {
//        // Create a new instance of ObjectMapper for test
//        ObjectMapper testObjectMapper = new ObjectMapper();
//        testObjectMapper.registerModule(new JavaTimeModule());
//
//        // Prepare test data
//        List<Credential> testCredentials = Arrays.asList(
//                new Credential("user1", "pass1", "site1"),
//                new Credential("user2", "pass2", "site2")
//        );
//
//        // Write test data to file
//        Files.write(Paths.get(TEST_FILENAME), testObjectMapper.writeValueAsBytes(testCredentials));
//
//        // Load credentials from file
//        storage = new CredentialStorage(TEST_FILENAME);
//
//        // Assert that credentials are loaded correctly
//        assertEquals(2, storage.getAllCredentials().size());
//    }
//
//    @Test
//    void storeShouldAddCredentialWhenNotFirstInList() {
//        // Add a different credential first
//        Credential firstCredential = new Credential("firstUser", "firstPassword", "firstWebsite");
//        storage.store(firstCredential);
//
//        // Now add the test credential
//        storage.store(testCredential);
//
//        // Check if both credentials are stored
//        List<Credential> credentials = storage.getAllCredentials();
//        assertTrue(credentials.contains(firstCredential));
//        assertTrue(credentials.contains(testCredential));
//    }
//
//    @Test
//    void updateShouldUpdateCredentialWhenNotFirstInList() {
//        // Add a different credential first
//        Credential firstCredential = new Credential("firstUser", "firstPassword", "firstWebsite");
//        storage.store(firstCredential);
//
//        // Store and then update the test credential
//        storage.store(testCredential);
//        Credential updatedCredential = new Credential("testUser", "updatedPassword", "testWebsite");
//        storage.update(updatedCredential);
//
//        // Retrieve and check if the test credential was updated
//        List<Credential> credentials = storage.getAllCredentials();
//        assertEquals("updatedPassword", credentials.get(1).getPassword());
//    }
//
//    @Test
//    void storeShouldUpdateCredentialWhenNotFirstInList() {
//        // Add a different credential first
//        Credential firstCredential = new Credential("firstUser", "firstPassword", "firstWebsite");
//        storage.store(firstCredential);
//
//        // Add the test credential
//        storage.store(testCredential);
//
//        // Update the test credential
//        Credential updatedTestCredential = new Credential("testUser", "updatedPassword", "testWebsite");
//        storage.store(updatedTestCredential);
//
//        // Check if the test credential is updated and the first credential remains unchanged
//        List<Credential> credentials = storage.getAllCredentials();
//        assertEquals("firstPassword", credentials.get(0).getPassword()); // Ensure the first credential is unchanged
//        assertEquals("updatedPassword", credentials.get(1).getPassword()); // Ensure the test credential is updated
//    }
//
//    @Test
//    void updateShouldIterateOverListAndNotFindMatchingCredential() {
//        // Add some credentials
//        Credential firstCredential = new Credential("firstUser", "firstPassword", "firstWebsite");
//        storage.store(firstCredential);
//        Credential secondCredential = new Credential("secondUser", "secondPassword", "secondWebsite");
//        storage.store(secondCredential);
//
//        // Attempt to update a credential not in the list
//        Credential nonExistingCredential = new Credential("nonExistingUser", "newPassword", "newWebsite");
//        storage.update(nonExistingCredential);
//
//        // Assert that the list size is the same and credentials are unchanged
//        List<Credential> credentials = storage.getAllCredentials();
//        assertEquals(2, credentials.size()); // Ensure no new credential is added
//        assertTrue(credentials.stream().noneMatch(c -> c.getEmailOrUsername().equals("nonExistingUser")));
//    }
//
//    // CredentialService.java tests
//
//    @Test
//    void shouldReturnAllCredentials() {
//        storage.store(new Credential("user1", "pass1", "site1"));
//        storage.store(new Credential("user2", "pass2", "site2"));
//
//        List<Credential> credentials = service.getAllCredentials();
//
//        assertEquals(2, credentials.size());
//    }
//
//    @Test
//    void shouldReturnCredentialByIndex() {
//        storage.store(new Credential("user1", "pass1", "site1"));
//        Credential expected = new Credential("user2", "pass2", "site2");
//        storage.store(expected);
//
//        Credential actual = service.getCredentialByIndex(1);
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void shouldReturnNullForInvalidIndex() {
//        storage.store(new Credential("user1", "pass1", "site1"));
//
//        assertNull(service.getCredentialByIndex(1));
//    }
//
//    @Test
//    void shouldAddValidCredential() {
//        Credential newCredential = new Credential("newUser", "newPass", "newSite");
//        service.addCredential(newCredential);
//
//        List<Credential> credentials = service.getAllCredentials();
//        assertTrue(credentials.contains(newCredential));
//    }
//
//    @Test
//    void shouldEditCredentialWithValidIndex() {
//        storage.store(new Credential("user", "pass", "site"));
//        service.editCredential(0, "newUser", "newPass", "newSite");
//
//        Credential updated = service.getCredentialByIndex(0);
//        assertEquals("newUser", updated.getEmailOrUsername());
//        assertEquals("newPass", updated.getPassword());
//        assertEquals("newSite", updated.getWebsite());
//    }
//
//    @Test
//    void shouldPartiallyEditCredentialWithValidIndex() {
//        storage.store(new Credential("user", "pass", "site"));
//        service.editCredential(0, "newUser", "", "");
//
//        Credential updated = service.getCredentialByIndex(0);
//        assertEquals("newUser", updated.getEmailOrUsername());
//        assertEquals("pass", updated.getPassword());
//        // Assert other fields remain unchanged
//    }
//
//    @Test
//    void shouldNotEditCredentialWithInvalidIndex() {
//        storage.store(new Credential("user", "pass", "site"));
//        service.editCredential(1, "newUser", "newPass", "newSite");
//
//        // Assert that the original credential remains unchanged
//        Credential original = service.getCredentialByIndex(0);
//        assertEquals("user", original.getEmailOrUsername());
//    }
//
//    @Test
//    void shouldUpdateExistingCredential() {
//        Credential credential = new Credential("user", "pass", "site");
//        storage.store(credential);
//
//        Credential updatedCredential = new Credential("user", "newPass", "newSite");
//        service.updateCredential(updatedCredential);
//
//        Credential retrieved = service.getCredentialByIndex(0);
//        assertEquals("newPass", retrieved.getPassword());
//    }
//
//    @Test
//    void shouldEditCredentialPartially() {
//        storage.store(new Credential("user", "pass", "site"));
//        service.editCredential(0, "newUser", "", "newSite");
//
//        Credential updated = service.getCredentialByIndex(0);
//        assertEquals("newUser", updated.getEmailOrUsername());
//        assertEquals("pass", updated.getPassword());
//        assertEquals("newSite", updated.getWebsite());
//    }
//
//    @Test
//    void shouldAddNewCredential() {
//        Credential newCredential = new Credential("newUser", "newPass", "newSite");
//        service.addCredential(newCredential);
//
//        Credential added = service.getCredentialByIndex(0);
//        assertNotNull(added);
//        assertEquals("newUser", added.getEmailOrUsername());
//        assertEquals("newPass", added.getPassword());
//        assertEquals("newSite", added.getWebsite());
//    }
//
//    @Test
//    void shouldUpdateCredential() {
//        Credential original = new Credential("user", "pass", "site");
//        storage.store(original);
//
//        Credential updatedCredential = new Credential("user", "newPass", "newSite");
//        service.updateCredential(updatedCredential);
//
//        Credential updated = service.getCredentialByIndex(0);
//        assertEquals("newPass", updated.getPassword());
//        assertEquals("newSite", updated.getWebsite());
//    }
//
//    @Test
//    void editCredentialShouldNotUpdateEmailWhenEmpty() {
//        storage.store(new Credential("user", "pass", "site"));
//        service.editCredential(0, "", "newPass", "newSite");
//
//        Credential updated = service.getCredentialByIndex(0);
//        assertEquals("user", updated.getEmailOrUsername()); // Email should remain unchanged
//        assertEquals("newPass", updated.getPassword());
//        assertEquals("newSite", updated.getWebsite());
//    }
//
//    @Test
//    void editCredentialShouldDoNothingForNegativeIndex() {
//        storage.store(new Credential("user", "pass", "site"));
//        service.editCredential(-1, "newUser", "newPass", "newSite");
//        // Assert that the original credential remains unchanged
//        assertEquals(1, service.getAllCredentials().size());
//    }
//
//    @Test
//    void editCredentialShouldDoNothingForIndexOutOfBounds() {
//        storage.store(new Credential("user", "pass", "site"));
//        service.editCredential(1, "newUser", "newPass", "newSite");
//        // Assert that the original credential remains unchanged
//        assertEquals(1, service.getAllCredentials().size());
//    }
//
//    @Test
//    void getCredentialByIndexShouldReturnNullForNegativeIndex() {
//        storage.store(new Credential("user1", "pass1", "site1"));
//        assertNull(service.getCredentialByIndex(-1));
//    }
//
//    @Test
//    void getCredentialByIndexShouldReturnNullForIndexOutOfBounds() {
//        storage.store(new Credential("user1", "pass1", "site1"));
//        assertNull(service.getCredentialByIndex(1));
//    }
//}