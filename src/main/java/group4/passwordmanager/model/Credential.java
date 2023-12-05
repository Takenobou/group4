    package group4.passwordmanager.model;

    import java.time.LocalDateTime;
    import java.util.List;

    public class Credential {
        private String emailOrUsername;
        private String password;
        private String website;
        private List<String> tags;
        private boolean isFavorite;
        private LocalDateTime lastAccessed;

        public Credential() {
        }

        public Credential(String emailOrUsername, String password, String website) {
            this.emailOrUsername = emailOrUsername;
            this.password = password;
            this.website = website;
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
    }
