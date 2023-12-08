package group4.passwordmanager.manager;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.service.PasswordStrengthSortService;

import java.util.Scanner;

public class StrengthSortManager {
    private final PasswordStrengthSortService strengthSortService;

    public StrengthSortManager(CredentialStorage storage) {
        this.strengthSortService = new PasswordStrengthSortService(storage);
    }

    public void sortStrengthOptions(Scanner scanner) {
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
    }
}
