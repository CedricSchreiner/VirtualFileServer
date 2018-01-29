package dao.classes;

import dao.enums.ColNameAdmin;
import dao.enums.TableName;
import dao.interfaces.AdminDao;
import models.interfaces.User;
import utilities.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static dao.constants.DaoConstants.PARAMETER_1;

public class AdminDaoImpl implements AdminDao {
    private static final String GC_ADD_ADMIN = "INSERT INTO " + TableName.Admin.getTableName() +
            " (" + ColNameAdmin.UserId + ") VALUES (?)";

    private final DatabaseConnection gob_databaseConnection = DatabaseConnection.getInstance();

    @Override
    public boolean addAdmin(User iva_user) {
        int lva_rowCount = 0;

        try (Connection lob_connection = this.gob_databaseConnection.getConnection();
             PreparedStatement lob_preparedStatement = lob_connection.prepareStatement(GC_ADD_ADMIN)) {

            lob_preparedStatement.setInt(PARAMETER_1, iva_user.getUserId());

            lva_rowCount = lob_preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Utils.convertIntToBoolean(lva_rowCount);
    }

    @Override
    public boolean removeAdmin(User iva_user) {
        return false;
    }
}
