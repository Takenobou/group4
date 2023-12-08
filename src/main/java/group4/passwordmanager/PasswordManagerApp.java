package group4.passwordmanager;

import group4.passwordmanager.manager.CredentialManager;
import group4.passwordmanager.manager.TagManager;
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
        PasswordStrengthSortService strengthSortService = new PasswordStrengthSortService(storage);


        while (true) {
            System.out.println("\nChoose an option: (search, list, create, view, edit," +
                    "delete, delete_all, last_accessed, exit)");
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
                    List<Credential> credentials = credentialService.getAllCredentials();
                    if (credentials.isEmpty()) {
                        System.out.println("No credentials available.");
                        break;
                    }

                    for (int i = 0; i < credentials.size(); i++) {
                        Credential credential = credentials.get(i);
                        String strength = StrengthEvaluatorService.evaluatePasswordStrength(credential.getPassword());
                        String tags = String.join(", ", credential.getTags());
                        System.out.println((i + 1) + ": Email/Username: " + credential.getEmailOrUsername()
                                + ", Website: " + credential.getWebsite()
                                + ", Tags: " + tags
                                + ", Strength: " + strength);
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

                case "last_accessed":
                    lastAccessedService.listCredentialsByLastAccessed();
                    break;


                case "delete":
                    credentialDeletionService.deleteSpecificCredential(scanner);
                    break;

                case "sort_strength":
                    System.out.println("Choose an option:\n1: Sort by category\n2: List by strength");
                    String sortOption = scanner.nextLine();
                    if ("1".equals(sortOption)) {
                        System.out.println("Enter strength category (Weak, Good, Strong):");
                        String category = scanner.nextLine();
                        strengthSortService.listCredentialsByCategory(category);
                    } else if ("2".equals(sortOption)) {
                        System.out.println("Choose an option:\n1: Strongest to Weakest\n2: Weakest to Strongest");
                        String orderOption = scanner.nextLine();
                        if ("1".equals(orderOption)) {
                            strengthSortService.listCredentialsByStrength(false); // false for descending order
                        } else if ("2".equals(orderOption)) {
                            strengthSortService.listCredentialsByStrength(true); // true for ascending order
                        } else {
                            System.out.println("Invalid option.");
                        }
                    } else {
                        System.out.println("Invalid option.");
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