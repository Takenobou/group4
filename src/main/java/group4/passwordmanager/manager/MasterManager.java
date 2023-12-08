package group4.passwordmanager.manager;

import group4.passwordmanager.model.Master;
import group4.passwordmanager.service.MasterService;

public class MasterManager {

    private static MasterService masterService;

    private static Master master;

    public MasterManager(MasterService masterService, Master master){
        this.masterService = masterService;
        this.master = master;
    }

    // Create master pass
    public static void createMaster(){
        // Generate Master pass
        String password = "masterPasswordNew";
        // set master pass
        master.setMasterPassword(password);
        masterService.displayMessage("Master Password set successfully.");
        System.out.println(master.getMasterPassword());
    }

    // Edit master pass

    // Delete master pass
}