package services.classes;

import services.interfaces.UserServiceInterface;
import models.classes.UserImpl;

public class UserService implements UserServiceInterface{
    /**
     * create a new User in the database
     *
     * @param iob_user this user must be saved in the database
     * @return true if the user was saved in the database, otherwise false
     */
    public boolean createNewUserInDatabase(UserImpl iob_user) {
        return false;
    }

    /**
     * look if the user exists in the database
     *
     * @param iob_user the user to log in
     * @return true if the user exists in the database
     */
    public boolean login(UserImpl iob_user) {
        return false;
    }

    /**
     * change the password of a user
     *
     * @param iob_user        change the password of this user
     * @param iva_newPassword the new password
     * @return true if the password was changed
     */
    public boolean changePassword(UserImpl iob_user, String iva_newPassword) {
        return false;
    }
}
