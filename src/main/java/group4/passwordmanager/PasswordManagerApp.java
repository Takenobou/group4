package group4.passwordmanager;

import group4.passwordmanager.manager.CredentialManager;
import group4.passwordmanager.manager.TagManager;
import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.service.AccessHistoryTracker;
import group4.passwordmanager.service.CredentialService;
import group4.passwordmanager.service.DeleteAllCredentials;
import group4.passwordmanager.service.SearchService;

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


        while (true) {
            System.out.println("\nChoose an option: (search, list, create, view, edit, delete_all, exit)");
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

                case "delete_all":
                    System.out.println("Are you sure you want to delete all credentials? (yes/no)");
                    String confirmation = scanner.nextLine();
                    if ("yes".equalsIgnoreCase(confirmation)) {
                        deletionService.deleteAllCredentials();
                        System.out.println("All credentials have been deleted.");
                    } else {
                        System.out.println("Operation cancelled.");
                    }
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