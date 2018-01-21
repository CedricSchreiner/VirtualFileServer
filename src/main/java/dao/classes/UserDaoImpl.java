package dao.classes;

import builder.DaoObjectBuilder;
import builder.ModelObjectBuilder;
import dao.DatabaseConnection;
import dao.enums.ColNameUser;
import dao.interfaces.UserDao;
import models.interfaces.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static dao.constants.AdminDaoConstants.COL_ADMIN_ID;
import static dao.constants.AdminDaoConstants.TABLE_ADMIN;
import static dao.constants.DaoConstants.*;
import static dao.constants.UserDaoConstants.*;

public class UserDaoImpl implements UserDao {
//----------------------------------------------------------------------------------------------------------------------
// Prepared Statements
//----------------------------------------------------------------------------------------------------------------------

    /**
     * SELECT * FROM User LEFT OUTER JOIN admin WHERE email = ?
     */
    private static final String GC_GET_USER = "SELECT * FROM " + TABLE_USER + " LEFT OUTER JOIN " + TABLE_ADMIN +
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

//----------------------------------------------------------------------------------------------------------------------

    private DatabaseConnection gob_databaseConnection = DatabaseConnection.getInstance();

    /**
     * get a user from the database with a specific email
     *
     * @param iva_email get the user with this email
     * @return the found user
     */
    @Override
    public User getUser(String iva_email) {
        ResultSet rs;
        User aUser = ModelObjectBuilder.getUserModel();

        try (Connection lob_connection = this.gob_databaseConnection.getConnection();
             PreparedStatement lob_preparedStatement = lob_connection.prepareStatement(GC_GET_USER)) {

            lob_preparedStatement.setString(PARAMETER_1, iva_email);
            rs = lob_preparedStatement.executeQuery();

            while (rs.next()) {
                aUser.setEmail(rs.getString(COL_USER_EMAIL));
                aUser.setPassword(rs.getString(COL_USER_PASSWORD));
                aUser.setName(rs.getString(COL_USER_NAME));

                if (rs.getInt(COL_ADMIN_ID) > 0) {
                    aUser.setIsAdmin(true);
                } else {
                    aUser.setIsAdmin(false);
                }

                aUser.setUserId(rs.getInt(COL_USER_ID));
                aUser.setAdminId(rs.getInt(COL_ADMIN_ID));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return aUser;
    }

    /**
     * delete a user from the database
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
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return convertIntToBoolean(iva_rowCount);
    }

    /**
     * create a new user in the database
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
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return convertIntToBoolean(iva_rowCount);
    }

    /**
     * Update the e-mail of an user
     *
     * @param user update this user
     * @return false if an error occurred, otherwise true
     */
    public boolean updateEmail(User user) {
        return updateUser(user, ColNameUser.Email, user.getEmail());
    }

    /**
     * Update the password of an user
     *
     * @param user update this user
     * @return false if an error occurred, otherwise true
     */
    public boolean updatePassword(User user) {
        return updateUser(user, ColNameUser.Password, user.getPassword());
    }

    /**
     * Update the name of an user
     *
     * @param user update this user
     * @return false if an error occurred, otherwise true
     */
    public boolean updateName(User user) {
        return updateUser(user, ColNameUser.Name, user.getName());
    }

    /**
     * Update an attribute of an user
     * @param user the user to update
     * @param colNameUser attribute name
     * @param value the new value
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
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return convertIntToBoolean(iva_rowCount);
    }

    /**
     * Converts an integer to boolean
     * @param iva_integerBoolean the integer
     * @return true if int = 1 otherwise false
     */
    private static boolean convertIntToBoolean(int iva_integerBoolean) {
        return iva_integerBoolean == 1;
    }

    public static void main(String[] args) {
        UserDao userDao = DaoObjectBuilder.getUserDaoObject();
        User user = ModelObjectBuilder.getUserModel();
        user.setEmail("Bla");
        user.setPassword("Password");
        user.setName("Florian");

        boolean a = userDao.createUser(user);


        //boolean a = userDao.updatePassword(user);
        //boolean a = userDao.deleteUser(user);
        System.out.println(a);
    }
}
