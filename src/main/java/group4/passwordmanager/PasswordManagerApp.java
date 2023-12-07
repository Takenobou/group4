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
        TagFilterService tagSearchService = new TagFilterService(storage);
        CredentialEditService credentialEditService = new CredentialEditService(storage);
        DeleteCredentialService credentialDeletionService = new DeleteCredentialService(storage);


        while (true) {
            System.out.println("\nChoose an option: (search, list, create, view, edit, strength," +
                    "delete, delete_all, list_by_tag, edit_email, exit)");
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
                    credentialManager.listCredentials(credentialService);
                    System.out.println("Enter the number of the credential to check password strength:");
                    try {
                        int index = Integer.parseInt(scanner.nextLine()) - 1;
                        Credential credential = credentialService.getCredentialByIndex(index);
                        if (credential != null) {
                            String passwordStrength = StrengthEvaluatorService.evaluatePasswordStrength(credential.getPassword());
                            System.out.println("Password Strength: " + passwordStrength);
                        } else {
                            System.out.println("Invalid index.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a number.");
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

                case "list_by_tag":
                    System.out.println("Enter the tag:");
                    String tag = scanner.nextLine();
                    tagSearchService.listCredentialsByTag(tag);
                    break;

                case "edit_email":
                    credentialEditService.editEmailOrUsername(scanner);
                    break;

                case "delete":
                    credentialDeletionService.deleteSpecificCredential(scanner);
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