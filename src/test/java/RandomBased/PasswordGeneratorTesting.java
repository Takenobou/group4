package RandomBased;

import java.security.SecureRandom;

//Random generator for guaranteed "Strong", "Good" & "Weak" passwords.
//Due to not being my user story, I have created my own for testing.
//This can be reliably used in the future if the user wants to make their own specific password strength.
public class PasswordGeneratorTesting {
    private static final String NUMBERS = "0123456789";
    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*";

    public static String generateRandomPassword(int length, boolean includeLetters, boolean includeNumbers, boolean includeSpecialChars) {
        String charSet = "";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // Ensure at least one of each required type is included
        if (includeLetters) {
            charSet += LETTERS;
            password.append(LETTERS.charAt(random.nextInt(LETTERS.length())));
        }
        if (includeNumbers) {
            charSet += NUMBERS;
            password.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        }
        if (includeSpecialChars) {
            charSet += SPECIAL_CHARACTERS;
            password.append(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));
        }

        // Fill the rest of the password length with random characters from the combined set
        for (int i = password.length(); i < length; i++) {
            int randomIndex = random.nextInt(charSet.length());
            password.append(charSet.charAt(randomIndex));
        }

        // Shuffle the characters to avoid a predictable pattern
        char[] pwdArray = password.toString().toCharArray();
        for (int i = 0; i < pwdArray.length; i++) {
            int randomIndexToSwap = random.nextInt(pwdArray.length);
            char temp = pwdArray[randomIndexToSwap];
            pwdArray[randomIndexToSwap] = pwdArray[i];
            pwdArray[i] = temp;
        }

        return new String(pwdArray);
    }

    public static String generateStrongPassword() {
        return PasswordGeneratorTesting.generateRandomPassword(12, true, true, true);
    }

    public static String generateGoodPassword() {
        return PasswordGeneratorTesting.generateRandomPassword(10, true, true, false);
    }

    public static String generateWeakPassword() {
        return PasswordGeneratorTesting.generateRandomPassword(8, false, true, false);
    }
}
