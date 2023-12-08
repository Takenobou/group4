package group4.passwordmanager.service;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;

import java.util.List;
import java.util.Scanner;

public class DeleteCredentialService {
    private CredentialStorage credentialStorage;


    //credential storage object to access and modify stored credentials
    public DeleteCredentialService(CredentialStorage storage) {
        this.credentialStorage = storage;
    }

    //func deletes single credential.  scanner object to read input from the user
    public void deleteSpecificCredential(Scanner scanner) {
        List<Credential> credentials = credentialStorage.getAllCredentials();

        if (credentials.isEmpty()) {
            System.out.println("No credentials available to delete.");
            return;
        }

        System.out.println("Select the credential to delete by entering its number:");
        for (int i = 0; i < credentials.size(); i++) {
            Credential credential = credentials.get(i);
            System.out.println((i + 1) + ": Email/Username: " + credential.getEmailOrUsername() + ", Website: " + credential.getWebsite());
        }

        int index;
        try {
            index = Integer.parseInt(scanner.nextLine()) - 1;
            if (index < 0 || index >= credentials.size()) {
                System.out.println("Invalid index.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        credentials.remove(index);
        credentialStorage.saveCredentials();

        System.out.println("Credential deleted successfully.");
    }
}
