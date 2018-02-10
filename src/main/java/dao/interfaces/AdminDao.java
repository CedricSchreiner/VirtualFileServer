package dao.interfaces;

import models.classes.User;

public interface AdminDao {
    /**
     * Sets a User as an admin
     * @param iva_user the user
     * @return false if an error occurred otherwise true
     */
    boolean addAdmin(User iva_user);

    /**
     * Removes the admin status from an user
     * @param iva_user the user
     * @return false if an error occurred otherwise true
     */
    boolean removeAdmin(User iva_user);
}
