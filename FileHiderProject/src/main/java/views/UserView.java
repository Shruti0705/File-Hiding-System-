package views;

import dao.DataDAO;
import model.Data;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserView {
    private static final Logger LOGGER = Logger.getLogger(UserView.class.getName());
    private final String email;

    public UserView(String email) {
        this.email = email;
    }

    public void home() {
        Scanner sc = new Scanner(System.in);
        do {
            System.out.println("Welcome " + this.email);
            System.out.println("Press 1 to show hidden files");
            System.out.println("Press 2 to hide a new file");
            System.out.println("Press 3 to unhide a file");
            System.out.println("Press 0 to exit");

            int ch;
            try {
                ch = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid input. Please enter a valid number.");
                continue;
            }

            switch (ch) {
                case 1 -> {
                    try {
                        List<Data> files = DataDAO.getAllFiles(this.email);
                        if (files.isEmpty()) {
                            System.out.println("No hidden files found.");
                        } else {
                            System.out.println("ID - File Name");
                            for (Data file : files) {
                                System.out.println(file.getId() + " - " + file.getFileName());
                            }
                        }
                    } catch (SQLException e) {
                        LOGGER.log(Level.SEVERE, "Error retrieving hidden files", e);
                    }
                }
                case 2 -> {
                    System.out.println("Enter the file path");
                    String path = sc.nextLine();
                    File f = new File(path);

                    if (!f.exists() || !f.isFile()) {
                        LOGGER.log(Level.WARNING, "Invalid file path. Please enter a valid file.");
                        continue;
                    }

                    Data file = new Data(0, f.getName(), path, this.email);
                    try {
                        DataDAO.hideFile(file);
                        System.out.println("File hidden successfully.");
                    } catch (SQLException | IOException e) {
                        LOGGER.log(Level.SEVERE, "Error hiding file", e);
                    }
                }
                case 3 -> {
                    try {
                        List<Data> files = DataDAO.getAllFiles(this.email);
                        if (files.isEmpty()) {
                            System.out.println("No hidden files found.");
                            continue;
                        }

                        System.out.println("ID - File Name");
                        for (Data file : files) {
                            System.out.println(file.getId() + " - " + file.getFileName());
                        }
                        System.out.println("Enter the ID of the file to unhide");

                        int id;
                        try {
                            id = Integer.parseInt(sc.nextLine());
                        } catch (NumberFormatException e) {
                            LOGGER.log(Level.WARNING, "Invalid input. Please enter a valid file ID.");
                            continue;
                        }

                        boolean isValidID = files.stream().anyMatch(file -> file.getId() == id);
                        if (isValidID) {
                            DataDAO.unhide(id);
                            System.out.println("File successfully unhidden.");
                        } else {
                            LOGGER.log(Level.WARNING, "Wrong ID. Please enter a valid file ID.");
                        }
                    } catch (SQLException | IOException e) {
                        LOGGER.log(Level.SEVERE, "Error unhiding file", e);
                    }
                }
                case 0 -> {
                    System.out.println("Exiting...");
                    System.exit(0);
                }
                default -> LOGGER.log(Level.WARNING, "Invalid choice. Please select a valid option.");
            }
        } while (true);
    }
}
