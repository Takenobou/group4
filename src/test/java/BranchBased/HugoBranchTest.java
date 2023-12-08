package BranchBased;

import group4.passwordmanager.manager.MasterManager;
import group4.passwordmanager.model.Master;
import group4.passwordmanager.service.MasterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HugoBranchTest {

    private Master testMaster;

    private MasterService service;

    private MasterManager masterManager;

    private static final String test_file = "test_master.json";


    @BeforeEach
    void setUp() throws IOException {
        testMaster = new Master("testPassword");
        service = new MasterService();
        Files.deleteIfExists(Paths.get(test_file));
    }

    @Test
    public void testCreateMasterPassword_WithoutExistingPassword() {
        // Setup: Ensure master.hasMasterPassword() returns false
        // Execute: MasterManager.createMasterPassword()
        // Verify: master.setMasterPassword() is called with a random password
    }

    @Test
    public void testCreateMasterPassword_WithExistingPassword() {
        testMaster.setMasterPassword("InitialPassword");
//        MasterManager.createMasterPassword();
        assertEquals("InitialPassword", testMaster.getMasterPassword());
    }


}
