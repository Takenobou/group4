package group4.passwordmanager.service;

import java.security.SecureRandom;

public class OTPGenerator {

    public static String generateOTP(){

        // Length and characters of password
        int length = 14;
        String alphaCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numericCharacters = "0123456789";
        String specialCharacters = "!@#$%^&*";
        String combinedChars = alphaCharacters + numericCharacters + specialCharacters;

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        // Append at least one of each type of character to the password
        sb.append(alphaCharacters.charAt(random.nextInt(alphaCharacters.length())));
        sb.append(numericCharacters.charAt(random.nextInt(numericCharacters.length())));
        sb.append(specialCharacters.charAt(random.nextInt(specialCharacters.length())));

        return sb.toString();
    }
}
