package dao.classes;

import dao.interfaces.SharedDirectoryDao;
import models.classes.SharedDirectory;
import models.classes.User;
import models.exceptions.SharedDirectoryException;
import utilities.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static dao.constants.DaoConstants.GC_PARAMETER_1;
import static dao.constants.DaoConstants.GC_PARAMETER_2;
import static dao.constants.SharedDirectoryConstants.*;
import static models.constants.SharedDirectoryConstants.GC_ERR_SHARED_DIRECTORY_ALREADY_EXISTS;

public class SharedDirectoryDaoImpl implements SharedDirectoryDao {
    // Prepared Statements -------------------------------------------------------------------------------------------------
    private static final String GC_ADD_SHARED_DIRECTORY = "INSERT INTO " + GC_TABLE_SHARED_DIRECTORY + " (" +
            GC_COL_SHARED_D_OWNER + " , " + GC_COL_SHARED_D_GROUP_NAME + ") VALUES (?,?)";

    /**
     * INSERT INTO SharedDirectoryMember (groupName, member) VALUES (?,?)
     */
    private static final String GC_ADD_NEW_MEMBER_TO_SHARED_DIRECTORY = "INSERT INTO " + GC_TABLE_SHARED_DIRECTORY_MEMBER +
            " (" + GC_COL_SHARED_D_MEMBER_GROUP_ID + ", " + GC_COL_SHARED_D_MEMBER_MEMBER_ID + ") VALUES (?,?)";

    private static final String GC_DELETE_SHARED_DIRECTORY = "DELETE FROM " + GC_TABLE_SHARED_DIRECTORY + " WHERE " +
            GC_COL_SHARED_D_ID + " = ?";

    private static final String GC_REMOVE_MEMBER_FROM_SHARED_DIRECTORY = "DELETE FROM " +
            GC_TABLE_SHARED_DIRECTORY_MEMBER + " WHERE " + GC_COL_SHARED_D_MEMBER_MEMBER_ID + " = ? AND " +
            GC_COL_SHARED_D_MEMBER_GROUP_ID + " = ?";

    /**
     * SELECT * FROM SharedDirectory LEFT OUTER JOIN SharedDirectoryMember ON SharedDirectory.id = SharedDirectoryMember.groupId
     */
    private static final String GC_GET_ALL_SHARED_DIRECTORIES = "SELECT * FROM " + GC_TABLE_SHARED_DIRECTORY +
            " LEFT OUTER JOIN " + GC_TABLE_SHARED_DIRECTORY_MEMBER + " ON " + GC_TABLE_SHARED_DIRECTORY + "." +
            GC_COL_SHARED_D_ID + " = " + GC_TABLE_SHARED_DIRECTORY_MEMBER + "." + GC_COL_SHARED_D_MEMBER_GROUP_ID;

    /**
     * DELETE FROM SharedDirectoryMember WHERE groupId = ?
     */
    private static final String GC_REMOVE_MEMBER = "DELETE FROM " + GC_TABLE_SHARED_DIRECTORY_MEMBER + " WHERE " +
            GC_COL_SHARED_D_MEMBER_GROUP_ID + " = ?";

    private static final String GC_GET_S_DIR_BY_ID = "SELECT * FROM " + GC_TABLE_SHARED_DIRECTORY +
            " LEFT OUTER JOIN " + GC_TABLE_SHARED_DIRECTORY_MEMBER + " ON " + GC_TABLE_SHARED_DIRECTORY + "." +
            GC_COL_SHARED_D_ID + " = " + GC_TABLE_SHARED_DIRECTORY_MEMBER + "." + GC_COL_SHARED_D_MEMBER_GROUP_ID +
            " WHERE " + GC_TABLE_SHARED_DIRECTORY + "." + GC_COL_SHARED_D_ID + " = ?";
// ---------------------------------------------------------------------------------------------------------------------

    private final DatabaseConnection gob_databaseConnection = DatabaseConnection.getInstance();

    public boolean addNewSharedDirectory(SharedDirectory iob_sharedDirectory) {
        int lva_rowCount = 0;

        try (Connection lob_connection = this.gob_databaseConnection.getConnection();
             PreparedStatement lob_preparedStatement = lob_connection.prepareStatement(GC_ADD_SHARED_DIRECTORY)) {

            lob_preparedStatement.setInt(GC_PARAMETER_1, iob_sharedDirectory.getOwner().getUserId());
            lob_preparedStatement.setString(GC_PARAMETER_2, iob_sharedDirectory.getDirectoryName());

            lva_rowCount = lob_preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getMessage().contains("A UNIQUE constraint failed")) {
                throw new SharedDirectoryException(GC_ERR_SHARED_DIRECTORY_ALREADY_EXISTS);
            }

            ex.printStackTrace();
        }

