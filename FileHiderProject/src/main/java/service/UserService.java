package service;

import dao.UserDAO;
import model.User;

import java.sql.SQLException;

public class UserService {
    public static Integer saveUser(User user) {
        try {
            if (UserDAO.isExists(user.getEmail())) {
                return 1; // Instead of returning 0
            }
            return UserDAO.saveUser(user);
        } catch (SQLException ex) {
            ex.printStackTrace(); // Replace with proper logging
            return -1; // Return an error code instead of null
        }
    }

}
