package group4.passwordmanager.model;

import java.security.SecureRandom;
import java.util.Base64;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Files;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Master {

    private String masterPassword;

    private boolean isLocked = true;

    private String filePath = "master_password.json";

    public Master(){loadMasterPassword();}

    public Master(String masterPassword){
        this.masterPassword = masterPassword;
        saveMasterPassword();
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
        saveMasterPassword();
    }

    public String getMasterPassword(){return masterPassword;}

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
        saveMasterPassword();
    }

    public void deleteMasterPassword() {
        this.masterPassword = null;
        saveMasterPassword();
    }

    private void loadMasterPassword() {
        try {
            if (Files.exists(Paths.get(filePath))) {
                FileReader reader = new FileReader(filePath);
                JSONObject jsonObject = new JSONObject(new JSONTokener(reader));
                this.masterPassword = jsonObject.optString("masterPassword", null);
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveMasterPassword() {
        try {
            FileWriter writer = new FileWriter(filePath);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("masterPassword", this.masterPassword);
            writer.write(jsonObject.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void lock() {
        this.isLocked = true;
    }

    public boolean isLocked() {
        return isLocked;
    }
}
