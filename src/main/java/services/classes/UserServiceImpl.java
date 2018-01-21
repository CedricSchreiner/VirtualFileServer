package services.classes;

import builder.DaoObjectBuilder;
import dao.interfaces.UserDao;
import models.interfaces.User;
import services.interfaces.UserService;
import models.classes.UserImpl;

import java.util.List;

public class UserServiceImpl implements UserService {
    UserDao lob_userDao = DaoObjectBuilder.getUserDaoObject();

    /**
     * create a new User in the database
     *
     * @param iob_user this user must be saved in the database
     * @return true if the user was saved in the database, otherwise false
     */
    public boolean createNewUserInDatabase(User iob_user) {
        return lob_userDao.createUser(iob_user);
    }

    /**
     * look if the user exists in the database
     *
     * @param iob_user the user to log in
     * @return true if the user exists in the database
     */
    public boolean login(User iob_user) {
        return false;
    }

    /**
     * change the password of a user
     *
     * @param iob_user        change the password of this user
     * @param iva_newPassword the new password
     * @return true if the password was changed
     */
    public boolean changePassword(User iob_user, String iva_newPassword) {
        iob_user.setPassword(iva_newPassword);
        return lob_userDao.updatePassword(iob_user);
    }

    @Override
    public User getUserByEmail(String iob_email) {
        return lob_userDao.getUser(iob_email);
    }

    @Override
    public List<User> getAllUser() {
        return lob_userDao.getAllUsers();
    }
}
