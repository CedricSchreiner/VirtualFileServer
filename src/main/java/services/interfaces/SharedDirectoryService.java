package services.interfaces;

import models.classes.SharedDirectory;
import models.classes.User;

import java.util.List;

public interface SharedDirectoryService {
    List<SharedDirectory> getSharedDirectory(User iob_user);
    boolean addNewSharedDirectory(SharedDirectory iob_sharedDirectory);
    boolean addNewMemberToSharedDirectory(SharedDirectory iob_sharedDirectory, User iob_member);
    boolean deleteSharedDirectory(SharedDirectory iob_sharedDirectory);
    boolean removeMemberFromSharedDirectory(SharedDirectory iob_sharedDirectory, User iob_member);
    SharedDirectory getSharedDirectoryById(int iva_id);
    List<SharedDirectory> getSharedDirectoriesOfUser(User iob_user);
}
