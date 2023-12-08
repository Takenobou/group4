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

    // Method to handle the creation or generation of the master password
    public static void createMasterPassword() {
        if (!master.hasMasterPassword()) {
            String randomPassword = master.generateRandomPassword(16);
            master.setMasterPassword(randomPassword);
            masterService.displayMessage("Generated Random Master Password: " + randomPassword);
        } else {
            masterService.displayMessage("Master Password already exists.");
        }
    }

    public static void editMasterPassword() {
        if (master.hasMasterPassword()) {
            String newPassword = master.generateRandomPassword(16);
            master.updateMasterPassword(newPassword);
            masterService.displayMessage("Master Password updated successfully to " + newPassword);
        } else {
            masterService.displayMessage("No existing Master Password found. Please create one first.");
        }
    }

    public static void deleteMasterPassword() {
        if (master.hasMasterPassword()) {
            if (masterService.confirmDeletion()) {
                master.deleteMasterPassword();
                masterService.displayMessage("Master Password deleted successfully.");
            } else {
                masterService.displayMessage("Master Password deletion cancelled.");
            }
        } else {
            masterService.displayMessage("No existing Master Password found.");
        }
    }

    public static void lockAccount() {
        master.lock();
        masterService.displayMessage("Account locked successfully.");
    }
}