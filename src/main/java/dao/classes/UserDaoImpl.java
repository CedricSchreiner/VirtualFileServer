package dao.classes;

import dao.enums.ColNameUser;
import dao.interfaces.UserDao;
import models.classes.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static dao.constants.AdminDaoConstants.*;
import static dao.constants.DaoConstants.*;
import static dao.constants.UserDaoConstants.*;
import static utilities.Utils.convertIntToBoolean;

public class UserDaoImpl implements UserDao {
//----------------------------------------------------------------------------------------------------------------------
// Prepared Statements
//----------------------------------------------------------------------------------------------------------------------

    /**
     * SELECT * FROM User LEFT OUTER JOIN Admin ON User.userId = Admin.userId WHERE email = ?
     */
    private static final String GC_GET_USER = "SELECT * FROM " + TABLE_USER + " LEFT OUTER JOIN " + TABLE_ADMIN +
            " ON " + TABLE_USER + "." + COL_USER_ID + " = " + TABLE_ADMIN + "." + COL_ADMIN_USER_ID +
            " WHERE " + COL_USER_EMAIL + " = ?";

    /**
     * DELETE FROM User WHERE userId = ?
     */
    private static final String GC_DELETE_USER = "DELETE FROM " + TABLE_USER + " WHERE " + COL_USER_ID + " = ?";

    /**
     * INSERT INTO User (email, password, name) VALUES (?, ?, ?)
     */
    private static final String GC_ADD_USER = "INSERT INTO " + TABLE_USER + " (" + COL_USER_EMAIL + ", " +
            COL_USER_PASSWORD + ", " + COL_USER_NAME + ") VALUES (?, ?, ?)";

    /**
     * UPDATE User SET $ = ? WHERE userId = ?
     */
    private static final String GC_UPDATE_USER = "UPDATE " + TABLE_USER + " SET $ = ? WHERE " + COL_USER_ID + " = ?";

    /**
     * SELECT * FROM User LEFT OUTER JOIN Admin ON User.userId = Admin.userId
     */
    private static final String GC_GET_ALL_USERS = "SELECT * FROM " + TABLE_USER + " LEFT OUTER JOIN " + TABLE_ADMIN +
            " ON " + TABLE_USER + "." + COL_USER_ID + " = " + TABLE_ADMIN + "." + COL_ADMIN_USER_ID;

//----------------------------------------------------------------------------------------------------------------------

    private final DatabaseConnection gob_databaseConnection = DatabaseConnection.getInstance();

