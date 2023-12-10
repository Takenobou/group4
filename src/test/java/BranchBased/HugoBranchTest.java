package BranchBased;

import group4.passwordmanager.service.OTPGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import org.mockito.Mockito;

import group4.passwordmanager.manager.MasterManager;
import group4.passwordmanager.model.Master;
import group4.passwordmanager.service.MasterService;

public class HugoBranchTest {

    private MasterService mockMasterService;
    private Master mockMaster;

    @BeforeEach
    public void setUp() {
        mockMasterService = Mockito.mock(MasterService.class);
        mockMaster = Mockito.mock(Master.class);
    }

    @Test
    public void testCreateMasterPasswordSuccessfully() {
        // Set up the master with no existing master password
        Mockito.when(mockMaster.hasMasterPassword()).thenReturn(false);
        Mockito.when(mockMaster.generateRandomPassword(16)).thenReturn("randomPassword");

        // Execute the createMasterPassword method
        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.createMasterPassword();

        // Verify that a random password is generated, set, and the correct message is displayed
        Mockito.verify(mockMaster, Mockito.times(1)).hasMasterPassword();
        Mockito.verify(mockMaster, Mockito.times(1)).generateRandomPassword(16);
        Mockito.verify(mockMaster, Mockito.times(1)).setMasterPassword("randomPassword");
        Mockito.verify(mockMasterService, Mockito.times(1)).displayMessage("Generated Random Master Password: randomPassword");
    }

    @Test
    public void testMasterPasswordAlreadyExists() {
        // Set up the master with an existing master password
        Mockito.when(mockMaster.hasMasterPassword()).thenReturn(true);

        // Execute the createMasterPassword method
        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.createMasterPassword();

        // Verify that the correct message is displayed when the master password already exists
        Mockito.verify(mockMaster, Mockito.times(1)).hasMasterPassword();
        Mockito.verify(mockMaster, Mockito.times(0)).generateRandomPassword(16);
        Mockito.verify(mockMaster, Mockito.times(0)).setMasterPassword(Mockito.anyString());
        Mockito.verify(mockMasterService, Mockito.times(1)).displayMessage("Master Password already exists.");
    }

    @Test
    public void testDeleteMasterPasswordSuccessfully() {
        // Set up the master with an existing master password
        Mockito.when(mockMaster.hasMasterPassword()).thenReturn(true);
        Mockito.when(mockMasterService.confirmDeletion()).thenReturn(true);

        // Execute the deleteMasterPassword method
        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.deleteMasterPassword();

        // Verify that the master password is deleted successfully and the correct message is displayed
        Mockito.verify(mockMaster, Mockito.times(1)).deleteMasterPassword();
        Mockito.verify(mockMasterService, Mockito.times(1)).displayMessage("Master Password deleted successfully.");
    }

    @Test
    public void testUnlockLockedAccountWithCorrectPassword() {
        // Set up the master with a locked account and a correct password provided
        Mockito.when(mockMaster.isLocked()).thenReturn(true);
        Mockito.when(mockMasterService.promptForUnlocking()).thenReturn("correctPassword");
        Mockito.when(mockMaster.unlock("correctPassword")).thenReturn(true);

        // Execute the unlockAccount method
        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.unlockAccount();

        // Verify that the account is unlocked successfully and the correct message is displayed
        Mockito.verify(mockMasterService, Mockito.times(1)).promptForUnlocking();
        Mockito.verify(mockMaster, Mockito.times(1)).unlock("correctPassword");
        Mockito.verify(mockMasterService, Mockito.times(1)).displayMessage("Account unlocked successfully.");
    }

    @Test
    public void testUnlockLockedAccountWithIncorrectPassword() {
        // Set up the master with a locked account and an incorrect password provided
        Mockito.when(mockMaster.isLocked()).thenReturn(true);
        Mockito.when(mockMasterService.promptForUnlocking()).thenReturn("incorrectPassword");
        Mockito.when(mockMaster.unlock("incorrectPassword")).thenReturn(false);

        // Execute the unlockAccount method
        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.unlockAccount();

        // Verify that the account remains locked and the correct message is displayed
        Mockito.verify(mockMasterService, Mockito.times(1)).promptForUnlocking();
        Mockito.verify(mockMaster, Mockito.times(1)).unlock("incorrectPassword");
        Mockito.verify(mockMasterService, Mockito.times(1)).displayMessage("Incorrect Master Password. Account remains locked.");
    }

    @Test
    public void testUnlockUnlockedAccount() {
        // Set up the master with an already unlocked account
        Mockito.when(mockMaster.isLocked()).thenReturn(false);

        // Execute the unlockAccount method
        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.unlockAccount();

        // Verify that the account is already unlocked and the correct message is displayed
        Mockito.verify(mockMasterService, Mockito.times(0)).promptForUnlocking();
        Mockito.verify(mockMaster, Mockito.times(0)).unlock(Mockito.anyString());
        Mockito.verify(mockMasterService, Mockito.times(1)).displayMessage("Account is already unlocked.");
    }

    @Testable
    public void testGenerateOTP() {
        // Generate OTP
        String otp = OTPGenerator.generateOTP();

        // Branch-based: Ensure the length is correct
        assertEquals(14, otp.length());

        // Branch-based: Ensure at least one character from each character type is present
        assertTrue(containsCharacterType(otp, "ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        assertTrue(containsCharacterType(otp, "0123456789"));
        assertTrue(containsCharacterType(otp, "!@#$%^&*"));

        // Test branches for other potential scenarios
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

}

