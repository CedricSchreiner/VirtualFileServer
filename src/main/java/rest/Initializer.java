package rest;

import builder.ServiceObjectBuilder;
import models.classes.*;
import services.interfaces.SharedDirectoryService;
import services.interfaces.UserService;
import utilities.Utils;

import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static rest.constants.InitializerConstants.GC_FILE_BASE_PATH;


public class Initializer extends HttpServlet {
    private static final String gva_rootDirectory = Utils.getRootDirectory();

//    //TODO change the path at the end of the project
//    public static String getUserBasePath() {
//        return GC_FILE_BASE_PATH.replace("$", System.getProperty("user.name"));
//    }

    /**
     * Add all file paths, from the files that are saved by the user, in the associated tree
     */
    public void init() {
        //---------------------------------Variables-------------------------------------------------
        FileTreeCollection lob_fileTree = FileTreeCollection.getInstance();
        final UserService lob_userService = ServiceObjectBuilder.getUserServiceObject();
        List<User> lco_userList = lob_userService.getAllUser();
        File lob_rootDirectory = new File(Utils.getRootDirectory());
        File lob_publicDirectory;
        SharedDirectory lob_dummyPublicDirectory;
        SharedDirectoryTree lob_sharedDirectoryTree;
        //-------------------------------------------------------------------------------------------

        if (!lob_rootDirectory.exists() || !lob_rootDirectory.isDirectory()) {
            lob_rootDirectory.mkdir();
        }

        lob_publicDirectory = new File(gva_rootDirectory + "\\Public");

        if (!lob_publicDirectory.exists() || !lob_publicDirectory.isDirectory()) {
            lob_publicDirectory.mkdir();
        }

        lob_dummyPublicDirectory = new SharedDirectory();
        lob_dummyPublicDirectory.setId(0);

        try {
            lob_sharedDirectoryTree = new SharedDirectoryTree(lob_dummyPublicDirectory, gva_rootDirectory + "\\Public");
            lob_fileTree.addSharedDirectoryTree(lob_sharedDirectoryTree);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        for (User lob_user : lco_userList) {
            try {
                initUserTree(lob_user);
                initUsersSharedDirectories(lob_user);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void initUserTree(User iob_user) throws IOException{
        String lva_userRootDirectory;
        UserTree lob_userTree;
        FileTreeCollection lob_collection = FileTreeCollection.getInstance();

        lva_userRootDirectory = gva_rootDirectory + iob_user.getName() + iob_user.getUserId();
        lob_userTree =  new UserTree(iob_user, lva_userRootDirectory);
        lob_collection.addUserTreeToCollection(lob_userTree);
    }

    public static void initUsersSharedDirectories(User iob_user) throws IOException{
        FileTreeCollection lob_collection = FileTreeCollection.getInstance();
        SharedDirectoryService lob_sharedDirectoryService = ServiceObjectBuilder.getSharedDirectoryServiceObject();
        String lva_userSharedDirectory;
        SharedDirectoryTree lob_sharedDirectoryTree;

        File lob_userSharedDirectory =  new File(gva_rootDirectory + iob_user.getName() + iob_user.getUserId() + "_shared");

        if (!lob_userSharedDirectory.exists() || !lob_userSharedDirectory.isDirectory()) {
            lob_userSharedDirectory.mkdir();
        }

        for (SharedDirectory lob_sharedDirectory : lob_sharedDirectoryService.getSharedDirectory(iob_user)) {
            lva_userSharedDirectory = lob_userSharedDirectory + "\\" + lob_sharedDirectory.getId();
            lob_sharedDirectoryTree = new SharedDirectoryTree(lob_sharedDirectory, lva_userSharedDirectory);
            lob_collection.addSharedDirectoryTree(lob_sharedDirectoryTree);
        }
    }
}
