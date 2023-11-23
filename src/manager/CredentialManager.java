package manager;

import model.CredentialStorage;
import service.AccessHistoryTracker;
import service.PasswordGenerator;

public class CredentialManager {
    private CredentialStorage storage;
    private PasswordGenerator passwordGenerator;
    private TagManager tagManager;
    private FavoritesManager favoritesManager;
    private AccessHistoryTracker accessHistoryTracker;

    public CredentialManager() {
        this.storage = new CredentialStorage();
        this.passwordGenerator = new PasswordGenerator();
        this.tagManager = new TagManager();
        this.favoritesManager = new FavoritesManager();
        this.accessHistoryTracker = new AccessHistoryTracker();
    }

    public CredentialStorage getCredentialStorage() {
        return this.storage;
    }


    // Methods to handle user stories:
    // - associateEmailOrUsername
    // - associatePassword
    // - retrieveEmailOrUsername
    // - retrievePassword
    // - associateWebsite
    // - editPassword
    // - etc...
}