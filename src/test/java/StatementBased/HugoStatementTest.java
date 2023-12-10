package StatementBased;

import group4.passwordmanager.manager.MasterManager;
import group4.passwordmanager.model.Master;
import group4.passwordmanager.service.MasterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class HugoStatementTest {

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
        Mockito.verify(mockMaster, Mockito.times(1)).hasMasterPassword();
        Mockito.verify(mockMasterService, Mockito.times(1)).confirmDeletion();
        Mockito.verify(mockMaster, Mockito.times(1)).deleteMasterPassword();
        Mockito.verify(mockMasterService, Mockito.times(1)).displayMessage("Master Password deleted successfully.");
    }

    @Test
    public void testCancelDeleteMasterPassword() {
        // Set up the master with an existing master password but cancel deletion
        Mockito.when(mockMaster.hasMasterPassword()).thenReturn(true);
        Mockito.when(mockMasterService.confirmDeletion()).thenReturn(false);

        // Execute the deleteMasterPassword method
        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.deleteMasterPassword();

        // Verify that the master password deletion is cancelled and the correct message is displayed
        Mockito.verify(mockMaster, Mockito.times(1)).hasMasterPassword();
        Mockito.verify(mockMasterService, Mockito.times(1)).confirmDeletion();
        Mockito.verify(mockMaster, Mockito.times(0)).deleteMasterPassword();
        Mockito.verify(mockMasterService, Mockito.times(1)).displayMessage("Master Password deletion cancelled.");
    }

    @Test
    public void testNoExistingMasterPassword() {
        // Set up the master with no existing master password
        Mockito.when(mockMaster.hasMasterPassword()).thenReturn(false);

        // Execute the deleteMasterPassword method
        MasterManager masterManager = new MasterManager(mockMasterService, mockMaster);
        masterManager.deleteMasterPassword();

        // Verify that the correct message is displayed when no existing master password is found
        Mockito.verify(mockMaster, Mockito.times(1)).hasMasterPassword();
        Mockito.verify(mockMasterService, Mockito.times(0)).confirmDeletion();
        Mockito.verify(mockMaster, Mockito.times(0)).deleteMasterPassword();
        Mockito.verify(mockMasterService, Mockito.times(1)).displayMessage("No existing Master Password found.");
    }

    @Test
    public void testUnlockAccountSuccessfully() {
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

}
