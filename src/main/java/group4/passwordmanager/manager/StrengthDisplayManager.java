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
        if (credentials.isEmpty()) {
            System.out.println("No credentials available.");
            return;
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
    }
}
