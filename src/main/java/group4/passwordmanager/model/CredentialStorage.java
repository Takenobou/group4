package group4.passwordmanager.model;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CredentialStorage {
    private final List<Credential> credentials;
    private final ObjectMapper objectMapper;
    private final File file;

    public CredentialStorage(String filename) {
        this.file = new File(filename);
        this.credentials = new ArrayList<>();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.registerModule(new JavaTimeModule());

        loadCredentials();
    }

    public void update(Credential credential) {
        for (int i = 0; i < credentials.size(); i++) {
            if (credentials.get(i).getEmailOrUsername().equals(credential.getEmailOrUsername())) {
                credentials.set(i, credential);
                saveCredentials();
                break;
            }
        }
    }

    public void store(Credential credential) {
        if (credential == null) {
            System.err.println("Cannot store null credential.");
            return;
        }

        // Check if the credential already exists
        boolean exists = credentials.stream()
                .anyMatch(c -> c.getEmailOrUsername().equals(credential.getEmailOrUsername()));
        if (!exists) {
            credentials.add(credential);
        } else {
            // If the credential exists, update it
            for (int i = 0; i < credentials.size(); i++) {
                if (credentials.get(i).getEmailOrUsername().equals(credential.getEmailOrUsername())) {
                    credentials.set(i, credential);
                    break;
                }
            }
        }
        saveCredentials();
    }

    public List<Credential> getAllCredentials() {
        return credentials;
    }

    private void loadCredentials() {
        if (file.exists() && !file.isDirectory()) {
            try {
                List<Credential> loadedCredentials = objectMapper.readValue(file, new TypeReference<>(){});
                credentials.addAll(loadedCredentials);
            } catch (IOException e) {
                System.err.println("Error loading credentials: " + e.getMessage());
            }
        }
    }

    public void saveCredentials() {
        try {
            objectMapper.writeValue(file, credentials);
        } catch (IOException e) {
            System.err.println("Error saving credentials: " + e.getMessage());
        }
    }
}
