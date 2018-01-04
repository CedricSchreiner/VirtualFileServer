package dao;

import interfaces.UserDaoInterface;
import models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static constants.UserDaoConstants.*;

public class UserDao implements UserDaoInterface {

    private DatabaseConnection gob_databaseConnection = DatabaseConnection.getInstance();

    /**
     * get a user from the database with a specific email
     *
     * @param iva_email get the user with this email
     * @return the found user
     */
    @Override
    public User getUser(String iva_email) {
        //------------Variables------------
        ResultSet lob_resultSet;
        PreparedStatement lob_preparedStatement;
        User rob_user = null;
        //---------------------------------
        try (Connection lob_connection = this.gob_databaseConnection.getConnection()){
            lob_preparedStatement = lob_connection.prepareStatement(GC_GET_USER);
            lob_preparedStatement.setString(0, iva_email);
            lob_resultSet = lob_preparedStatement.executeQuery();

            while (lob_resultSet.next()) {
                rob_user = new User(
                        lob_resultSet.getString("email"),
                        lob_resultSet.getString("password"),
                        convertIntToBoolean(lob_resultSet.getInt("isAdmin")),
                        lob_resultSet.getInt("userId"),
                        lob_resultSet.getInt("adminId")
                );
            }

        } catch (SQLException e) {
            //TODO logger
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
        return false;
    }

    /**
     * update an user in the database
     *
     * @param user update this user
     * @return false if an error occurred, otherwise true
     */
    @Override
    public boolean updateUser(User user) {
        return false;
    }

    private static boolean convertIntToBoolean(int iva_integerBoolean) {
        switch (iva_integerBoolean) {
            case 1: return true;
            default: return false;
        }
    }
}