    /**
     * Get an user from the database with a specific email
     *
     * @param iva_email get the user with this email
     * @return the found user
     */
    @Override
    public User getUser(String iva_email) {
        User lob_aUser = new User();
        ResultSet lob_rs = null;
        Connection lob_connection = null;
        PreparedStatement lob_preparedStatement = null;

        try {
            lob_connection = this.gob_databaseConnection.getConnection();
            lob_preparedStatement = lob_connection.prepareStatement(GC_GET_USER);

            lob_preparedStatement.setString(PARAMETER_1, iva_email);
            lob_rs = lob_preparedStatement.executeQuery();

            while (lob_rs.next()) {
                lob_aUser.setEmail(lob_rs.getString(COL_USER_EMAIL));
                lob_aUser.setPassword(lob_rs.getString(COL_USER_PASSWORD));
                lob_aUser.setName(lob_rs.getString(COL_USER_NAME));

                if (lob_rs.getInt(COL_ADMIN_ID) > 0) {
                    lob_aUser.setIsAdmin(true);
                } else {
                    lob_aUser.setIsAdmin(false);
                }

                lob_aUser.setUserId(lob_rs.getInt(COL_USER_ID));
                lob_aUser.setAdminId(lob_rs.getInt(COL_ADMIN_ID));

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (lob_rs != null) {
                    lob_rs.close();
                }

                if (lob_preparedStatement != null) {
                    lob_preparedStatement.close();
                }

                if (lob_connection != null) {
                    lob_connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return lob_aUser;
    }

    /**
     * Get all users from db
     *
     * @return all user
     */
    @Override
    public List<User> getAllUsers() {
        List<User> lob_userList = new ArrayList<>();
        ResultSet lob_rs;
        User lob_aUser;

        try (Connection lob_connection = this.gob_databaseConnection.getConnection();
             PreparedStatement lob_preparedStatement = lob_connection.prepareStatement(GC_GET_ALL_USERS)) {

            lob_rs = lob_preparedStatement.executeQuery();

            while (lob_rs.next()) {
                lob_aUser = new User();

                lob_aUser.setEmail(lob_rs.getString(COL_USER_EMAIL));
                lob_aUser.setPassword(lob_rs.getString(COL_USER_PASSWORD));
                lob_aUser.setName(lob_rs.getString(COL_USER_NAME));

                if (lob_rs.getInt(COL_ADMIN_ID) > 0) {
                    lob_aUser.setIsAdmin(true);
                } else {
                    lob_aUser.setIsAdmin(false);
                }

                lob_aUser.setUserId(lob_rs.getInt(COL_USER_ID));
                lob_aUser.setAdminId(lob_rs.getInt(COL_ADMIN_ID));

                lob_userList.add(lob_aUser);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lob_userList;
    }

    /**
     * Delete an user from the database
     *
     * @param user delete that specific user
     * @return false if an error occurred, otherwise true
     */
    @Override
    public boolean deleteUser(User user) {
        int iva_rowCount = 0;

        try (Connection lob_connection = this.gob_databaseConnection.getConnection();
             PreparedStatement lob_preparedStatement = lob_connection.prepareStatement(GC_DELETE_USER)) {

            lob_preparedStatement.setInt(PARAMETER_1, user.getUserId());
            iva_rowCount = lob_preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return convertIntToBoolean(iva_rowCount);
    }

    /**
     * Create a new user in the database
     *
     * @param user create this user
     * @return false if an error occurred, otherwise true
     */
    @Override
    public boolean createUser(User user) {
        int iva_rowCount = 0;

        try (Connection lob_connection = this.gob_databaseConnection.getConnection();
             PreparedStatement lob_preparedStatement = lob_connection.prepareStatement(GC_ADD_USER)) {

            lob_preparedStatement.setString(PARAMETER_1, user.getEmail());
            lob_preparedStatement.setString(PARAMETER_2, user.getPassword());
            lob_preparedStatement.setString(PARAMETER_3, user.getName());

            iva_rowCount = lob_preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return convertIntToBoolean(iva_rowCount);
    }

    /**
     * Update the e-mail of an user
     *
     * @param user update this user
     * @return false if an error occurred, otherwise true
     */
    @Override
    public boolean updateEmail(User user) {
        return updateUser(user, ColNameUser.Email, user.getEmail());
    }

    /**
     * Update the password of an user
     *
     * @param user update this user
     * @return false if an error occurred, otherwise true
     */
    @Override
    public boolean updatePassword(User user) {
        return updateUser(user, ColNameUser.Password, user.getPassword());
    }

    /**
     * Update the name of an user
     *
     * @param user update this user
     * @return false if an error occurred, otherwise true
     */
    @Override
    public boolean updateName(User user) {
        return updateUser(user, ColNameUser.Name, user.getName());
    }

    /**
     * Update an attribute of an user
     *
     * @param user        the user to update
     * @param colNameUser attribute name
     * @param value       the new value
     * @return false if an error occurred, otherwise true
     */
    private boolean updateUser(User user, ColNameUser colNameUser, String value) {
        String lob_preparedStatementString = GC_UPDATE_USER.replace("$", colNameUser.getColName());
        int iva_rowCount = 0;

        try (Connection lob_connection = this.gob_databaseConnection.getConnection();
             PreparedStatement lob_preparedStatement = lob_connection.prepareStatement(lob_preparedStatementString)) {

            lob_preparedStatement.setString(PARAMETER_1, value);
            lob_preparedStatement.setInt(PARAMETER_2, user.getUserId());

            iva_rowCount = lob_preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return convertIntToBoolean(iva_rowCount);
    }
}
