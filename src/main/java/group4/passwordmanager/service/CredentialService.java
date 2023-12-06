    package group4.passwordmanager.service;

    import group4.passwordmanager.model.Credential;
    import group4.passwordmanager.model.CredentialStorage;

    import java.util.List;

    public class CredentialService {

        private final CredentialStorage storage;

        public CredentialService(CredentialStorage storage) {
            this.storage = storage;
        }

        public List<Credential> getAllCredentials() {
            return storage.getAllCredentials();
        }

        public Credential getCredentialByIndex(int index) {
            List<Credential> credentials = getAllCredentials();
            if (index >= 0 && index < credentials.size()) {
                return credentials.get(index);
            }
            return null;
        }

        public void addCredential(Credential credential) {
            // Logic to add a credential
            storage.store(credential);
        }

        public void editCredential(int index, String newEmailOrUsername, String newPassword, String newWebsite) {
            List<Credential> credentials = storage.getAllCredentials();
            if (index >= 0 && index < credentials.size()) {
                Credential credential = credentials.get(index);

                if (!newEmailOrUsername.isEmpty()) {
                    credential.setEmailOrUsername(newEmailOrUsername);
                }
                if (!newPassword.isEmpty()) {
                    credential.setPassword(newPassword);
                }
                if (!newWebsite.isEmpty()) {
                    credential.setWebsite(newWebsite);
                }

                storage.update(credential);
            }
        }

        public void updateCredential(Credential credential) {
            storage.update(credential);
        }

    }
