package services.classes;

import builder.DaoObjectBuilder;
import builder.ModelObjectBuilder;
import dao.interfaces.UserDao;
import models.exceptions.UserEmptyException;
import models.exceptions.UsersNotEqualException;
import models.interfaces.User;
import services.interfaces.UserService;

import java.util.List;

import static models.constants.UserConstants.GC_EMPTY_USER;
import static models.constants.UserConstants.GC_USERS_NOT_EQUAL;

public class UserServiceImpl implements UserService {
    private final UserDao gob_userDao = DaoObjectBuilder.getUserDaoObject();

    /**
     * create a new User in the database
     *
     * @param iob_user this user must be saved in the database
     * @return true if the user was saved in the database, otherwise false
     */
    public boolean createNewUserInDatabase(User iob_user) {
        if (!gob_userDao.getUser(iob_user.getEmail()).isEmpty()) {
            return false;
        }

        String password = PasswordService.encryptPassword(iob_user.getPassword());
        iob_user.setPassword(password);

        return gob_userDao.createUser(iob_user);
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

        if (lob_user.isEmpty()) {
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
     * @param iob_user change the password of this user
     * @param iva_newPassword the new password
     * @return true if the password was changed
     */
    public boolean changePassword(User iob_user, String iva_newPassword) {
        User lob_user = gob_userDao.getUser(iob_user.getEmail());

        if (lob_user.isEmpty()) {
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

    public static void main(String[] args) {
        UserServiceImpl userService = new UserServiceImpl();
        User user = ModelObjectBuilder.getUserModel();
        user.setEmail("Bla2");
        user.setPassword("password");
        user.setName("name");
        userService.createNewUserInDatabase(user);
    }
}


