package group4.passwordmanager.manager;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.service.CredentialService;
import group4.passwordmanager.service.StrengthEvaluatorService;

import java.util.List;

public class StrengthDisplayManager {
    private final CredentialService credentialService;

    public StrengthDisplayManager(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    public void displayStrengths() {
        List<Credential> credentials = credentialService.getAllCredentials();
        if (credentials == null || credentials.isEmpty()) {  // Add null check here
            System.out.println("No credentials available.");
            return;
        }

        for (Credential credential : credentials) {
            String strength = StrengthEvaluatorService.evaluatePasswordStrength(credential.getPassword());
            String tags = (credential.getTags() != null) ? String.join(", ", credential.getTags()) : "No Tags";
            System.out.println("Email/Username: " + (credential.getEmailOrUsername() != null ? credential.getEmailOrUsername() : "Unknown Email/Username")  // Handle null email/username
                    + ", Website: " + (credential.getWebsite() != null ? credential.getWebsite() : "Unknown Website")  // Handle null website
                    + ", Tags: " + tags
                    + ", Strength: " + (strength != null ? strength : "Unknown Strength"));  // Handle null strength
        }
    }

}
