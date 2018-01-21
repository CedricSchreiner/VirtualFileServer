package dao.classes;

import builder.ModelObjectBuilder;
import dao.DatabaseConnection;
import dao.enums.ColNameUser;
import dao.interfaces.UserDaoInterface;
import models.interfaces.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static dao.constants.AdminDaoConstants.TABLE_ADMIN;
import static dao.constants.DaoConstants.*;
import static dao.constants.UserDaoConstants.*;

public class UserDaoImpl implements UserDaoInterface {
//----------------------------------------------------------------------------------------------------------------------
// Prepared Statements
//----------------------------------------------------------------------------------------------------------------------

    /**
     * SELECT * FROM users NATURAL JOIN admin WHERE email = ?
     */
    private static final String GC_GET_USER = "SELECT * FROM " + TABLE_USER + " NATURAL JOIN " + TABLE_ADMIN +
            " WHERE " + COL_USER_EMAIL + " = ?";

    /**
     * DELETE FROM users WHERE userId = ?
     */
    private static final String GC_DELETE_USER = "DELETE FROM " + TABLE_USER + " WHERE " + COL_USER_ID + " = ?";

    /**
     * INSERT INTO users (email, password, name) VALUES (?, ?, ?)
     */
    private static final String GC_ADD_USER = "INSERT INTO " + TABLE_USER + " (" + COL_USER_EMAIL + ", " +
            COL_USER_PASSWORD + ", " + COL_USER_NAME + ") VALUES (?, ?, ?)";

    /**
     * UPDATE usersSET $ = ? WHERE userId = ?
     */
    private static final String GC_UPDATE_USER = "UPDATE " + TABLE_USER + "SET $ = ? WHERE " + COL_USER_ID + " = ?";

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

        try (Connection lob_connection = this.gob_databaseConnection.getConnection();
             PreparedStatement lob_preparedStatement = lob_connection.prepareStatement(GC_GET_USER)) {

            lob_preparedStatement.setString(PARAMETER_1, iva_email);
            rs = lob_preparedStatement.executeQuery();

            while(rs.next()) {
                User aUser = ModelObjectBuilder.getUserModel();

                aUser.setEmail(COL_USER_EMAIL);
                aUser.setPassword(COL_USER_PASSWORD);
                aUser.setName(COL_USER_NAME);
                //aUser.setIsAdmin(COL_ADMIN_);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * delete a user from the database
     *
     * @param user delete that specific user
     * @return false if an error occurred, otherwise true
     */
    @Override
    public boolean deleteUser(User user) {
        try (Connection lob_connection = this.gob_databaseConnection.getConnection();
             PreparedStatement lob_preparedStatement = lob_connection.prepareStatement(GC_DELETE_USER)){

            lob_preparedStatement.setInt(PARAMETER_1, user.getUserId());
            lob_preparedStatement.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * create a new user in the database
     *
     * @param user create this user
     * @return false if an error occurred, otherwise true
     */
    @Override
    public boolean createUser(User user) {
        try (Connection lob_connection = this.gob_databaseConnection.getConnection();
             PreparedStatement lob_preparedStatement = lob_connection.prepareStatement(GC_ADD_USER)) {

            lob_preparedStatement.setString(PARAMETER_1, user.getEmail());
            lob_preparedStatement.setString(PARAMETER_2, user.getPassword());
            lob_preparedStatement.setString(PARAMETER_3, user.getName());

            lob_preparedStatement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateEmail(User user) {
        return updateUser(user, ColNameUser.Email, user.getEmail());
    }

    public boolean updatePassword(User user) {
        return updateUser(user, ColNameUser.Password, user.getPassword());
    }

    public boolean updateName(User user) {
        return updateUser(user, ColNameUser.Name, user.getName());
    }

    /**
     * update an user in the database
     *
     * @param user update this user
     * @return false if an error occurred, otherwise true
     */
    private boolean updateUser(User user, ColNameUser colNameUser, String value) {
        String lob_preparedStatementString = GC_UPDATE_USER.replace("$", colNameUser.getColName());

        try (Connection lob_connection = this.gob_databaseConnection.getConnection();
             PreparedStatement lob_preparedStatement = lob_connection.prepareStatement(lob_preparedStatementString)) {

            lob_preparedStatement.setString(PARAMETER_1, value);
            lob_preparedStatement.setInt(PARAMETER_2, user.getUserId());

            lob_preparedStatement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static boolean convertIntToBoolean(int iva_integerBoolean) {
        switch (iva_integerBoolean) {
            case 1:
                return true;
            default:
                return false;
        }
    }


    public static void main(String[] args) {
        System.out.println(GC_ADD_USER);
        System.out.println(GC_GET_USER);
        System.out.println(GC_DELETE_USER);
        System.out.println(GC_UPDATE_USER);
    }

}
