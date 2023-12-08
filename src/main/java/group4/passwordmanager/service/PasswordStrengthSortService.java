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

    public void listCredentialsByStrength(boolean ascending) {
        Comparator<Credential> strengthComparator = Comparator.comparing(
                Credential::getPasswordStrength,
                ascending ? Comparator.naturalOrder() : Comparator.reverseOrder()
        );

        List<Credential> sortedCredentials = credentialStorage.getAllCredentials().stream()
                .sorted(strengthComparator)
                .collect(Collectors.toList());

        for (Credential credential : sortedCredentials) {
            System.out.println("Email/Username: " + credential.getEmailOrUsername()
                    + ", Website: " + credential.getWebsite()
                    + ", Strength: " + credential.getPasswordStrength());
        }
    }

    public void listCredentialsByCategory(String category) {
        List<Credential> filteredCredentials = credentialStorage.getAllCredentials().stream()
                .filter(c -> c.getPasswordStrength().equalsIgnoreCase(category))
                .collect(Collectors.toList());

        for (Credential credential : filteredCredentials) {
            System.out.println("Email/Username: " + credential.getEmailOrUsername()
                    + ", Website: " + credential.getWebsite()
                    + ", Strength: " + credential.getPasswordStrength());
        }
    }
}
