package group4.passwordmanager.service;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service to list credentials based on password strength.
 */
public class PasswordStrengthListingService {
    private final CredentialStorage credentialStorage;

    public PasswordStrengthListingService(CredentialStorage storage) {
        this.credentialStorage = storage;
    }

    /**
     * Convert strength category to a numerical value for sorting.
     */
    private int strengthValue(String passwordStrength) {
        switch (passwordStrength) {
            case "Strong":
                return 3;
            case "Good":
                return 2;
            case "Weak":
                return 1;
            default:
                return 0;  // For undefined strengths
        }
    }

    /**
     * Lists credentials sorted by password strength.
     * @param ascending Whether to sort in ascending order.
     */
    public void listCredentialsByStrength(boolean ascending) {
        // Comparator for sorting credentials by strength value
        Comparator<Credential> byStrength = Comparator.comparingInt(
                c -> strengthValue(StrengthEvaluatorService.evaluatePasswordStrength(c.getPassword()))
        );

        // Sort and collect the credentials
        List<Credential> sortedCredentials = credentialStorage.getAllCredentials()
                .stream()
                .sorted(ascending ? byStrength : byStrength.reversed())
                .collect(Collectors.toList());

        // Display each credential with its strength
        for (Credential credential : sortedCredentials) {
            String strength = StrengthEvaluatorService.evaluatePasswordStrength(credential.getPassword());
            System.out.println("Email/Username: " + credential.getEmailOrUsername() + ", Strength: " + strength);
        }
    }

    /**
     * Lists credentials by a specific strength category.
     * @param strengthCategory The strength category to filter by.
     */
    public void listCredentialsBySpecificStrength(String strengthCategory) {
        // Filter credentials by the specified strength category
        List<Credential> filteredCredentials = credentialStorage.getAllCredentials()
                .stream()
                .filter(c -> StrengthEvaluatorService.evaluatePasswordStrength(c.getPassword()).equals(strengthCategory))
                .collect(Collectors.toList());

        // Display each filtered credential
        for (Credential credential : filteredCredentials) {
            System.out.println("Email/Username: " + credential.getEmailOrUsername() + ", Strength: " + strengthCategory);
        }
    }
}
