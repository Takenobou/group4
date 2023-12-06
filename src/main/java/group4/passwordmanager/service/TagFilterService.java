package group4.passwordmanager.service;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;

import java.util.List;
import java.util.stream.Collectors;

public class TagFilterService {
    private CredentialStorage credentialStorage;

    public TagFilterService(CredentialStorage storage) {
        this.credentialStorage = storage;
    }

    public void listCredentialsByTag(String tag) {
        List<Credential> filteredCredentials = credentialStorage.getAllCredentials().stream()
                .filter(credential -> credential.getTags() != null && credential.getTags().contains(tag))
                .collect(Collectors.toList());

        if (filteredCredentials.isEmpty()) {
            System.out.println("No credentials found for tag: " + tag);
            return;
        }

        for (int i = 0; i < filteredCredentials.size(); i++) {
            Credential credential = filteredCredentials.get(i);
            System.out.println((i + 1) + ": Email/Username: " + credential.getEmailOrUsername() + ", Website: " + credential.getWebsite() + ", Tags: " + credential.getTags());
        }
    }
}
