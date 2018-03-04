package rest;

import builder.ServiceObjectBuilder;
import models.classes.*;
import services.classes.SharedDirectoryServiceImpl;
import services.interfaces.SharedDirectoryService;
import services.interfaces.UserService;
import utilities.Utils;

import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static rest.constants.InitializerConstants.GC_FILE_BASE_PATH;


public class Initializer extends HttpServlet {

    //TODO change the path at the end of the project
    public static String getUserBasePath() {
        return GC_FILE_BASE_PATH.replace("$", System.getProperty("user.name"));
    }

    /**
     * Add all file paths, from the files that are saved by the user, in the associated tree
     */
    public void init() {
        //---------------------------------Variables-------------------------------------------------
        FileTreeCollection lob_fileTree = FileTreeCollection.getInstance();
        //get all users from the database and read for every single one the file tree
        final UserService lob_userService = ServiceObjectBuilder.getUserServiceObject();
        List<User> lco_userList = lob_userService.getAllUser();
        File lob_rootDirectory = new File(Utils.getRootDirectory());
        File lob_sharedUserDirectory;
        File lob_publicDirectory;
        UserTree lob_userTree;
        SharedDirectory lob_dummyPublicDirectory;
        SharedDirectoryTree lob_sharedDirectoryTree;
        String lva_rootDirectory = Utils.getRootDirectory();
        String lva_userRootDirectory;
        String lva_userSharedDirectory;
        SharedDirectoryService lob_sharedDirectoryService = ServiceObjectBuilder.getSharedDirectoryServiceObject();
        //-------------------------------------------------------------------------------------------

        if (!lob_rootDirectory.exists() || !lob_rootDirectory.isDirectory()) {
            lob_rootDirectory.mkdir();
        }

        lob_publicDirectory = new File(lva_rootDirectory + "\\Public");

        if (!lob_publicDirectory.exists() || !lob_publicDirectory.isDirectory()) {
            lob_publicDirectory.mkdir();
        }

        lob_dummyPublicDirectory = new SharedDirectory();
        lob_dummyPublicDirectory.setId(0);

        try {
            lob_sharedDirectoryTree = new SharedDirectoryTree(lob_dummyPublicDirectory, lva_rootDirectory + "\\Public");
            lob_fileTree.addSharedDirectoryTree(lob_sharedDirectoryTree);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        for (User lob_user : lco_userList) {
            try {
                //get the directory of the user, build the tree and add it to the collection
                lva_userRootDirectory = lva_rootDirectory + lob_user.getName() + lob_user.getUserId();
                lob_userTree = new UserTree(lob_user, lva_userRootDirectory);
                lob_fileTree.addUserTreeToCollection(lob_userTree);
                lob_sharedUserDirectory = new File(lva_userRootDirectory + "_shared");

                if (!lob_sharedUserDirectory.exists() || !lob_sharedUserDirectory.isDirectory()) {
                    lob_sharedUserDirectory.mkdir();
                }

                for (SharedDirectory lob_sharedDirectory : lob_sharedDirectoryService.getSharedDirectoryOfUser(lob_user)) {
                    lva_userSharedDirectory = lva_userRootDirectory + "_shared\\" + lob_sharedDirectory.getId();
                    lob_sharedDirectoryTree = new SharedDirectoryTree(lob_sharedDirectory, lva_userSharedDirectory);
                    lob_fileTree.addSharedDirectoryTree(lob_sharedDirectoryTree);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
