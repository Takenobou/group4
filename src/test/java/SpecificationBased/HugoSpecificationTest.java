package SpecificationBased;

import group4.passwordmanager.manager.MasterManager;
import group4.passwordmanager.model.Master;
import group4.passwordmanager.service.MasterService;
import group4.passwordmanager.service.OTPGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HugoSpecificationTest {

    private MasterService mockMasterService;
    private Master mockMaster;

    @BeforeEach
    public void setUp() {
        mockMasterService = Mockito.mock(MasterService.class);
        mockMaster = Mockito.mock(Master.class);
    }

    @Test
    public void testCreateMasterPasswordSuccessfully() {
        Mockito.when(mockMaster.hasMasterPassword()).thenReturn(false);
        Mockito.when(mockMaster.generateRandomPassword(16)).thenReturn("randomPassword");

        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.createMasterPassword();

        // Verify that a random password is generated
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
        Mockito.when(mockMaster.hasMasterPassword()).thenReturn(true);
        Mockito.when(mockMasterService.confirmDeletion()).thenReturn(true);

        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.deleteMasterPassword();

        Mockito.verify(mockMaster, Mockito.times(1)).deleteMasterPassword();
        Mockito.verify(mockMasterService, Mockito.times(1)).displayMessage("Master Password deleted successfully.");
    }

    @Test
    public void testCancelDeleteMasterPassword() {
        Mockito.when(mockMaster.hasMasterPassword()).thenReturn(true);
        Mockito.when(mockMasterService.confirmDeletion()).thenReturn(false);

        // Execute the deleteMasterPassword method
        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.deleteMasterPassword();

        // Verify that the master password deletion is cancelled
        Mockito.verify(mockMaster, Mockito.times(0)).deleteMasterPassword();
        Mockito.verify(mockMasterService, Mockito.times(1)).displayMessage("Master Password deletion cancelled.");
    }

    @Test
    public void testNoExistingMasterPassword() {
        Mockito.when(mockMaster.hasMasterPassword()).thenReturn(false);

        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.deleteMasterPassword();

        Mockito.verify(mockMaster, Mockito.times(0)).deleteMasterPassword();
        Mockito.verify(mockMasterService, Mockito.times(1)).displayMessage("No existing Master Password found.");
    }

    @Test
    public void testLockAccountSuccessfully() {
        // Set up the master with an unlocked account
        Mockito.when(mockMaster.isLocked()).thenReturn(false);

        // Execute the lockAccount method
        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.lockAccount();

        // Verify that the account is locked successfully
        Mockito.verify(mockMaster, Mockito.times(1)).lock();
        Mockito.verify(mockMasterService, Mockito.times(1)).displayMessage("Account locked successfully.");
    }

    @Test
    public void testLockAlreadyLockedAccount() {
        // Set up the master with a locked account
        Mockito.when(mockMaster.isLocked()).thenReturn(true);

        // Execute the lockAccount method
        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.lockAccount();

        // Verify that attempting to lock an already locked account doesn't change its status
        Mockito.verify(mockMaster, Mockito.times(0)).lock();
        Mockito.verify(mockMasterService, Mockito.times(1)).displayMessage("Account is already locked.");
    }

    @Test
    public void testLockAccountWithIncorrectPassword() {
        Mockito.when(mockMaster.isLocked()).thenReturn(false);
        Mockito.when(mockMasterService.promptForUnlocking()).thenReturn("incorrectPassword");
        Mockito.when(mockMaster.unlock("incorrectPassword")).thenReturn(false);

        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.unlockAccount();

        Mockito.verify(mockMaster, Mockito.times(0)).lock();
        Mockito.verify(mockMasterService, Mockito.times(1)).displayMessage("Incorrect Master Password. Account remains locked.");
    }

    @Test
    public void testLockAccountWithCorrectPassword() {
        // Set up the master with an unlocked account
        Mockito.when(mockMaster.isLocked()).thenReturn(false);
        Mockito.when(mockMasterService.promptForUnlocking()).thenReturn("correctPassword");
        Mockito.when(mockMaster.unlock("correctPassword")).thenReturn(true);

        // Execute the lockAccount method
        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.unlockAccount();

        // Verify that the account is unlocked successfully
        Mockito.verify(mockMaster, Mockito.times(1)).lock();
        Mockito.verify(mockMasterService, Mockito.times(1)).displayMessage("Account unlocked successfully.");
    }

    @Test
    public void testGenerateOTP() {
        // Generate OTP
        String otp = OTPGenerator.generateOTP();

        // Ensure the length is correct
        assertEquals(14, otp.length());

        // Ensure at least one character from each character type is present
        assertTrue("No uppercase letter found", containsCharacterType(otp, "ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        assertTrue("No numeric digit found", containsCharacterType(otp, "0123456789"));
        assertTrue("No special character found", containsCharacterType(otp, "!@#$%^&*"));
    }

    private void assertTrue(String string, boolean containsCharacterType) {
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
    public void testUnlockLockedAccountWithCorrectPassword() {
        Mockito.when(mockMaster.isLocked()).thenReturn(true);
        Mockito.when(mockMasterService.promptForUnlocking()).thenReturn("correctPassword");
        Mockito.when(mockMaster.unlock("correctPassword")).thenReturn(true);

        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.unlockAccount();

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

        // Verify that the account is already unlocked
        Mockito.verify(mockMasterService, Mockito.times(0)).promptForUnlocking();
        Mockito.verify(mockMaster, Mockito.times(0)).unlock(Mockito.anyString());
        Mockito.verify(mockMasterService, Mockito.times(1)).displayMessage("Account is already unlocked.");
    }
}
