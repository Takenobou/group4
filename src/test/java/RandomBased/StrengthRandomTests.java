package RandomBased;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.service.*;
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
import java.util.regex.Pattern;

import static RandomBased.PasswordGeneratorTesting.generateRandomPassword;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;



//Random tests for strength functionality
public class StrengthRandomTests {

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

    @Test
    void testGenerateRandomPasswordNumbersOnly() {
        String pwd = generateRandomPassword(8, false, true, false);
        assertTrue(Pattern.matches("[0-9]+", pwd), "The password should contain numbers only");
        String strength = StrengthEvaluatorService.evaluatePasswordStrength(pwd);
        assertEquals("Weak", strength, "The password is Weak because it has numbers only");
    }

    @Test
    void testGenerateRandomPasswordLettersAndNumbers() {
        String pwd = generateRandomPassword(8, true, true, false);
        assertTrue(Pattern.matches("[a-zA-Z0-9]+", pwd), "The password should contain letters and numbers only");
        String strength = StrengthEvaluatorService.evaluatePasswordStrength(pwd);
        assertEquals("Good", strength, "The password is Good because it has letters and digits");
    }

    @Test
    void testGenerateRandomPasswordLettersNumbersAndSpecialCharacters() {
        String pwd = generateRandomPassword(10, true, true, true);
        assertTrue(Pattern.matches("[a-zA-Z0-9!@#$%^&*]+", pwd), "The password should contain letters, numbers, and special characters");
        String strength = StrengthEvaluatorService.evaluatePasswordStrength(pwd);
        assertEquals("Strong", strength,
                "The password '" + pwd + "' is expected to be Strong because it has letters, digits, and specials.");    }


    @Test
    void testGenerateShortestPassword() {

        int shortestLength = 1;

        // Generate the shortest password
        String pwd = generateRandomPassword(shortestLength, true, false, false);

        // Check the password length
        assertEquals(shortestLength, pwd.length(), "The password should be " + shortestLength + " character long.");

        // Check if the password contains at least a letter
        assertTrue(pwd.matches("[a-zA-Z]+"), "The shortest password should contain at least a letter.");
    }

    @Test
    void testGenerateLongestPassword() {
        int longestLength = 1000;

        // Generate the longest password
        String pwd = generateRandomPassword(longestLength, true, true, true);

        // Check the password length
        assertEquals(longestLength, pwd.length(), "The password should be " + longestLength + " characters long.");

        // Check if the password meets the criteria of containing letters, numbers, and special characters
        assertTrue(pwd.matches("^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*]).{" + longestLength + "}$"),
                "The longest password should contain letters, numbers, and special characters.");
    }

    @Test
    void testGenerateRandomPasswordNumbersAndSpecialCharacters() {
        String pwd = generateRandomPassword(10, false, true, true);
        assertTrue(Pattern.matches("[0-9!@#$%^&*]+", pwd), "The password should contain only numbers and special characters");
        String strength = StrengthEvaluatorService.evaluatePasswordStrength(pwd);
        assertEquals("Good", strength,
                "The password '" + pwd + "' is expected to be Good because it has digits and specials.");
    }

    @Test
    void testListingOnlyStrongPasswords() {
// Mock data for the test
        LocalDateTime now = LocalDateTime.now();

//instantiation of Credential objects
        Credential strongCredential = new Credential("userStrong", PasswordGeneratorTesting.generateStrongPassword(), "websiteStrong", now);
        Credential goodCredential = new Credential("userGood", PasswordGeneratorTesting.generateGoodPassword(), "websiteGood", now);
        Credential weakCredential = new Credential("userWeak", PasswordGeneratorTesting.generateWeakPassword(), "websiteWeak", now);

        List<Credential> credentials = List.of(strongCredential, goodCredential, weakCredential);
        when(mockCredentialStorage.getAllCredentials()).thenReturn(credentials);

        PasswordStrengthSortService sortService = new PasswordStrengthSortService(mockCredentialStorage);
        PasswordStrengthListingService listingService = new PasswordStrengthListingService(mockCredentialStorage);

        // Act: Sort credentials by strength and list only strong ones
        sortService.listCredentialsByStrength(true); // true for ascending order
        listingService.listCredentialsBySpecificStrength("Strong");

        // Assert: Capture and verify the output
        String output = outContent.toString();
        assertFalse(output.isEmpty(), "Output should not be empty");
        assertTrue(output.contains(strongCredential.getEmailOrUsername()), "Output should contain the strong credential");
        assertTrue(output.contains(goodCredential.getEmailOrUsername()), "Output should not contain the good credential");
        assertTrue(output.contains(weakCredential.getEmailOrUsername()), "Output should not contain the weak credential");
    }


    @Test
    void testListingOnlyGoodPasswords() {
// Mock data for the test
        LocalDateTime now = LocalDateTime.now();

//instantiation of Credential objects
        Credential strongCredential = new Credential("userStrong", PasswordGeneratorTesting.generateStrongPassword(), "websiteStrong", now);
        Credential goodCredential = new Credential("userGood", PasswordGeneratorTesting.generateGoodPassword(), "websiteGood", now);
        Credential weakCredential = new Credential("userWeak", PasswordGeneratorTesting.generateWeakPassword(), "websiteWeak", now);

        List<Credential> credentials = List.of(strongCredential, goodCredential, weakCredential);
        when(mockCredentialStorage.getAllCredentials()).thenReturn(credentials);

        PasswordStrengthSortService sortService = new PasswordStrengthSortService(mockCredentialStorage);
        PasswordStrengthListingService listingService = new PasswordStrengthListingService(mockCredentialStorage);

        // Act: Sort credentials by strength and list only strong ones
        sortService.listCredentialsByStrength(true); // true for ascending order
        listingService.listCredentialsBySpecificStrength("Good");

        // Assert: Capture and verify the output
        String output = outContent.toString();
        assertFalse(output.isEmpty(), "Output should not be empty");
        assertTrue(output.contains(strongCredential.getEmailOrUsername()), "Output should not contain the strong credential");
        assertTrue(output.contains(goodCredential.getEmailOrUsername()), "Output should contain the good credential");
        assertTrue(output.contains(weakCredential.getEmailOrUsername()), "Output should not contain the weak credential");
    }




