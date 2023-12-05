package group4.passwordmanager.service;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.service.CredentialService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AccessHistoryTracker {
    // Methods to track and show last access time of passwords
    private final CredentialService credentialService;

    public AccessHistoryTracker(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    public void trackAccessHistory(Credential credential) {
        LocalDateTime currentTimestamp = LocalDateTime.now();
        LocalDateTime lastAccessed = credential.getLastAccessed();

        if (lastAccessed == null) {
            System.out.println("Last accessed: This is the first time it is accessed");
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String formattedTimestamp = lastAccessed.format(formatter);
            System.out.println("Last accessed: " + formattedTimestamp);
        }

        // Update the lastAccessed timestamp to the current time
        credential.setLastAccessed(currentTimestamp);
        credentialService.updateCredential(credential);
    }
}
