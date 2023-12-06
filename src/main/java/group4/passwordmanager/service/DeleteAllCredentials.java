package group4.passwordmanager.service;

import group4.passwordmanager.model.CredentialStorage;

public class DeleteAllCredentials {

    private CredentialStorage credentialStorage;

    public DeleteAllCredentials(CredentialStorage storage) {
        this.credentialStorage = storage;
    }

    public void deleteAllCredentials() {
        // Clear all credentials
        credentialStorage.getAllCredentials().clear();

        // Save the empty state to the file
        credentialStorage.saveCredentials();
    }
}
