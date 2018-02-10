package dao.classes;

import dao.interfaces.AdminDao;
import models.classes.User;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utilities.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static dao.constants.AdminDaoConstants.COL_ADMIN_USER_ID;
import static dao.constants.AdminDaoConstants.TABLE_ADMIN;
import static dao.constants.DaoConstants.PARAMETER_1;

public class AdminDaoImpl implements AdminDao {
// Prepared Statements -------------------------------------------------------------------------------------------------
    /**
     * INSERT INTO Admin (userId) VALUES (?)
     */
    private static final String GC_ADD_ADMIN = "INSERT INTO " + TABLE_ADMIN +
            " (" + COL_ADMIN_USER_ID + ") VALUES (?)";

// ---------------------------------------------------------------------------------------------------------------------

    private final DatabaseConnection gob_databaseConnection = DatabaseConnection.getInstance();

    /**
     * Adds an user to the admin data table
     *
     * @param iva_user the user
     * @return number of changed rows
     */
    @Override
    public boolean addAdmin(User iva_user) {
        int lva_rowCount = 0;

        try (Connection lob_connection = this.gob_databaseConnection.getConnection();
             PreparedStatement lob_preparedStatement = lob_connection.prepareStatement(GC_ADD_ADMIN)) {

            lob_preparedStatement.setInt(PARAMETER_1, iva_user.getUserId());

            lva_rowCount = lob_preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return Utils.convertIntToBoolean(lva_rowCount);
    }

    /**
     * Not used
     *
     * @param iva_user the user
     * @return null
     */
    @Override
    public boolean removeAdmin(User iva_user) {
        throw new NotImplementedException();
    }
}
