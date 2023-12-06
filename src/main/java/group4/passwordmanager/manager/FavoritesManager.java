package group4.passwordmanager.manager;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.service.CredentialService;

import java.util.List;
import java.util.Scanner;

public class FavoritesManager {
    public static void markAsFavorite(Scanner scanner, CredentialService credentialService) {
        List<Credential> credentials = credentialService.getAllCredentials();

        int lastIndex = credentials.size() - 1;

        if (lastIndex >= 0) {
            Credential lastCredential = credentials.get(lastIndex);

            System.out.println("Do you want to save this account as a favorite? (yes/no)");
            String favoriteOption = scanner.nextLine().toLowerCase();

            if ("yes".equals(favoriteOption)) {
                lastCredential.setFavorite(true);
                credentialService.updateCredential(lastCredential);
                System.out.println("Account saved as a favorite!");
            } else {
                System.out.println("Account not saved as a favorite.");
            }
        } else {
            System.out.println("No account found to save as a favorite.");
        }
    }

    public static void markAsFavoriteDuringEdit(Scanner scanner, CredentialService credentialService, int index) {
        List<Credential> credentials = credentialService.getAllCredentials();

        if (index >= 0 && index < credentials.size()) {
            Credential credential = credentials.get(index);

            System.out.println("Do you want to save this account as a favorite? (yes/no)");
            String favoriteOption = scanner.nextLine().toLowerCase();

            if ("yes".equals(favoriteOption)) {
                credential.setFavorite(true);
            } else {
                credential.setFavorite(false);
            }

            credentialService.updateCredential(credential);

            System.out.println("Account updated as a favorite!");
        } else {
            System.out.println("Invalid index.");
        }
    }

}
