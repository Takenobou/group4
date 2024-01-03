package RandomBased;

import group4.passwordmanager.manager.DeleteAllManager;
import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.service.DeleteAllCredentials;
import group4.passwordmanager.service.DeleteCredentialService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

//Random tests for delete functionality.
public class DeleteRandomTests {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Mock
    private CredentialStorage mockCredentialStorage;

    @InjectMocks
    private DeleteAllCredentials deleteAllCredentialsService;

    private DeleteAllManager deleteAllManager;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(outContent));
        deleteAllManager = new DeleteAllManager(deleteAllCredentialsService);
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
        Mockito.reset(mockCredentialStorage);
    }

    @Test
    public void when100CredentialsExist_thenAllDeletedSuccessfully() {
        // Create 100 random credentials
        List<Credential> randomCredentials = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            randomCredentials.add(new Credential("user" + i + "@example.com", "password" + i, "website" + i, null));
        }

        // Mock the behavior of the CredentialStorage
        when(mockCredentialStorage.getAllCredentials()).thenReturn(randomCredentials);

        // Simulate user input "yes" for deletion confirmation
        Scanner scanner = new Scanner(new ByteArrayInputStream("yes\n".getBytes()));

        // Call the method under test
        deleteAllManager.deleteAllCredentialsOption(scanner);

        // Verify the credentials are cleared and saved
        verify(mockCredentialStorage, times(2)).getAllCredentials(); // Now expecting 2 invocations
        verify(mockCredentialStorage).saveCredentials();

        // Assert that all credentials have been deleted
        assertTrue(randomCredentials.isEmpty(), "All credentials should be deleted.");

        // Verify output message
        String output = outContent.toString();
        assertTrue(output.contains("All credentials have been deleted."), "Appropriate message should be displayed after deletion.");
    }

    @Test
    public void whenMultipleCredentialsExist_thenOnlySpecificCredentialDeleted() {
        // Create 100 random credentials
        List<Credential> randomCredentials = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            randomCredentials.add(new Credential("user" + i + "@example.com", "password" + i, "website" + i, null));
        }

        // Mock the behavior of the CredentialStorage
        when(mockCredentialStorage.getAllCredentials()).thenReturn(randomCredentials);
        Credential expectedDeletedCredential = randomCredentials.get(49); // Credential number 50 (0-indexed)

        // Simulate user input "50" for the credential to be deleted
        Scanner scanner = new Scanner(new ByteArrayInputStream("50\n".getBytes()));

        // Create an instance of the DeleteCredentialService
        DeleteCredentialService deleteCredentialService = new DeleteCredentialService(mockCredentialStorage);

        // Call the method under test
        deleteCredentialService.deleteSpecificCredential(scanner);

        // Verify the specific credential is deleted
        assertFalse(randomCredentials.contains(expectedDeletedCredential), "Credential No. 50 should be deleted.");

        // Verify the other credentials are still there
        assertEquals(99, randomCredentials.size(), "99 credentials should remain.");

        // Verify the saveCredentials method was called
        verify(mockCredentialStorage).saveCredentials();

        // Verify output message
        String output = outContent.toString();
        assertTrue(output.contains("Credential deleted successfully."), "Appropriate message should be displayed after deletion.");
    }


    @Test
    public void whenOneCredentialExists_thenItIsDeletedSuccessfully() {
        // Create 1 random credential
        Credential randomCredential = new Credential("user@example.com", "password", "website", null);

        // Mock the behavior of the CredentialStorage to return a list with the single credential
        List<Credential> credentialsList = new ArrayList<>();
        credentialsList.add(randomCredential);
        when(mockCredentialStorage.getAllCredentials()).thenReturn(credentialsList);

        // Simulate user input "1" for the credential to be deleted
        Scanner scanner = new Scanner(new ByteArrayInputStream("1\n".getBytes()));

        // Create an instance of the DeleteCredentialService
        DeleteCredentialService deleteCredentialService = new DeleteCredentialService(mockCredentialStorage);

        // Call the method under test
        deleteCredentialService.deleteSpecificCredential(scanner);

        // Verify the specific credential is deleted
        assertTrue(credentialsList.isEmpty(), "The credential should be deleted.");

        // Verify the saveCredentials method was called
        verify(mockCredentialStorage).saveCredentials();

        // Verify output message
        String output = outContent.toString();
        assertTrue(output.contains("Credential deleted successfully."), "Appropriate message should be displayed after deletion.");
    }

    @Test
    public void whenMultipleCredentialsExist_thenOnlySpecificRandomCredentialDeleted() {
        // Create a list of 10 random credentials
        List<Credential> randomCredentials = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            randomCredentials.add(new Credential("user" + i + "@example.com", "password" + i, "website" + i, null));
        }

        // Mock the behavior of the CredentialStorage
        when(mockCredentialStorage.getAllCredentials()).thenReturn(randomCredentials);

        // Choose a random index to delete
        int randomIndexToDelete = ThreadLocalRandom.current().nextInt(0, 10);

        // Simulate user input for the credential to be deleted
        Scanner scanner = new Scanner(new ByteArrayInputStream((randomIndexToDelete + 1 + "\n").getBytes()));

        Credential toDelete = randomCredentials.get(randomIndexToDelete);

        // Create an instance of the DeleteCredentialService
        DeleteCredentialService deleteCredentialService = new DeleteCredentialService(mockCredentialStorage);

        // Call the method under test
        deleteCredentialService.deleteSpecificCredential(scanner);

        // Verify the specific credential is deleted
        assertEquals(9, randomCredentials.size(), "Number of credentials should be one less.");
        assertFalse(randomCredentials.contains(toDelete), "Randomly selected credential should be deleted.");

        // Verify the saveCredentials method was called
        verify(mockCredentialStorage).saveCredentials();

        // Verify output message
        String output = outContent.toString();
        assertTrue(output.contains("Credential deleted successfully."), "Appropriate message should be displayed after deletion.");
    }


}
