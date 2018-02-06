package services.classes;

import builder.DaoObjectBuilder;
import dao.interfaces.SharedDirectoryDao;
import dao.interfaces.UserDao;
import models.exceptions.UserEmptyException;
import models.interfaces.SharedDirectory;
import models.interfaces.User;
import services.exceptions.SharedDirectoryIsEmptyException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SharedDirectoryService {

    private static final String err_msg_sharedDirectory_list_isEmpty = "Error: list is empty.";
    private static final String err_msg_User_not_exist = "Error: User does not exist";
    private UserDao gob_userDao = DaoObjectBuilder.getUserDaoObject();
    private SharedDirectoryDao gob_sharedDirectory = DaoObjectBuilder.getSharedDirectoryObject();

    private List<SharedDirectory> getSharedDirectoryService(User iob_user){
        List<SharedDirectory> ili_sharedDir = gob_sharedDirectory.getAllSharedDirectories();
        List<SharedDirectory> rli_associatedShareDir = new ArrayList<>();


        if(ili_sharedDir.isEmpty()){
            throw new SharedDirectoryIsEmptyException(err_msg_sharedDirectory_list_isEmpty);
        }else if(gob_userDao.getUser(iob_user.getEmail()) == null) {
            throw new UserEmptyException(err_msg_User_not_exist);
        }else{
            for (SharedDirectory lob_temp : ili_sharedDir){
                if(lob_temp.getMembers().contains(iob_user)){
                    rli_associatedShareDir.add(lob_temp);
                }
            }
        }
        return rli_associatedShareDir;
    }
}
