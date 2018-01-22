package services.interfaces;

import models.interfaces.User;

import java.util.List;

public interface UserService {

    /**
     * create a new User in the database
     * @param iob_user this user must be saved in the database
     * @return true if the user was saved in the database, otherwise false
     */
    boolean createNewUserInDatabase(User iob_user);

    /**
     * look if the user exists in the database
     * @param iob_user the user to log in
     * @return true if the user exists in the database
     */
    User login(User iob_user);

    /**
     * change the password of a user
     * @param iob_user change the password of this user
     * @param iva_newPassword the new password
     * @return true if the password was changed
     */
    boolean changePassword(User iob_user, String iva_newPassword);

    User getUserByEmail(String iob_email);

    List<User> getAllUser();
}
