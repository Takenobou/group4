package BranchBased;

import group4.passwordmanager.manager.DeleteAllManager;
import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.service.DeleteAllCredentials;
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

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

// Tests here test the delete_all function.
public class DeleteAllTest {
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
    public void whenUserConfirms_thenDeleteAllCredentials() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("yes\n".getBytes()));
        when(mockCredentialStorage.getAllCredentials()).thenReturn(new ArrayList<>()); // Empty list

        deleteAllManager.deleteAllCredentialsOption(scanner);

        verify(mockCredentialStorage).getAllCredentials(); // Verify retrieval of credentials
        verify(mockCredentialStorage, never()).saveCredentials(); // Verify saveCredentials() is never called

        String output = outContent.toString();
        assertTrue(output.contains("No credentials available to delete."), "Appropriate message should be displayed when no credentials are available.");
    }



    @Test
    public void whenUserCancels_thenNoActionTaken() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("no\n".getBytes()));

        deleteAllManager.deleteAllCredentialsOption(scanner);

        verify(mockCredentialStorage, never()).getAllCredentials(); // Verify no retrieval of credentials
        verify(mockCredentialStorage, never()).saveCredentials(); // Verify no save action

        String output = outContent.toString();
        assertTrue(output.contains("Operation cancelled."));
    }


    @Test
    public void whenCredentialsExist_thenAllDeletedSuccessfully() {
        // Set up a scanner with user input "yes"
        Scanner scanner = new Scanner(new ByteArrayInputStream("yes\n".getBytes()));

        // Creating a mutable list from an immutable list
        List<Credential> credentials = new ArrayList<>(List.of(
                new Credential("user1@example.com", "password1", "website1", null),
                new Credential("user2@example.com", "password2", "website2", null)
        ));

        // Mock the behavior of the CredentialStorage
        when(mockCredentialStorage.getAllCredentials()).thenReturn(credentials);

        // Call the method under test
        deleteAllManager.deleteAllCredentialsOption(scanner);

        // Verify the credentials are cleared and saved
        verify(mockCredentialStorage).saveCredentials();
        assertTrue(credentials.isEmpty(), "Credentials list should be empty after deletion");


        // Verify all credentials are deleted (list should be empty)
        assertEquals(0, credentials.size());

        // Verify the output message
        String output = outContent.toString();
        assertTrue(output.contains("All credentials have been deleted."));
    }

    @Test
    public void whenInvalidConfirmationInput_thenNoActionTaken() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("maybe\n".getBytes()));

        deleteAllManager.deleteAllCredentialsOption(scanner);

        verify(mockCredentialStorage, never()).getAllCredentials();
        verify(mockCredentialStorage, never()).saveCredentials();

        String output = outContent.toString();
        assertTrue(output.contains("Operation cancelled."), "No action should be taken on invalid input.");
    }

    @Test
    public void whenCredentialsExist_thenAllDeletedIncludingRandomOne() {
        // Create a list of 10 random credentials
        List<Credential> randomCredentials = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            randomCredentials.add(new Credential("user" + i + "@example.com", "password" + i, "website" + i, null));
        }

        // Mock the behavior of the CredentialStorage
        when(mockCredentialStorage.getAllCredentials()).thenReturn(randomCredentials);

        // Call the method under test
        deleteAllCredentialsService.deleteAllCredentials();

        // Verify the credentials are cleared and saved
        verify(mockCredentialStorage).getAllCredentials();
        verify(mockCredentialStorage).saveCredentials();

        // Assert that all credentials have been deleted
        assertTrue(randomCredentials.isEmpty(), "All credentials should be deleted.");
    }




}
