package group4.passwordmanager.manager;

import group4.passwordmanager.model.CredentialStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TagManager {

    private final CredentialStorage credentialStorage;

    public TagManager(CredentialStorage credentialStorage) {
        this.credentialStorage = credentialStorage;
    }

    // Add a method to enter tags
    public List<String> enterTags(Scanner scanner) {
        System.out.println("Enter Tags (comma-separated, leave blank if none):");
        String tagsInput = scanner.nextLine();
        List<String> tags = new ArrayList<>();
        if (!tagsInput.isEmpty()) {
            String[] tagArray = tagsInput.split(",");
            for (String tag : tagArray) {
                tags.add(tag.trim());
            }
        }
        return tags;
    }

    public List<String> editTags(Scanner scanner, List<String> existingTags) {
        while (true) {
            System.out.println("Current Tags:");
            for (int i = 0; i < existingTags.size(); i++) {
                System.out.println((i + 1) + ": " + existingTags.get(i));
            }

            System.out.println("Enter the number of the tag to edit, type 0 to add a new tag, or leave blank to continue:");
            String selection = scanner.nextLine();

            if (selection.isEmpty()) {
                return existingTags;  // Continue without editing
            }

            try {
                int index = Integer.parseInt(selection);
                if (index == 0) {
                    System.out.println("Enter the new tag name:");
                    String addedTagName = scanner.nextLine();
                    existingTags.add(addedTagName);
                } else if (index >= 1 && index <= existingTags.size()) {
                    System.out.println("Choose an option: (1) Delete tag, (2) Edit tag");
                    String option = scanner.nextLine();

                    switch (option) {
                        case "1":
                            existingTags.remove(index - 1);
                            break;
                        case "2":
                            System.out.println("Enter the new tag name:");
                            String newTagName = scanner.nextLine();
                            existingTags.set(index - 1, newTagName);
                            break;
                        default:
                            System.out.println("Invalid option. Try again.");
                            break;
                    }
                } else {
                    System.out.println("Invalid selection.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid selection.");
            }
        }
    }

}
