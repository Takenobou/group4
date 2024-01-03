    package group4.passwordmanager.model;

    import group4.passwordmanager.service.StrengthEvaluatorService;

    import java.time.LocalDateTime;
    import java.util.List;

    public class Credential {
        private String emailOrUsername;
        private String password;
        private String website;
        private List<String> tags;
        private boolean isFavorite;
        private LocalDateTime lastAccessed;

        private String passwordStrength;

        // Field for last modified date
        private LocalDateTime lastModified;

        public Credential(String string, String string2, String string3, String string4, String string5) {
        }

        public Credential(String emailOrUsername, String password, String website, LocalDateTime lastAccessed2) {
            this.emailOrUsername = emailOrUsername;
            this.password = password;
            this.website = website;
            this.lastAccessed = lastAccessed2;
            //Set the password strength
            this.passwordStrength = StrengthEvaluatorService.evaluatePasswordStrength(password);

        }

        public Credential() {
        }

        public String getPasswordStrength() {
            return passwordStrength;
        }

        public void setPasswordStrength(String passwordStrength) {
            this.passwordStrength = passwordStrength;
        }

        public String getEmailOrUsername() {
            return emailOrUsername;
        }

        public void setEmailOrUsername(String emailOrUsername) {
            this.emailOrUsername = emailOrUsername;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public boolean isFavorite() {
            return isFavorite;
        }

        public void setFavorite(boolean favorite) {
            isFavorite = favorite;
        }

        public LocalDateTime getLastAccessed() {
            return lastAccessed;
        }

        public void setLastAccessed(LocalDateTime lastAccessed) {
            this.lastAccessed = lastAccessed;
        }


        public LocalDateTime getLastModified() {
            return lastModified;
        }

        public void setLastModified(LocalDateTime lastModified) {
            this.lastModified = lastModified;
        }
    }
