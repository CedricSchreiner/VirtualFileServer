package services.classes;

import builder.DaoObjectBuilder;
import builder.ServiceObjectBuilder;
import dao.interfaces.SharedDirectoryDao;
import models.classes.SharedDirectory;
import models.classes.User;
import models.exceptions.SharedDirectoryException;
import services.interfaces.SharedDirectoryService;
import services.interfaces.UserService;

import java.util.ArrayList;
import java.util.List;

import static models.constants.SharedDirectoryConstants.GC_ERR_S_DIR_DOES_NOT_EXISTS;
import static models.constants.SharedDirectoryConstants.GC_ERR_S_DIR_MEMBER_ALREADY_EXISTS;
import static models.constants.SharedDirectoryConstants.GC_ERR_S_DIR_MEMBER_DOES_NOT_EXISTS;
import static models.constants.UserConstants.GC_EMPTY_USER;

public class SharedDirectoryServiceImpl implements SharedDirectoryService {
    private SharedDirectoryDao gob_sharedDirectoryDao = DaoObjectBuilder.getSharedDirectoryObject();
    private UserService gob_userService = ServiceObjectBuilder.getUserServiceObject();

    public List<SharedDirectory> getSharedDirectoryOfUser(User iob_user) {
        List<SharedDirectory> lli_sharedDirectories;
        List<SharedDirectory> lli_sharedDirectoriesOfOUser = new ArrayList<>();

        lli_sharedDirectories = gob_sharedDirectoryDao.getAllSharedDirectories();

        for (SharedDirectory lob_sharedDirectory : lli_sharedDirectories) {
            if (lob_sharedDirectory.getOwner().getUserId() == iob_user.getUserId()) {
                lli_sharedDirectoriesOfOUser.add(lob_sharedDirectory);
            }
        }

        return lli_sharedDirectoriesOfOUser;
    }


    /**
     * Adds a new shared directory and all its member
     *
     * @param iob_sharedDirectory the shared directory
     * @return false if an exception occurred otherwise true
     */
    public boolean addNewSharedDirectory(SharedDirectory iob_sharedDirectory) {
        boolean lva_hasSharedDirectoryAdded;
        boolean lva_hasMemberAdded = false;
        User lob_user;

        // set all attributes of the owner instead of the id
        lob_user = gob_userService.getUserByEmail(iob_sharedDirectory.getOwner().getEmail());
        iob_sharedDirectory.setOwner(lob_user);

        // check if the user exists
        if (lob_user == null) {
            throw new SharedDirectoryException(GC_EMPTY_USER);
        }

        // add the shared directory
        lva_hasSharedDirectoryAdded = gob_sharedDirectoryDao.addNewSharedDirectory(iob_sharedDirectory);

        // set the id of the shared directory
        for (SharedDirectory lob_sharedDirectory : getSharedDirectoryOfUser(lob_user)) {
            if (areTheDirectoriesEqual(lob_sharedDirectory, iob_sharedDirectory)) {
                iob_sharedDirectory.setId(lob_sharedDirectory.getId());
            }
        }

        // add all members to the new shared directory
        for (User lob_member : iob_sharedDirectory.getMembers()) {
            lob_member = gob_userService.getUserByEmail(lob_member.getEmail());
            lva_hasMemberAdded = addNewMemberToSharedDirectory(iob_sharedDirectory, lob_member);
        }

        return lva_hasSharedDirectoryAdded && lva_hasMemberAdded;
    }

    /**
     * Adds a member to a shared directory
     *
     * @param iob_sharedDirectory the shared directory
     * @param iob_member          the new member
     * @return false if an exception occurred otherwise true
     */
    public boolean addNewMemberToSharedDirectory(SharedDirectory iob_sharedDirectory, User iob_member) {
        SharedDirectory lob_sharedDirectory = gob_sharedDirectoryDao
                .getSharedDirectoryById(iob_sharedDirectory.getId());

        // check if the shared directory exists
        if (lob_sharedDirectory == null) {
            throw new SharedDirectoryException(GC_ERR_S_DIR_DOES_NOT_EXISTS);
        }

        iob_member = gob_userService.getUserByEmail(iob_member.getEmail());

        return gob_sharedDirectoryDao.addNewMemberToSharedDirectory(lob_sharedDirectory, iob_member);
    }

    public boolean deleteSharedDirectory(SharedDirectory iob_sharedDirectory) {
        SharedDirectory lob_sharedDirectory = gob_sharedDirectoryDao
                .getSharedDirectoryById(iob_sharedDirectory.getId());

        if (lob_sharedDirectory == null) {
            throw new SharedDirectoryException(GC_ERR_S_DIR_DOES_NOT_EXISTS);
        }

        return gob_sharedDirectoryDao.deleteSharedDirectory(iob_sharedDirectory);
    }

    public boolean removeMemberFromSharedDirectory(SharedDirectory iob_sharedDirectory, User iob_member) {
        SharedDirectory lob_sharedDirectory = gob_sharedDirectoryDao
                .getSharedDirectoryById(iob_sharedDirectory.getId());

        if (lob_sharedDirectory == null) {
            throw new SharedDirectoryException(GC_ERR_S_DIR_DOES_NOT_EXISTS);
        }

        iob_member = gob_userService.getUserByEmail(iob_member.getEmail());

        for (User lob_user : lob_sharedDirectory.getMembers()) {
            if (lob_user.getUserId() == iob_member.getUserId()) {
                return gob_sharedDirectoryDao.removeMemberFromSharedDirectory(iob_sharedDirectory, iob_member);
            }
        }

        throw new SharedDirectoryException(GC_ERR_S_DIR_MEMBER_DOES_NOT_EXISTS);
    }

    public SharedDirectory getSharedDirectoryById(int iva_id) {
        return gob_sharedDirectoryDao.getSharedDirectoryById(iva_id);
    }

    private boolean areTheDirectoriesEqual(SharedDirectory iob_dir1, SharedDirectory iob_dir2) {
        return (iob_dir1.getOwner().getUserId() == iob_dir2.getOwner().getUserId() &&
                iob_dir1.getDirectoryName().equals(iob_dir2.getDirectoryName()));
    }
}
