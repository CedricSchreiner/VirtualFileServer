package dao.interfaces;

import models.interfaces.SharedDirectory;
import models.interfaces.User;

import java.util.List;

public interface SharedDirectoryDao {
    boolean addNewSharedDirectory(SharedDirectory iob_sharedDirectory);
    boolean addNewMemberToSharedDirectory(SharedDirectory iob_sharedDirectory, User iob_user);
    boolean deleteSharedDirectory(SharedDirectory iob_sharedDirectory);
    boolean removeMemberFromSharedDirectory(SharedDirectory iob_sharedDirectory, User iob_user);
    List<SharedDirectory> getAllSharedDirectories();
}