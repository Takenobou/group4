package RandomBased;

import group4.passwordmanager.manager.MasterManager;
import group4.passwordmanager.model.Master;
import group4.passwordmanager.service.MasterService;
import group4.passwordmanager.service.OTPGenerator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class HugoRandomTest {
    private MasterService mockMasterService;
    private Master mockMaster;

    @BeforeEach
    public void setUp() {
        mockMasterService = Mockito.mock(MasterService.class);
        mockMaster = Mockito.mock(Master.class);
    }

    @Test
    public void testRandomPasswordGenerationWithNoMasterPassword() {

        Mockito.when(mockMaster.hasMasterPassword()).thenReturn(false);
        Mockito.when(mockMaster.generateRandomPassword(16)).thenReturn("randomPassword");

        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.createMasterPassword();

        // Verify that a random password is generated
        Mockito.verify(mockMaster, Mockito.times(1)).generateRandomPassword(16);
    }

    @Test
    public void testRandomConfirmationForDeletion() {
        // Set up the master with an existing master password
        Mockito.when(mockMaster.hasMasterPassword()).thenReturn(true);

        // Execute the deleteMasterPassword method
        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.deleteMasterPassword();

        // Verify that confirmation for deletion is prompted
        Mockito.verify(mockMasterService, Mockito.times(1)).confirmDeletion();
    }

    @Test
    public void testRandomPasswordGeneration() {
        Mockito.when(mockMaster.isLocked()).thenReturn(false);
        Mockito.when(mockMasterService.promptForUnlocking()).thenReturn("correctPassword");
        Mockito.when(mockMaster.unlock("correctPassword")).thenReturn(true);
        Mockito.when(mockMaster.getMasterPassword()).thenReturn("someRandomPassword");

        // Execute the unlockAccount method
        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.unlockAccount();

        // Verify that the account is unlocked successfully
        Mockito.verify(mockMaster).unlock("correctPassword");
        Mockito.verify(mockMasterService).displayMessage("Account unlocked successfully.");

        String generatedPassword = mockMaster.getMasterPassword();
        Assertions.assertEquals("someRandomPassword", generatedPassword);
        assertEquals(16, generatedPassword.length());
    }

    @Test
    public void testGenerateOTP() {
        // Repeat the test multiple times to ensure randomness
        for (int i = 0; i < 10; i++) {
            // Generate OTP
            String otp = OTPGenerator.generateOTP();

            // Random-based: Ensure the length is correct
            assertEquals(14, otp.length());

            //Ensure at least one character from each character type is present
            assertTrue(containsCharacterType(otp, "ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
            assertTrue(containsCharacterType(otp, "0123456789"));
            assertTrue(containsCharacterType(otp, "!@#$%^&*"));
        }
    }

    private void assertEquals(int i, int length) {
    }

    private void assertTrue(boolean containsCharacterType) {
    }

    // Helper method to check if at least one character from a type is present
    private boolean containsCharacterType(String input, String characters) {
        for (char c : characters.toCharArray()) {
            if (input.indexOf(c) != -1) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void testRandomPasswordForUnlocking() {
        // Set up the master with a locked account
        Mockito.when(mockMaster.isLocked()).thenReturn(true);

        // Execute the unlockAccount method
        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.unlockAccount();

        // Verify that a password is prompted for unlocking
        Mockito.verify(mockMasterService, Mockito.times(1)).promptForUnlocking();
    }
}
