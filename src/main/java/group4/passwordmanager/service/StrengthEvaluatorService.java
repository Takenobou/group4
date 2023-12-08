package group4.passwordmanager.service;

public class StrengthEvaluatorService {
    public static String evaluatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return "Password is empty";
        }

        boolean hasLetters = false;
        boolean hasDigits = false;
        boolean hasSpecialChars = false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetters = true;
            } else if (Character.isDigit(c)) {
                hasDigits = true;
            } else {
                hasSpecialChars = true;
            }

            //if all conditions are met to check further
            if (hasLetters && hasDigits && hasSpecialChars) {
                break;
            }
        }

        //evaluate  strength based on character types found
        if (hasLetters && hasDigits && hasSpecialChars) {
            return "Strong";
        } else if (hasLetters && hasDigits) {
            return "Good";
        } else {
            return "Weak";
        }
    }
}
