package group4.passwordmanager.service;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;

import java.util.List;
import java.util.Scanner;

public class CredentialEditService {
    private CredentialStorage credentialStorage;

    public CredentialEditService(CredentialStorage storage) {
        this.credentialStorage = storage;
    }

    public void editEmailOrUsername(Scanner scanner) {
        List<Credential> credentials = credentialStorage.getAllCredentials();

        if (credentials.isEmpty()) {
            System.out.println("No credentials available to edit.");
            return;
        }

        System.out.println("Select the credential to edit by entering its number:");
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

        Credential selectedCredential = credentials.get(index);
        System.out.println("Selected Credential: " + selectedCredential.getEmailOrUsername() + ", " + selectedCredential.getWebsite());
        System.out.println("Enter new Email/Username:");
        String newEmailOrUsername = scanner.nextLine();

        selectedCredential.setEmailOrUsername(newEmailOrUsername);
        credentialStorage.update(selectedCredential);

        System.out.println("Credential updated successfully.");
    }
}
