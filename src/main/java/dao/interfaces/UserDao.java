package dao.interfaces;

import models.interfaces.User;

import java.util.List;

public interface UserDao {

    /**
     * get a user from the database with a specific email
     * @param iva_email get the user with this email
     * @return the found user
     */
    User getUser(String iva_email);

    List<User> getAllUsers();
    /**
     * delete a user from the database
     * @param user delete that specific user
     * @return false if an error occurred, otherwise true
     */
    boolean deleteUser(User user);

    /**
     * create a new user in the database
     * @param user create this user
     * @return false if an error occurred, otherwise true
     */
    boolean createUser(User user);

    /**
     * update an user in the database
     * @param user update this user
     * @return false if an error occurred, otherwise true
     */
    boolean updateEmail(User user);
    boolean updatePassword(User user);
    boolean updateName(User user);
}
