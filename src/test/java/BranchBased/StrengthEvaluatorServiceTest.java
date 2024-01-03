package BranchBased;

import group4.passwordmanager.service.StrengthEvaluatorService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

//Tests here test the strength functionality.
public class StrengthEvaluatorServiceTest {
    @Test
    void testPasswordIsEmpty(){
        String pwd = "";
        String strength = StrengthEvaluatorService.evaluatePasswordStrength(pwd);
        assertEquals(strength, "Password is empty", "The password is empty and should return the empty message");
    }

    @Test
    void testPasswordIsNull(){
        String pwd = null;
        String strength = StrengthEvaluatorService.evaluatePasswordStrength(pwd);
        assertEquals(strength, "Password is empty", "The password is empty and should return the empty message");
    }

    @Test
    void testPasswordIsStrong(){
        String pwd = "password1234@";
        String strength = StrengthEvaluatorService.evaluatePasswordStrength(pwd);
        assertEquals(strength, "Strong", "The password is Strong because it has letter, digit and specials");
    }

    @Test
    void testPasswordIsGood(){
        String pwd = "password1234";
        String strength = StrengthEvaluatorService.evaluatePasswordStrength(pwd);
        assertEquals(strength, "Good", "The password is Good because it has letters and digits");
    }

    @Test
    void testPasswordIsWeakLettersOnly(){
        String pwd = "password";
        String strength = StrengthEvaluatorService.evaluatePasswordStrength(pwd);
        assertEquals(strength, "Weak", "The password is Weak because it has letters only");
    }

    @Test
    void testPasswordIsWeakNumbersOnly(){
        String pwd = "1234";
        String strength = StrengthEvaluatorService.evaluatePasswordStrength(pwd);
        assertEquals(strength, "Weak", "The password is Weak because it has numbers only");
    }

    @Test
    void testPasswordWithSpecialCharsOnly() {
        String pwd = "@#$%";
        String strength = StrengthEvaluatorService.evaluatePasswordStrength(pwd);
        assertEquals("Weak", strength, "The password is Weak because it has special characters only");
    }

    @Test
    void testPasswordWithLettersAndSpecialCharsOnly() {
        String pwd = "password@#";
        String strength = StrengthEvaluatorService.evaluatePasswordStrength(pwd);
        assertEquals("Good", strength, "The password is Good because it has letters and special characters");
    }

    @Test
    void testPasswordWithDigitsAndSpecialCharsOnly() {
        String pwd = "1234@#";
        String strength = StrengthEvaluatorService.evaluatePasswordStrength(pwd);
        assertEquals("Good", strength, "The password is Good because it has digits and special characters only");
    }

    @Test
    void testSingleCharacterPassword() {
        String pwdLetter = "a";
        String pwdDigit = "1";
        String pwdSpecial = "@";
        assertEquals("Weak", StrengthEvaluatorService.evaluatePasswordStrength(pwdLetter), "Single letter should be weak");
        assertEquals("Weak", StrengthEvaluatorService.evaluatePasswordStrength(pwdDigit), "Single digit should be weak");
        assertEquals("Weak", StrengthEvaluatorService.evaluatePasswordStrength(pwdSpecial), "Single special character should be weak");
    }

    @Test
    void testPasswordWithWhitespace() {
        String pwd = "password 123";
        String strength = StrengthEvaluatorService.evaluatePasswordStrength(pwd);
        assertEquals("Good", strength, "The password is Good because it has letters and digits, whitespace should not affect");
    }

    @Test
    void testPasswordWithOnlyWhitespace() {
        String pwd = "  ";
        String strength = StrengthEvaluatorService.evaluatePasswordStrength(pwd);
        assertEquals("Weak", strength, "The password is Weak because it has only whitespace");
    }

    @Test
    void testPasswordWithOnlyNumbersAndSpecials() {
        String pwd = "123!@#";
        String strength = StrengthEvaluatorService.evaluatePasswordStrength(pwd);
        assertEquals("Good", strength, "The password is Good because it has numbers and specials");
    }
}
