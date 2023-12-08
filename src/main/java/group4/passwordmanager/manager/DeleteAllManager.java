package group4.passwordmanager.manager;

import group4.passwordmanager.service.DeleteAllCredentials;
import java.util.Scanner;

public class DeleteAllManager {
    private final DeleteAllCredentials deleteAllCredentialsService;

    public DeleteAllManager(DeleteAllCredentials deleteAllCredentialsService) {
        this.deleteAllCredentialsService = deleteAllCredentialsService;
    }

    public void deleteAllCredentialsOption(Scanner scanner) {
        System.out.println("Are you sure you want to delete all credentials? (yes/no)");
        String confirmation = scanner.nextLine();
        if ("yes".equalsIgnoreCase(confirmation)) {
            deleteAllCredentialsService.deleteAllCredentials();
            System.out.println("All credentials have been deleted.");
        } else {
            System.out.println("Operation cancelled.");
        }
    }
}
