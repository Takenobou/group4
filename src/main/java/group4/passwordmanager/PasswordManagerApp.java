package group4.passwordmanager;

import group4.passwordmanager.manager.*;
import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.service.*;

import java.util.List;
import java.util.Scanner;

public class PasswordManagerApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CredentialStorage storage = new CredentialStorage("credentials.json");
        CredentialService credentialService = new CredentialService(storage);
        AccessHistoryTracker historyTracker = new AccessHistoryTracker(credentialService);
        SearchService searchService = new SearchService(credentialService);
        TagManager tagManager = new TagManager(storage);
        CredentialManager credentialManager = new CredentialManager(credentialService, tagManager, historyTracker, searchService);
        DeleteAllCredentials deletionService = new DeleteAllCredentials(storage);
        DeleteCredentialService credentialDeletionService = new DeleteCredentialService(storage);
        LastAccessedListService lastAccessedService = new LastAccessedListService(storage);
        StrengthSortManager strengthSortManager = new StrengthSortManager(storage);
        DeleteAllManager deleteAllManager = new DeleteAllManager(deletionService);
        StrengthDisplayManager strengthDisplayManager = new StrengthDisplayManager(credentialService);

        while (true) {
            System.out.println("\nChoose an option: (search, list, create, view, edit," +
                    " delete, delete_all, last_accessed, strength, sort_strength, exit)");
            String option = scanner.nextLine();
            String[] parts = option.split(" ");
            String command = parts[0];

            switch (command.toLowerCase()) {
                case "search":
                    if (parts.length < 2) {
                        System.out.println("Please provide a search term (email or website or tag).");
                    } else {
                        String searchTerm = parts[1];
                        credentialManager.searchCredentials(scanner, searchService, searchTerm);
                    }
                    break;
                case "list":
                    credentialManager.listCredentials(credentialService);
                    break;
                case "create":
                    credentialManager.createCredential(scanner, credentialService, tagManager);

                    List<Credential> allCredentials = credentialService.getAllCredentials();
                    if (!allCredentials.isEmpty()) {
                        Credential lastCredential = allCredentials.get(allCredentials.size() - 1);
                        String passwordStrength = StrengthEvaluatorService.evaluatePasswordStrength(lastCredential.getPassword());
                        System.out.println("The strength of the newly created password is: " + passwordStrength);
                    }
                    break;
                case "view":
                case "edit":
                    if (parts.length < 2) {
                        System.out.println("Please provide an index number.");
                    } else {
                        int index = Integer.parseInt(parts[1]);
                        if (command.equals("view")) {
                            credentialManager.viewCredential(credentialService, index, historyTracker);
                        } else {
                            credentialManager.editCredential(scanner, credentialService, tagManager, index);
                        }
                    }
                    break;

                case "strength":
                    strengthDisplayManager.displayStrengths();
                    break;

                case "delete_all":
                    deleteAllManager.deleteAllCredentialsOption(scanner);
                    break;

                case "last_accessed":
                    lastAccessedService.listCredentialsByLastAccessed();
                    break;


                case "delete":
                    credentialDeletionService.deleteSpecificCredential(scanner);
                    break;

                case "sort_strength":
                    strengthSortManager.sortStrengthOptions(scanner);
                    break;

                case "exit":
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}