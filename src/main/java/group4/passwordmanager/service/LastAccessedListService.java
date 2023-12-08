package group4.passwordmanager.service;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LastAccessedListService {
    private CredentialStorage credentialStorage;

    public LastAccessedListService(CredentialStorage storage) {
        this.credentialStorage = storage;
    }

    public void listCredentialsByLastAccessed() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<Credential> sortedCredentials = credentialStorage.getAllCredentials()
                .stream()
                .sorted(Comparator.comparing(Credential::getLastAccessed, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        for (int i = 0; i < sortedCredentials.size(); i++) {
            Credential credential = sortedCredentials.get(i);
            String lastAccessedFormatted = credential.getLastAccessed() != null ? credential.getLastAccessed().format(formatter) : "Never Accessed";
            System.out.println((i + 1) + ": Email/Username: " + credential.getEmailOrUsername() + ", Website: " + credential.getWebsite() + ", Last Accessed: " + lastAccessedFormatted);
        }
    }
}
