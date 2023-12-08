package group4.passwordmanager.model;

import java.security.SecureRandom;
import java.util.Base64;

public class Master {

    private String masterPassword;

    public Master(){}

    public Master(String masterPassword){
        this.masterPassword = masterPassword;
    }

    public boolean hasMasterPassword() {
        return masterPassword != null && !masterPassword.isEmpty();
    }

    // Generates a random master password.
    public String generateRandomPassword(int length) {
        byte[] randomBytes = new byte[length];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getEncoder().encodeToString(randomBytes).substring(0, length);
    }

    public void updateMasterPassword(String newPassword) {
        this.masterPassword = newPassword;
    }

    public String getMasterPassword(){return masterPassword;}

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }

    public void deleteMasterPassword() {
        this.masterPassword = null;
    }

}
