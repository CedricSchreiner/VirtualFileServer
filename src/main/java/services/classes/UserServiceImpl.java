package services.classes;

import builder.DaoObjectBuilder;
import dao.interfaces.UserDao;
import models.classes.User;
import models.exceptions.UserAlreadyExistsException;
import models.exceptions.UserDirectoryNotCreated;
import models.exceptions.UserEmptyException;
import models.exceptions.UsersNotEqualException;
import services.interfaces.UserService;
import utilities.Utils;

import java.io.File;
import java.util.List;

import static models.constants.UserConstants.*;

public class UserServiceImpl implements UserService {
    private final UserDao gob_userDao = DaoObjectBuilder.getUserDaoObject();

    /**
     * create a new User in the database
     *
     * @param iob_user this user must be saved in the database
     * @return true if the user was saved in the database, otherwise false
     */
    public boolean createNewUserInDatabase(User iob_user) {
        User lob_user;
        File lob_dir;
        String lva_userName;
        String lva_userDirectoryName;
        String lva_password;

        if (!gob_userDao.getUser(iob_user.getEmail()).testisEmpty()) {
            throw new UserAlreadyExistsException(GC_USER_ALREADY_EXISTS);
        }

        lva_password = PasswordService.encryptPassword(iob_user.getPassword());
        iob_user.setPassword(lva_password);

        if (gob_userDao.createUser(iob_user)) {
            lob_user = getUserByEmail(iob_user.getEmail());
            lva_userName = lob_user.getName();
            lva_userDirectoryName = lva_userName + lob_user.getUserId();

            lob_dir = new File(Utils.getRootDirectory() + lva_userDirectoryName);

            if (!lob_dir.mkdir()) {
                throw new UserDirectoryNotCreated(GC_USER_DIRECTORY_NOT_CREATED);
            }

            return true;
        }

        return false;
    }

    /**
     * Checks if the user exists in the database
     *
     * @param iob_user the user to log in
     * @return the user which logged in
     */
    public User login(User iob_user) {
        User lob_user;
        lob_user = gob_userDao.getUser(iob_user.getEmail());

        if (lob_user.testisEmpty()) {
            throw new UserEmptyException(GC_EMPTY_USER);
        }

        String encryptedPassword = PasswordService.encryptPassword(iob_user.getPassword());
        iob_user.setPassword(encryptedPassword);

        if (!iob_user.equals(lob_user)) {
            throw new UsersNotEqualException(GC_USERS_NOT_EQUAL);
        }

        return lob_user;
    }

    /**
     * change the password of a user
     *
     * @param iob_user        change the password of this user
     * @param iva_newPassword the new password
     * @return true if the password was changed
     */
    public boolean changePassword(User iob_user, String iva_newPassword) {
        User lob_user = gob_userDao.getUser(iob_user.getEmail());

        if (lob_user.testisEmpty()) {
            throw new UserEmptyException(GC_EMPTY_USER);
        }

        lob_user.setPassword(PasswordService.encryptPassword(iva_newPassword));
        return gob_userDao.updatePassword(lob_user);
    }

    @Override
    public User getUserByEmail(String iob_email) {
        return gob_userDao.getUser(iob_email);
    }

    @Override
    public List<User> getAllUser() {
        return gob_userDao.getAllUsers();
    }

    public User getUserById(int iva_id) {
        for (User lob_user : getAllUser()) {
            if (lob_user.getUserId() == iva_id) {
                return lob_user;
            }
        }

        return null;
    }
}


