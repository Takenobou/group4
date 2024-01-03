package group4.passwordmanager.service;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PasswordStrengthSortService {
    private final CredentialStorage credentialStorage;

    public PasswordStrengthSortService(CredentialStorage storage) {
        this.credentialStorage = storage;
    }

    private int strengthValue(String passwordStrength) {
        switch (passwordStrength) {
            case "Strong":
                return 3;
            case "Good":
                return 2;
            case "Weak":
                return 1;
            default:
                return 0;
        }
    }

    public void listCredentialsByStrength(boolean ascending) {
        List<Credential> allCredentials = credentialStorage.getAllCredentials();
        if (allCredentials == null) {
            System.out.println("No credentials available.");
            return;
        }

        Comparator<Credential> byStrength = Comparator.comparingInt(
                c -> strengthValue(StrengthEvaluatorService.evaluatePasswordStrength(c.getPassword()))
        );

        List<Credential> sortedCredentials = allCredentials.stream()
                .sorted(ascending ? byStrength : byStrength.reversed())
                .collect(Collectors.toList());

        for (Credential credential : sortedCredentials) {
            System.out.println("Email/Username: " + credential.getEmailOrUsername() +
                    ", Website: " + credential.getWebsite() +
                    ", Strength: " + credential.getPasswordStrength());
        }
    }

    public void listCredentialsByCategory(String category) {
        List<Credential> filteredCredentials = credentialStorage.getAllCredentials()
                .stream()
                .filter(c -> StrengthEvaluatorService.evaluatePasswordStrength(c.getPassword()).equalsIgnoreCase(category))
                .collect(Collectors.toList());

        for (Credential credential : filteredCredentials) {
            System.out.println("Email/Username: " + credential.getEmailOrUsername() +
                    ", Website: " + credential.getWebsite() +
                    ", Strength: " + credential.getPasswordStrength());
        }
    }
}
