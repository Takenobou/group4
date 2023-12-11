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

        Mockito.when(mockMaster.hasMasterPassword()).thenReturn(false);
        Mockito.when(mockMaster.generateRandomPassword(16)).thenReturn("randomPassword");

        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.createMasterPassword();

        Mockito.verify(mockMaster, Mockito.times(1)).hasMasterPassword();
        Mockito.verify(mockMaster, Mockito.times(1)).generateRandomPassword(16);
        Mockito.verify(mockMaster, Mockito.times(1)).setMasterPassword("randomPassword");
        Mockito.verify(mockMasterService, Mockito.times(1)).displayMessage("Generated Random Master Password: randomPassword");
    }

    @Test
    public void testMasterPasswordAlreadyExists() {

        Mockito.when(mockMaster.hasMasterPassword()).thenReturn(true);

        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.createMasterPassword();

        Mockito.verify(mockMaster, Mockito.times(1)).hasMasterPassword();
        Mockito.verify(mockMaster, Mockito.times(0)).generateRandomPassword(16);
        Mockito.verify(mockMaster, Mockito.times(0)).setMasterPassword(Mockito.anyString());
        Mockito.verify(mockMasterService, Mockito.times(1)).displayMessage("Master Password already exists.");
    }

    @Test
    public void testDeleteMasterPasswordSuccessfully() {
        // Set up the master existing password
        Mockito.when(mockMaster.hasMasterPassword()).thenReturn(true);
        Mockito.when(mockMasterService.confirmDeletion()).thenReturn(true);

        // Execute the deleteMasterPassword method
        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.deleteMasterPassword();

        Mockito.verify(mockMaster, Mockito.times(1)).deleteMasterPassword();
        Mockito.verify(mockMasterService, Mockito.times(1)).displayMessage("Master Password deleted successfully.");
    }

    @Test
    public void testUnlockLockedAccountWithCorrectPassword() {

        Mockito.when(mockMaster.isLocked()).thenReturn(true);
        Mockito.when(mockMasterService.promptForUnlocking()).thenReturn("correctPassword");
        Mockito.when(mockMaster.unlock("correctPassword")).thenReturn(true);

        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.unlockAccount();

        // Verify that the account is unlocked successfully
        Mockito.verify(mockMasterService, Mockito.times(1)).promptForUnlocking();
        Mockito.verify(mockMaster, Mockito.times(1)).unlock("correctPassword");
        Mockito.verify(mockMasterService, Mockito.times(1)).displayMessage("Account unlocked successfully.");
    }

    @Test
    public void testUnlockLockedAccountWithIncorrectPassword() {
        Mockito.when(mockMaster.isLocked()).thenReturn(true);
        Mockito.when(mockMasterService.promptForUnlocking()).thenReturn("incorrectPassword");
        Mockito.when(mockMaster.unlock("incorrectPassword")).thenReturn(false);

        // Execute the unlockAccount method
        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.unlockAccount();

        Mockito.verify(mockMasterService, Mockito.times(1)).promptForUnlocking();
        Mockito.verify(mockMaster, Mockito.times(1)).unlock("incorrectPassword");
        Mockito.verify(mockMasterService, Mockito.times(1)).displayMessage("Incorrect Master Password. Account remains locked.");
    }

    @Test
    public void testUnlockUnlockedAccount() {
        Mockito.when(mockMaster.isLocked()).thenReturn(false);

        // Execute the unlockAccount method
        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.unlockAccount();

        Mockito.verify(mockMasterService, Mockito.times(0)).promptForUnlocking();
        Mockito.verify(mockMaster, Mockito.times(0)).unlock(Mockito.anyString());
        Mockito.verify(mockMasterService, Mockito.times(1)).displayMessage("Account is already unlocked.");
    }
    
    @Test
    public void testEditMasterPassword_ExistingPassword() {
        Mockito.when(mockMaster.hasMasterPassword()).thenReturn(true);
        Mockito.when(mockMaster.generateRandomPassword(16)).thenReturn("newPassword");
        
        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);

        masterManager.editMasterPassword(); // Replace YourClass with the actual class name

        Mockito.verify(mockMaster).updateMasterPassword("newPassword");
        Mockito.verify(mockMasterService).displayMessage("Master Password updated successfully to newPassword");
    }

}
