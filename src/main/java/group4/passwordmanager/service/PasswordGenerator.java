package group4.passwordmanager.service;

import java.security.SecureRandom;
import java.util.Scanner;

public class PasswordGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";

    public static String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        int length = 8; // The length of the randomized password

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }

    public static String enterPassword(Scanner scanner) {
        System.out.println("Choose an option for password: (1) Enter your own password, (2) Generate a random password");
        String passwordOption = scanner.nextLine();

        if ("1".equals(passwordOption)) {
            System.out.println("Enter Password:");
            return scanner.nextLine();
        } else if ("2".equals(passwordOption)) {
            String generatedPassword = generateRandomPassword();
            System.out.println("Generated Password: " + generatedPassword);
            return generatedPassword;
        } else {
            System.out.println("Invalid option. Defaulting to your own password.");
            System.out.println("Enter Password:");
            return scanner.nextLine();
        }
    }

    public static String editPassword(Scanner scanner, String currentPassword) {
        System.out.println("Do you want to change the password? (1) Yes, (2) No");
        String changePasswordOption = scanner.nextLine();

        if ("1".equals(changePasswordOption)) {
            System.out.println("Choose an option for password: (1) Enter your new password, (2) Generate a new random password");
            String newPasswordOption = scanner.nextLine();

            if ("1".equals(newPasswordOption)) {
                System.out.println("Enter New Password:");
                return scanner.nextLine();
            } else if ("2".equals(newPasswordOption)) {
                String newPassword = generateRandomPassword();
                System.out.println("Generated Password: " + newPassword);
                return newPassword;
            } else {
                System.out.println("Invalid option. Keeping the current password.");
                return currentPassword;
            }
        } else {
            return currentPassword;  // Keep the current password
        }
    }
}
