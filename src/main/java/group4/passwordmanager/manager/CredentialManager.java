package group4.passwordmanager.manager;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.service.AccessHistoryTracker;
import group4.passwordmanager.service.CredentialService;
import group4.passwordmanager.service.PasswordGenerator;
import group4.passwordmanager.service.SearchService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;

public class CredentialManager {
    private CredentialService credentialService;
    private TagManager tagManager;
    private AccessHistoryTracker historyTracker;
    private SearchService searchService;

    public CredentialManager(CredentialService credentialService, TagManager tagManager,
                             AccessHistoryTracker historyTracker, SearchService searchService) {
        this.credentialService = credentialService;
        this.tagManager = tagManager;
        this.historyTracker = historyTracker;
        this.searchService = searchService;
    }


    public void listCredentials(CredentialService credentialService) {
        List<Credential> credentials = credentialService.getAllCredentials();
        for (int i = 0; i < credentials.size(); i++) {
            Credential credential = credentials.get(i);
            System.out.println((i + 1) + ": Email/Username: " + credential.getEmailOrUsername() + ", Website: " + credential.getWebsite() + ", Tags " + credential.getTags());
        }
    }

    public void createCredential(Scanner scanner, CredentialService credentialService, TagManager tagManager) {
        System.out.println("Enter Email or Username:");
        String emailOrUsername = scanner.nextLine();

        // Call PasswordGenerator to enter password
        String password = PasswordGenerator.enterPassword(scanner);

        System.out.println("Enter Website:");
        String website = scanner.nextLine();

        // Call TagManager to enter tags
        List<String> tags = tagManager.enterTags(scanner);

        Credential credential = new Credential(emailOrUsername, password, website);
        credential.setTags(tags);
        credentialService.addCredential(credential);

        System.out.println("Credential added successfully.");
    }

    public void viewCredential(CredentialService credentialService, int index, AccessHistoryTracker historyTracker) {
        index -= 1;
        Credential credential = credentialService.getCredentialByIndex(index);
        if (credential != null) {
            // Display all details of the credential
            System.out.println("Email/Username: " + credential.getEmailOrUsername());
            System.out.println("Password: " + credential.getPassword());
            System.out.println("Website: " + credential.getWebsite());
            historyTracker.trackAccessHistory(credential);
            System.out.println("Tags: " + credential.getTags());

            historyTracker.trackAccessHistory(credential);

            if (credential.getLastModified() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                String formattedDate = credential.getLastModified().format(formatter);
                System.out.println("Last Modified: " + formattedDate);



                long daysSinceModification = ChronoUnit.DAYS.between(credential.getLastModified(), LocalDateTime.now());
                System.out.println("Days since last modification: " + daysSinceModification + " days");


                if (daysSinceModification > 30) {
                    System.out.println("Recommendation: Consider updating this password. It has been " + daysSinceModification + " days since it was last updated.");
                }
            } else {
                System.out.println("Last Modified: This credential has never been updated.");
            }


        } else {
            System.out.println("Invalid index.");
        }
    }

    public void editCredential(Scanner scanner, CredentialService credentialService, TagManager tagManager, int index) {
        index -= 1;
        Credential credential = credentialService.getCredentialByIndex(index);
        if (credential != null) {
            credential.setLastModified(LocalDateTime.now());
            credentialService.updateCredential(credential);
            System.out.println("Credential updated successfully. Last modified on: " + credential.getLastModified());

            System.out.println("Editing Credential: " + credential.getEmailOrUsername());
            System.out.println("Current Password: " + credential.getPassword());
            System.out.println("Current Website: " + credential.getWebsite());
            System.out.println("Current Tags: " + credential.getTags());

            System.out.println("Enter new Email/Username (leave blank to keep current):");
            String newEmailOrUsername = scanner.nextLine();

            String newPassword = PasswordGenerator.editPassword(scanner, credential.getPassword());

            System.out.println("Enter new Website (leave blank to keep current):");
            String newWebsite = scanner.nextLine();

            List<String> newTags = tagManager.editTags(scanner, credential.getTags());

            credentialService.editCredential(index, newEmailOrUsername, newPassword, newWebsite);
            System.out.println("Credential updated successfully.");

        } else {
            System.out.println("Invalid index.");
        }
    }

    public void searchCredentials(Scanner scanner, SearchService searchService, String searchTerm) {
        searchService.searchCredentialsAndPrintDetails(scanner, searchTerm);
    }
}
