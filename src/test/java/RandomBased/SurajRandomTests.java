package RandomBased;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.service.CredentialService;
import group4.passwordmanager.service.DeleteCredentialService;
import group4.passwordmanager.service.PasswordGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class SurajRandomTests {

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
    public void testRandomBased() {
        CredentialStorage credentialStorage = mock(CredentialStorage.class);
        List<Credential> credentials = generateRandomCredentials(10);
        when(credentialStorage.getAllCredentials()).thenReturn(credentials);

        DeleteCredentialService deleteCredentialService = new DeleteCredentialService(credentialStorage);
        Scanner scanner = new Scanner("5\n"); // Simulate user input selecting a random index
        deleteCredentialService.deleteSpecificCredential(scanner);

        verify(credentialStorage, times(1)).getAllCredentials();
    }

    private CredentialStorage verify(CredentialStorage credentialStorage, Object times) {
        return null;
    }

    private Object times(int i) {
        return null;
    }

    private CredentialStorage mock(Class<CredentialStorage> class1) {
        return null;
    }

    private List<Credential> generateRandomCredentials(int count) {
        return null;
    }




    private List<Credential> generateRandomCredentials(int count, int minStrength) {
        List<Credential> credentials = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            int strength = random.nextInt(41) + minStrength; // Minimum strength of 60
            String password = generateRandomPassword(strength);

            credentials.add(new Credential("user" + i, password, "example" + i + ".com", null));
        }

        return credentials;
    }

    private String generateRandomPassword(int strength) {

        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 12; i++) {
            char randomChar = (char) (random.nextInt(26) + 'a');
            password.append(randomChar);
        }

        password.append(random.nextInt(100));

        return password.toString();
    }



    @Test
    void testGenerateRandomPassword() {
        String randomPassword = PasswordGenerator.generateRandomPassword();

        assertEquals(8, randomPassword.length());

        assertTrue(randomPassword.matches("[A-Za-z0-9!@#$%^&*()-_=+]+"));
    }

    @Test
    void testEnterPasswordOption1() {
        String userInput = "1\nuserEnteredPassword\n";
        InputStream inputStream = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inputStream);

        Scanner scanner = new Scanner(System.in);
        String enteredPassword = PasswordGenerator.enterPassword(scanner);

        assertEquals("userEnteredPassword", enteredPassword);
    }

    @Test
    void testEnterPasswordOption2() {
        String userInput = "2\n";
        InputStream inputStream = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inputStream);

        Scanner scanner = new Scanner(System.in);
        String enteredPassword = PasswordGenerator.enterPassword(scanner);

        assertNotNull(enteredPassword);
    }

    @Test
    void testEnterPasswordInvalidOption() {
        String userInput = "invalidOption\nuserEnteredPassword\n";
        InputStream inputStream = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inputStream);

        Scanner scanner = new Scanner(System.in);
        String enteredPassword = PasswordGenerator.enterPassword(scanner);

        assertEquals("userEnteredPassword", enteredPassword);
    }

    @Test
    void testEditPasswordOption1() {
        String userInput = "1\nnewUserPassword\n";
        InputStream inputStream = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inputStream);

        Scanner scanner = new Scanner(System.in);
        String editedPassword = PasswordGenerator.editPassword(scanner, "currentPassword");

        assertEquals("newUserPassword", editedPassword);
    }

    @Test
    void testEditPasswordOption2() {
        String userInput = "2\n";
        InputStream inputStream = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inputStream);

        Scanner scanner = new Scanner(System.in);
        String editedPassword = PasswordGenerator.editPassword(scanner, "currentPassword");

        assertNotNull(editedPassword);
    }

    @Test
    void testEditPasswordInvalidOption() {
        String userInput = "invalidOption\nnewUserPassword\n";
        InputStream inputStream = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inputStream);

        Scanner scanner = new Scanner(System.in);
        String editedPassword = PasswordGenerator.editPassword(scanner, "currentPassword");

        assertEquals("newUserPassword", editedPassword);
    }




}