        return Utils.convertIntToBoolean(lva_rowCount);
    }

    public boolean addNewMemberToSharedDirectory(SharedDirectory iob_sharedDirectory, User iob_user) {
        int lva_rowCount = 0;

        try (Connection lob_connection = this.gob_databaseConnection.getConnection();
             PreparedStatement lob_preparedStatement = lob_connection.prepareStatement(GC_ADD_NEW_MEMBER_TO_SHARED_DIRECTORY)) {

            lob_preparedStatement.setInt(GC_PARAMETER_1, iob_sharedDirectory.getId());
            lob_preparedStatement.setInt(GC_PARAMETER_2, iob_user.getUserId());

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

            lob_preparedStatement.setInt(GC_PARAMETER_1, iob_sharedDirectory.getId());

            lva_rowCount = lob_preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try (Connection lob_connection = this.gob_databaseConnection.getConnection();
             PreparedStatement lob_preparedStatement = lob_connection.prepareStatement(GC_DELETE_SHARED_DIRECTORY)) {

            lob_preparedStatement.setInt(GC_PARAMETER_1, iob_sharedDirectory.getId());

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

            lob_preparedStatement.setInt(GC_PARAMETER_1, iob_user.getUserId());
            lob_preparedStatement.setInt(GC_PARAMETER_2, iob_sharedDirectory.getId());

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
                lob_sharedDirectory = new SharedDirectory();
                lob_member = new User();
                lob_owner = new User();
                lva_sharedDirectoryExists = false;

                lva_sharedDirectoryID = lob_rs.getInt(GC_COL_SHARED_D_ID);
                for (SharedDirectory lob_tmp : lob_sharedDirectoryList) {
                    if (lob_tmp.getId() == lva_sharedDirectoryID) {
                        lob_member.setUserId(lob_rs.getInt(GC_COL_SHARED_D_MEMBER_MEMBER_ID));
                        lob_tmp.getMembers().add(lob_member);
                        lva_sharedDirectoryExists = true;
                    }
                }

                if (!lva_sharedDirectoryExists) {
                    lob_sharedDirectory.setId(lob_rs.getInt(GC_COL_SHARED_D_ID));
                    lob_owner.setUserId(lob_rs.getInt(GC_COL_SHARED_D_OWNER));
                    lob_sharedDirectory.setOwner(lob_owner);
                    lob_sharedDirectory.setDirectoryName(lob_rs.getString(GC_COL_SHARED_D_GROUP_NAME));
                    lob_member.setUserId(lob_rs.getInt(GC_COL_SHARED_D_MEMBER_MEMBER_ID));
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

    public SharedDirectory getSharedDirectoryById(int iva_id) {
        List<User> lob_memberList = new ArrayList<>();
        ResultSet lob_rs;
        SharedDirectory lob_sharedDirectory = null;
        User lob_owner;
        User lob_member;
        boolean lva_sharedDirectoryExists = false;
        int lva_memberId;

        try (Connection lob_connection = this.gob_databaseConnection.getConnection();
             PreparedStatement lob_preparedStatement = lob_connection.prepareStatement(GC_GET_S_DIR_BY_ID)) {

            lob_preparedStatement.setInt(GC_PARAMETER_1, iva_id);
            lob_rs = lob_preparedStatement.executeQuery();

            while (lob_rs.next()) {
                lob_member = new User();
                lob_owner = new User();

// TODO
// SELECT * FROM User as owner CROSS JOIN (SharedDirectory CROSS JOIN (SharedDirectoryMember CROSS JOIN User AS member
// ON SharedDirectoryMember.member = member.userId)
// ON SharedDirectory.id = SharedDirectoryMember.groupId) ON owner.userId = SharedDirectory.owner WHERE SharedDirectory.id = ?

                if (!lva_sharedDirectoryExists) {
                    lob_sharedDirectory = new SharedDirectory();
                    lob_sharedDirectory.setId(lob_rs.getInt(GC_COL_SHARED_D_ID));
                    lob_owner.setUserId(lob_rs.getInt(GC_COL_SHARED_D_OWNER));
                    lob_sharedDirectory.setOwner(lob_owner);
                    lob_sharedDirectory.setDirectoryName(lob_rs.getString(GC_COL_SHARED_D_GROUP_NAME));

                    lva_memberId = lob_rs.getInt(GC_COL_SHARED_D_MEMBER_MEMBER_ID);

                    if (lva_memberId != 0) {
                        lob_member.setUserId(lva_memberId);
                        lob_memberList.add(lob_member);
                    }

                    lob_sharedDirectory.setMembers(lob_memberList);
                    lva_sharedDirectoryExists = true;
                } else {
                    lva_memberId = lob_rs.getInt(GC_COL_SHARED_D_MEMBER_MEMBER_ID);

                    if (lva_memberId != 0) {
                        lob_member.setUserId(lva_memberId);
                        lob_sharedDirectory.getMembers().add(lob_member);
                    }
                }
            }
        } catch (SQLException ex) {
            if (ex.getMessage().contains("A UNIQUE constraint failed")) {
                throw new SharedDirectoryException("GC_ERR_S_DIR_MEMBER_ALREADY_EXISTS");
            }

            ex.printStackTrace();
        }

        return lob_sharedDirectory;
    }
}
