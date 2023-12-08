package group4.passwordmanager.service;

import group4.passwordmanager.model.CredentialStorage;

public class DeleteAllCredentials {

    private CredentialStorage credentialStorage;

    public DeleteAllCredentials(CredentialStorage storage) {
        this.credentialStorage = storage;
    }

    public void deleteAllCredentials() {
        credentialStorage.getAllCredentials().clear();
        credentialStorage.saveCredentials();
    }
}
