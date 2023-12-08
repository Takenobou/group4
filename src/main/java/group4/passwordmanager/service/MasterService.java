package group4.passwordmanager.service;

import group4.passwordmanager.model.Master;

import java.util.Scanner;

public class MasterService {

    private Scanner scanner = new Scanner(System.in);

    public void displayMessage(String message) {
        System.out.println(message);
    }

    public boolean confirmDeletion() {
        System.out.print("Are you sure you want to delete the Master Password? (yes/no): ");
        String input = scanner.nextLine();
        return "yes".equalsIgnoreCase(input.trim());
    }

    public void displayLockStatus(boolean isLocked) {
        if (isLocked) {
            System.out.println("Account is locked.");
        } else {
            System.out.println("Account is unlocked.");
        }
    }
}
