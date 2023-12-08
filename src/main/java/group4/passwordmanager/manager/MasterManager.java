package group4.passwordmanager.manager;

import group4.passwordmanager.model.Master;
import group4.passwordmanager.service.MasterService;

public class MasterManager {

    private MasterService masterService;

    private static Master master;

    public MasterManager(MasterService masterService, Master master){
        this.masterService = masterService;
        this.master = master;
    }

    // Create master pass
    public static void createMaster(){
        // Dummy
        String password = "masterPasswordNew";
        master.setMasterPassword(password);
        System.out.println("Master password created.");
        System.out.println(master.getMasterPassword());
    }

    // Edit master pass

    // Delete master pass
}