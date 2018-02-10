package dao.classes;

import builder.ModelObjectBuilder;
import dao.interfaces.SharedDirectoryDao;
import models.interfaces.SharedDirectory;
import models.interfaces.User;
import utilities.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * SELECT * FROM SharedDirectory LEFT OUTER JOIN SharedDirectoryMember ON SharedDirectory.id = SharedDirectoryMember.groupId
     */
    private static final String GC_GET_ALL_SHARED_DIRECTORIES = "SELECT * FROM " + TABLE_SHARED_DIRECTORY +
            " LEFT OUTER JOIN " + TABLE_SHARED_DIRECTORY_MEMBER + " ON " + TABLE_SHARED_DIRECTORY + "." +
            COL_SHARED_D_ID + " = " + TABLE_SHARED_DIRECTORY_MEMBER + "." + COL_SHARED_D_MEMBER_GROUP_ID;

    /**
     * DELETE FROM SharedDirectoryMember WHERE groupId = ?
     */
    private static final String GC_REMOVE_MEMBER = "DELETE FROM " + TABLE_SHARED_DIRECTORY_MEMBER + " WHERE " +
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
             PreparedStatement lob_preparedStatement = lob_connection.prepareStatement(GC_REMOVE_MEMBER)) {

            lob_preparedStatement.setInt(PARAMETER_1, iob_sharedDirectory.getId());

            lva_rowCount = lob_preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

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

    @Override
    public List<SharedDirectory> getAllSharedDirectories() {
        List<SharedDirectory> lob_sharedDirectoryList = new ArrayList<>();
        List<User> lob_memberList = new ArrayList<>();

        ResultSet lob_rs;
        SharedDirectory lob_sharedDirectory;
        User lob_owner;
        User lob_member;
        int lva_sharedDirectoryID;
        boolean lva_sharedDirectoryExists;

        try (Connection lob_connection = this.gob_databaseConnection.getConnection();
             PreparedStatement lob_preparedStatement = lob_connection.prepareStatement(GC_GET_ALL_SHARED_DIRECTORIES)) {

            lob_rs = lob_preparedStatement.executeQuery();


            while (lob_rs.next()) {
                lob_sharedDirectory = ModelObjectBuilder.getSharedDirectoryObject();
                lob_member = ModelObjectBuilder.getUserModel();
                lob_owner = ModelObjectBuilder.getUserModel();
                lva_sharedDirectoryExists = false;

                lva_sharedDirectoryID = lob_rs.getInt(COL_SHARED_D_ID);
                for (SharedDirectory lob_tmp : lob_sharedDirectoryList) {
                    if (lob_tmp.getId() == lva_sharedDirectoryID) {
                        lob_member.setUserId(lob_rs.getInt(COL_SHARED_D_MEMBER_MEMBER));
                        lob_tmp.getMembers().add(lob_member);
                        lva_sharedDirectoryExists = true;
                    }
                }

                if (!lva_sharedDirectoryExists) {
                    lob_sharedDirectory.setId(lob_rs.getInt(COL_SHARED_D_ID));
                    lob_owner.setUserId(lob_rs.getInt(COL_SHARED_D_OWNER));
                    lob_sharedDirectory.setOwner(lob_owner);
                    lob_sharedDirectory.setDirectoryName(lob_rs.getString(COL_SHARED_D_GROUP_NAME));
                    lob_member.setUserId(lob_rs.getInt(COL_SHARED_D_MEMBER_MEMBER));
                    lob_memberList.add(lob_member);
                    lob_sharedDirectory.setMembers(lob_memberList);
                    lob_sharedDirectoryList.add(lob_sharedDirectory);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lob_sharedDirectoryList;
    }
}
