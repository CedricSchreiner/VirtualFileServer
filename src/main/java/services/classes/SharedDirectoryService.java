package services.classes;

import builder.DaoObjectBuilder;
import dao.interfaces.SharedDirectoryDao;
import dao.interfaces.UserDao;
import models.interfaces.SharedDirectory;
import models.interfaces.User;
import services.exceptions.SharedDirectoryIsEmptyException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SharedDirectoryService {

    private static final String err_msg_sharedDirectory_list_isEmpty = "Error: list is empty.";
    private UserDao gob_userDao = DaoObjectBuilder.getUserDaoObject();
    private SharedDirectoryDao gob_sharedDirectory = DaoObjectBuilder.getSharedDirectoryObject();

    private List<SharedDirectory> getSharedDirectoryService(User iob_user){
        List<SharedDirectory> ili_sharedDir = gob_sharedDirectory.getAllSharedDirectories();
        List<SharedDirectory> rli_associatedShareDir = new ArrayList<>();

        if(ili_sharedDir.isEmpty()){
            throw new SharedDirectoryIsEmptyException(err_msg_sharedDirectory_list_isEmpty);
        }else{
            int iva_sharedDirectoryIndex = ili_sharedDir.size();
            while(iva_sharedDirectoryIndex != 0){
                if(ili_sharedDir.get(iva_sharedDirectoryIndex).getMembers().contains(iob_user)){
                    rli_associatedShareDir.add(ili_sharedDir.get(iva_sharedDirectoryIndex));
                    iva_sharedDirectoryIndex--;
                }else {
                    iva_sharedDirectoryIndex--;
                }
            }
        }
        return rli_associatedShareDir;
    }
}
