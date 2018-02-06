package dao.classes;

import builder.DaoObjectBuilder;
import builder.ModelObjectBuilder;
import dao.interfaces.SharedDirectoryDao;
import models.interfaces.SharedDirectory;
import models.interfaces.User;
import utilities.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import static dao.constants.DaoConstants.PARAMETER_1;
import static dao.constants.DaoConstants.PARAMETER_2;
import static dao.constants.SharedDirectoryConstants.*;

public class SharedDirectoryDaoImpl implements SharedDirectoryDao {
// Prepared Statements -------------------------------------------------------------------------------------------------
    private static final String GC_ADD_SHARED_DIRECTORY = "INSERT INTO " + TABLE_SHARED_DIRECTORY + " (" +
            COL_SHARED_D_OWNER + " , " + COL_SHARED_D_GROUP_NAME + ") VALUES (?,?)";

    /**
     * INSERT INTO SharedDirectoryMember (groupName, member) VALUES (?,?)
     */
    private static final String GC_ADD_NEW_MEMBER_TO_SHARED_DIRECTORY = "INSERT INTO " + TABLE_SHARED_DIRECTORY_MEMBER +
            " (" + COL_SHARED_D_MEMBER_GROUP_ID + ", " + COL_SHARED_D_MEMBER_MEMBER + ") VALUES (?,?)";

    private static final String GC_DELETE_SHARED_DIRECTORY = "DELETE FROM " + TABLE_SHARED_DIRECTORY + " WHERE " +
            COL_SHARED_D_ID + " = ?";

    private static final String GC_REMOVE_MEMBER_FROM_SHARED_DIRECTORY = "DELETE FROM " +
            TABLE_SHARED_DIRECTORY_MEMBER + " WHERE " + COL_SHARED_D_MEMBER_MEMBER + " = ? AND " +
            COL_SHARED_D_MEMBER_GROUP_ID + " = ?";
// ---------------------------------------------------------------------------------------------------------------------

    private final DatabaseConnection gob_databaseConnection = DatabaseConnection.getInstance();

    public boolean addNewSharedDirectory(SharedDirectory iob_sharedDirectory) {
        int lva_rowCount = 0;

        try (Connection lob_connection = this.gob_databaseConnection.getConnection();
             PreparedStatement lob_preparedStatement = lob_connection.prepareStatement(GC_ADD_SHARED_DIRECTORY)) {

            lob_preparedStatement.setInt(PARAMETER_1, iob_sharedDirectory.getOwner().getUserId());
            lob_preparedStatement.setString(PARAMETER_2, iob_sharedDirectory.getDirectoryName());

            lva_rowCount = lob_preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return Utils.convertIntToBoolean(lva_rowCount);
    }

    public boolean addNewMemberToSharedDirectory(SharedDirectory iob_sharedDirectory, User iob_user) {
        int lva_rowCount = 0;

        try (Connection lob_connection = this.gob_databaseConnection.getConnection();
             PreparedStatement lob_preparedStatement = lob_connection.prepareStatement(GC_ADD_NEW_MEMBER_TO_SHARED_DIRECTORY)) {

            lob_preparedStatement.setInt(PARAMETER_1, iob_sharedDirectory.getId());
            lob_preparedStatement.setInt(PARAMETER_2, iob_user.getUserId());

            lva_rowCount = lob_preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return Utils.convertIntToBoolean(lva_rowCount);
    }

    public boolean deleteSharedDirectory(SharedDirectory iob_sharedDirectory) {
        int lva_rowCount = 0;

        try (Connection lob_connection = this.gob_databaseConnection.getConnection();
             PreparedStatement lob_preparedStatement = lob_connection.prepareStatement(GC_DELETE_SHARED_DIRECTORY)) {

            lob_preparedStatement.setInt(PARAMETER_1, iob_sharedDirectory.getId());

            lva_rowCount = lob_preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return Utils.convertIntToBoolean(lva_rowCount);
    }

    @Override
    public boolean removeMemberFromSharedDirectory(SharedDirectory iob_sharedDirectory, User iob_user) {
        int lva_rowCount = 0;

        try (Connection lob_connection = this.gob_databaseConnection.getConnection();
             PreparedStatement lob_preparedStatement = lob_connection.prepareStatement(GC_REMOVE_MEMBER_FROM_SHARED_DIRECTORY)) {

            lob_preparedStatement.setInt(PARAMETER_1, iob_user.getUserId());
            lob_preparedStatement.setInt(PARAMETER_2, iob_sharedDirectory.getId());

            lva_rowCount = lob_preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return Utils.convertIntToBoolean(lva_rowCount);
    }

    public static void main(String[] args) {
        SharedDirectoryDao s = DaoObjectBuilder.getSharedDirectoryObject();

        SharedDirectory sh = ModelObjectBuilder.getSharedDirectoryObject();
        User owner = ModelObjectBuilder.getUserModel();
        owner.setUserId(3);
        User member = ModelObjectBuilder.getUserModel();
        member.setUserId(5);
        ArrayList<User> userList = new ArrayList<>();
        userList.add(member);
        sh.setDirectoryName("testOrdner");
        sh.setOwner(owner);
        sh.setId(1);
        sh.setMembers(userList);

        s.removeMemberFromSharedDirectory(sh, member);

    }
}