    @Test
    public void testSortCredentialsAscendingWithMultipleStrengths() {
        LocalDateTime now = LocalDateTime.now();

        // Creating three credentials of each strength
        Credential strongCredential1 = new Credential("userStrong1", PasswordGeneratorTesting.generateStrongPassword(), "websiteStrong1", now);
        Credential strongCredential2 = new Credential("userStrong2", PasswordGeneratorTesting.generateStrongPassword(), "websiteStrong2", now);
        Credential strongCredential3 = new Credential("userStrong3", PasswordGeneratorTesting.generateStrongPassword(), "websiteStrong3", now);
        Credential goodCredential1 = new Credential("userGood1", PasswordGeneratorTesting.generateGoodPassword(), "websiteGood1", now);
        Credential goodCredential2 = new Credential("userGood2", PasswordGeneratorTesting.generateGoodPassword(), "websiteGood2", now);
        Credential goodCredential3 = new Credential("userGood3", PasswordGeneratorTesting.generateGoodPassword(), "websiteGood3", now);
        Credential weakCredential1 = new Credential("userWeak1", PasswordGeneratorTesting.generateWeakPassword(), "websiteWeak1", now);
        Credential weakCredential2 = new Credential("userWeak2", PasswordGeneratorTesting.generateWeakPassword(), "websiteWeak2", now);
        Credential weakCredential3 = new Credential("userWeak3", PasswordGeneratorTesting.generateWeakPassword(), "websiteWeak3", now);

        // Combine all credentials into a list
        List<Credential> credentials = List.of(
                strongCredential1, strongCredential2, strongCredential3,
                goodCredential1, goodCredential2, goodCredential3,
                weakCredential1, weakCredential2, weakCredential3
        );

        when(mockCredentialStorage.getAllCredentials()).thenReturn(credentials);

        PasswordStrengthSortService sortService = new PasswordStrengthSortService(mockCredentialStorage);
        sortService.listCredentialsByStrength(true); // true for ascending order

        String output = outContent.toString();
        assertFalse(output.isEmpty(), "Output should not be empty");
        assertTrue(output.indexOf("userWeak1") < output.indexOf("userGood1"), "Weak credentials should be listed before good ones for ascending order.");
        assertTrue(output.indexOf("userGood1") < output.indexOf("userStrong1"), "Good credentials should be listed before strong ones for ascending order.");
    }



    @Test
    public void testSortCredentialsDescendingWithMultipleStrengths() {
        LocalDateTime now = LocalDateTime.now();

        // Creating three credentials of each strength
        Credential strongCredential1 = new Credential("userStrong1", PasswordGeneratorTesting.generateStrongPassword(), "websiteStrong1", now);
        Credential strongCredential2 = new Credential("userStrong2", PasswordGeneratorTesting.generateStrongPassword(), "websiteStrong2", now);
        Credential strongCredential3 = new Credential("userStrong3", PasswordGeneratorTesting.generateStrongPassword(), "websiteStrong3", now);
        Credential goodCredential1 = new Credential("userGood1", PasswordGeneratorTesting.generateGoodPassword(), "websiteGood1", now);
        Credential goodCredential2 = new Credential("userGood2", PasswordGeneratorTesting.generateGoodPassword(), "websiteGood2", now);
        Credential goodCredential3 = new Credential("userGood3", PasswordGeneratorTesting.generateGoodPassword(), "websiteGood3", now);
        Credential weakCredential1 = new Credential("userWeak1", PasswordGeneratorTesting.generateWeakPassword(), "websiteWeak1", now);
        Credential weakCredential2 = new Credential("userWeak2", PasswordGeneratorTesting.generateWeakPassword(), "websiteWeak2", now);
        Credential weakCredential3 = new Credential("userWeak3", PasswordGeneratorTesting.generateWeakPassword(), "websiteWeak3", now);

        // Combine all credentials into a list
        List<Credential> credentials = List.of(
                strongCredential1, strongCredential2, strongCredential3,
                goodCredential1, goodCredential2, goodCredential3,
                weakCredential1, weakCredential2, weakCredential3
        );

        when(mockCredentialStorage.getAllCredentials()).thenReturn(credentials);

        PasswordStrengthSortService sortService = new PasswordStrengthSortService(mockCredentialStorage);
        sortService.listCredentialsByStrength(false); // false for descending order

        String output = outContent.toString();
        assertFalse(output.isEmpty(), "Output should not be empty");
        assertTrue(output.indexOf("userStrong1") < output.indexOf("userGood1"), "Strong credentials should be listed before good ones for descending order.");
        assertTrue(output.indexOf("userGood1") < output.indexOf("userWeak1"), "Good credentials should be listed before weak ones for descending order.");
    }



}



