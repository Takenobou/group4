package BranchBased;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.service.DeleteCredentialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Tests here test the "delete" function and delete functionalities.
public class DeleteCredentialTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final Scanner scanner = new Scanner(System.in);

    @Mock
    private CredentialStorage mockCredentialStorage;

    @InjectMocks
    private DeleteCredentialService deleteCredentialService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(outContent));
        deleteCredentialService = new DeleteCredentialService(mockCredentialStorage);
    }

    @Test
    public void whenNoCredentials_thenNotifyUser() {
        when(mockCredentialStorage.getAllCredentials()).thenReturn(List.of());

        deleteCredentialService.deleteSpecificCredential(new Scanner("1\n"));
        String output = outContent.toString();
        assertTrue(output.contains("No credentials available to delete."),
                "Output should notify user when no credentials are available.");
    }

    @Test
    public void whenValidIndex_thenDeleteCredential() {
        // Creating a mutable list from an immutable list
        List<Credential> credentials = new ArrayList<>(List.of(
                new Credential("user1@example.com", "password1", "website1", null),
                new Credential("user2@example.com", "password2", "website2", null)
        ));

        when(mockCredentialStorage.getAllCredentials()).thenReturn(credentials);

        deleteCredentialService.deleteSpecificCredential(new Scanner("1\n"));
        String output = outContent.toString();

        assertTrue(output.contains("Credential deleted successfully."),
                "Output should confirm successful deletion.");
        verify(mockCredentialStorage).saveCredentials(); // Verify credentials are saved after deletion
    }


    @Test
    public void whenInvalidIndex_thenNotifyUser() {
        List<Credential> credentials = List.of(new Credential("user1@example.com", "password1", "website1", null));
        when(mockCredentialStorage.getAllCredentials()).thenReturn(credentials);

        deleteCredentialService.deleteSpecificCredential(new Scanner("2\n"));
        String output = outContent.toString();
        assertTrue(output.contains("Invalid index."),
                "Output should notify user of invalid index.");
    }

    @Test
    public void whenNonNumericInput_thenNotifyUser() {
        List<Credential> credentials = List.of(new Credential("user1@example.com", "password1", "website1", null));
        when(mockCredentialStorage.getAllCredentials()).thenReturn(credentials);

        deleteCredentialService.deleteSpecificCredential(new Scanner("abc\n"));
        String output = outContent.toString();
        assertTrue(output.contains("Invalid input. Please enter a number."),
                "Output should notify user of invalid input.");
    }

    @Test
    public void whenNullInput_thenNotifyUser() {
        List<Credential> credentials = List.of(new Credential("user1@example.com", "password1", "website1", null));
        when(mockCredentialStorage.getAllCredentials()).thenReturn(credentials);

        deleteCredentialService.deleteSpecificCredential(new Scanner("\n"));
        String output = outContent.toString();
        assertTrue(output.contains("Invalid input. Please enter a number."),
                "Output should notify user of invalid input.");
    }

    @Test
    public void whenDeleteSpecific_thenOnlySpecificCredentialDeleted() {
        // Creating a mutable list of credentials
        List<Credential> credentials = new ArrayList<>(List.of(
                new Credential("user1@example.com", "password1", "website1", null),
                new Credential("user2@example.com", "password2", "website2", null),
                new Credential("user3@example.com", "password3", "website3", null)
        ));
        when(mockCredentialStorage.getAllCredentials()).thenReturn(credentials);

        //Deleting the second credential (index 1 in 0-based)
        deleteCredentialService.deleteSpecificCredential(new Scanner("2\n"));

        //Verifying
        assertEquals(2, credentials.size(), "The number of credentials should be reduced by one.");
        assertFalse(credentials.stream().anyMatch(cred ->
                cred.getEmailOrUsername().equals("user2@example.com") &&
                        cred.getWebsite().equals("website2")), "The specified credential should be deleted.");
        assertTrue(credentials.stream().anyMatch(cred ->
                cred.getEmailOrUsername().equals("user1@example.com") &&
                        cred.getWebsite().equals("website1")), "Other credentials should remain unchanged.");
        assertTrue(credentials.stream().anyMatch(cred ->
                cred.getEmailOrUsername().equals("user3@example.com") &&
                        cred.getWebsite().equals("website3")), "Other credentials should remain unchanged.");

        verify(mockCredentialStorage).saveCredentials(); // Verify credentials are saved after deletion
    }

    @Test
    public void whenDeletingMultipleSpecificCredentialsSequentially_thenEachIsDeleted() {
        List<Credential> credentials = new ArrayList<>(List.of(
                new Credential("user1@example.com", "password1", "website1", null),
                new Credential("user2@example.com", "password2", "website2", null),
                new Credential("user3@example.com", "password3", "website3", null)
        ));
        when(mockCredentialStorage.getAllCredentials()).thenReturn(credentials);

        // Delete the first credential (index 0)
        deleteCredentialService.deleteSpecificCredential(new Scanner("1\n"));

        //Now list has shifted, the second credential becomes the first one in the list
        deleteCredentialService.deleteSpecificCredential(new Scanner("1\n"));

        assertEquals(1, credentials.size(), "Two credentials should have been deleted.");
        assertTrue(credentials.stream().noneMatch(cred -> cred.getEmailOrUsername().equals("user1@example.com") || cred.getEmailOrUsername().equals("user2@example.com")),
                "First and second credentials should be deleted.");
        assertTrue(credentials.stream().anyMatch(cred -> cred.getEmailOrUsername().equals("user3@example.com")),
                "Third credential should remain.");
    }



    @Test
    public void whenInvalidIndexGreaterThanListSize_thenNotifyUser() {
        List<Credential> credentials = new ArrayList<>(List.of(
                new Credential("user1@example.com", "password1", "website1", null)
        ));
        when(mockCredentialStorage.getAllCredentials()).thenReturn(credentials);

        deleteCredentialService.deleteSpecificCredential(new Scanner("3\n"));
        String output = outContent.toString();
        assertTrue(output.contains("Invalid index."),
                "Output should notify user of invalid index.");
    }

    @Test
    public void whenDeletingCredentialWithSpecialCharacters_thenDeletedSuccessfully() {
        List<Credential> credentials = new ArrayList<>(List.of(
                new Credential("user!@#$%^&*().example.com", "password1", "website1", null)
        ));
        when(mockCredentialStorage.getAllCredentials()).thenReturn(credentials);

        deleteCredentialService.deleteSpecificCredential(new Scanner("1\n"));
        String output = outContent.toString();
        assertTrue(output.contains("Credential deleted successfully."),
                "Output should confirm successful deletion.");
    }

    @Test
    public void whenAttemptToDeleteSameCredentialTwice_thenNotifyUser() {
        List<Credential> credentials = new ArrayList<>(List.of(
                new Credential("user1@example.com", "password1", "website1", null)
        ));
        when(mockCredentialStorage.getAllCredentials()).thenReturn(credentials);

        deleteCredentialService.deleteSpecificCredential(new Scanner("1\n"));
        deleteCredentialService.deleteSpecificCredential(new Scanner("1\n"));
        String output = outContent.toString();
        assertTrue(output.contains("No credentials available to delete."),
                "Output should notify user when no credentials are available.");
    }


}
