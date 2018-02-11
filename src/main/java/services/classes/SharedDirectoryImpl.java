package services.classes;

import builder.DaoObjectBuilder;
import dao.interfaces.SharedDirectoryDao;
import dao.interfaces.UserDao;
import models.classes.SharedDirectory;
import models.classes.User;
import models.exceptions.UserEmptyException;
import services.exceptions.SharedDirectoryIsEmptyException;
import services.interfaces.SharedDirectoryService;

import java.util.ArrayList;
import java.util.List;

import static services.constants.SharedDirectoryServiceConstants.GC_ERR_MSG_SHARED_DIRECTORY_IS_EMPTY;
import static services.constants.SharedDirectoryServiceConstants.GC_ERR_MSG_USER_NOT_EXIST;

public class SharedDirectoryImpl implements SharedDirectoryService {
    private UserDao gob_userDao = DaoObjectBuilder.getUserDaoObject();
    private SharedDirectoryDao gob_sharedDirectory = DaoObjectBuilder.getSharedDirectoryObject();

    public List<SharedDirectory> getSharedDirectoryService(User iob_user) {
        List<SharedDirectory> ili_sharedDir = gob_sharedDirectory.getAllSharedDirectories();
        List<SharedDirectory> rli_associatedShareDir = new ArrayList<>();


        if (ili_sharedDir.isEmpty()) {
            throw new SharedDirectoryIsEmptyException(GC_ERR_MSG_SHARED_DIRECTORY_IS_EMPTY);
        } else if (gob_userDao.getUser(iob_user.getEmail()) == null) {
            throw new UserEmptyException(GC_ERR_MSG_USER_NOT_EXIST);
        } else {
            for (SharedDirectory lob_temp : ili_sharedDir) {
                if (lob_temp.getMembers().contains(iob_user)) {
                    rli_associatedShareDir.add(lob_temp);
                }
            }
        }
        return rli_associatedShareDir;
    }
}
