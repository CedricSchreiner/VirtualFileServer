package rest;

import builder.ServiceObjectBuilder;
import models.classes.FileTreeCollection;
import models.classes.UserTree;
import models.interfaces.User;
import services.interfaces.UserService;
import utilities.Utils;

import static rest.constants.InitializerConstants.*;

import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;
import java.util.List;


public class Initializer extends HttpServlet{

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
        UserTree lob_userTree;
        //-------------------------------------------------------------------------------------------

        if (!lob_rootDirectory.exists() || !lob_rootDirectory.isDirectory()) {
            lob_rootDirectory.mkdir();
        }

            for (User lob_user : lco_userList) {
                try {
                    //get the directory of the user, build the tree and add it to the collection
                    lob_userTree = new UserTree(lob_user, Utils.getRootDirectory());
                    lob_fileTree.addTreeToCollection(lob_userTree);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    //TODO change the path at the end of the project
    public static String getUserBasePath() {
        return GC_FILE_BASE_PATH.replace("$", System.getProperty("user.name"));
    }
}
