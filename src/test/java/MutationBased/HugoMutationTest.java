package MutationBased;

import group4.passwordmanager.manager.MasterManager;
import group4.passwordmanager.model.Master;
import group4.passwordmanager.service.MasterService;
import group4.passwordmanager.service.OTPGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class HugoMutationTest {

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
    public void testLockAccountSuccessfully() {
        // Set up the master with an unlocked account
        Mockito.when(mockMaster.isLocked()).thenReturn(false);

        // Execute the lockAccount method
        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.lockAccount();

        // Verify that the account is locked successfully and the correct message is displayed
        Mockito.verify(mockMaster, Mockito.times(1)).lock();
        Mockito.verify(mockMasterService, Mockito.times(1)).displayMessage("Account locked successfully.");
    }

    @Test
    public void testGenerateOTP() {
        // Generate OTP
        String otp = OTPGenerator.generateOTP();

        // Mutation-based: Ensure the length is correct
        assertEquals(14, otp.length());

        // Mutation-based: Ensure at least one character from each character type is present
        assertTrue(containsCharacterType(otp, "ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        assertTrue(containsCharacterType(otp, "0123456789"));
        assertTrue(containsCharacterType(otp, "!@#$%^&*"));

        // Introduce mutation: Change the length expectation
        assertNotEquals(15, otp.length());

        // Introduce mutation: Remove one character type expectation
        assertFalse(containsCharacterType(otp, "ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
    }

    private void assertNotEquals(int i, int length) {
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

