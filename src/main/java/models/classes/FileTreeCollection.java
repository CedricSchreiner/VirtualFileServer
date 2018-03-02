package models.classes;

import fileTree.interfaces.Tree;

import java.util.ArrayList;
import java.util.Collection;

public class FileTreeCollection {
    private static FileTreeCollection gob_instance = new FileTreeCollection();
    private Collection<UserTree> gco_userTrees;
    private Collection<SharedDirectoryTree> gco_sharedDirectories;

    private FileTreeCollection() {
        this.gco_userTrees = new ArrayList<>();
        this.gco_sharedDirectories = new ArrayList<>();
    }

    public static FileTreeCollection getInstance() {
        return gob_instance;
    }

    public void addUserTreeToCollection(UserTree iob_userTree) {
        this.gco_userTrees.add(iob_userTree);
    }

    public void addSharedDirectoryTree(SharedDirectoryTree iob_sharedDirectoryTree) {
        this.gco_sharedDirectories.add(iob_sharedDirectoryTree);
    }

    public SharedDirectory getSharedDirectoryFromTree(int iva_id) {
        for (SharedDirectoryTree lob_sharedDirectory : gco_sharedDirectories) {
            if (lob_sharedDirectory.getSharedDirectory().getId() == iva_id) {
                return lob_sharedDirectory.getSharedDirectory();
            }
        }
        return null;
    }

    public Tree getTreeFromUser(User iob_user) {
        //--------------------Variables--------------------
        String lob_userEmail = iob_user.getEmail();
        String lob_userTreeEmail;
        //-------------------------------------------------

        for (UserTree userTree : this.gco_userTrees) {
            lob_userTreeEmail = userTree.getUser().getEmail();
            if (lob_userEmail.equals(lob_userTreeEmail)) {
                return userTree.getTree();
            }
        }
        return null;
    }

    public Tree getSharedDirectoryTree(int iva_id) {
        for (SharedDirectoryTree lob_sharedDirectory : gco_sharedDirectories) {
            if (lob_sharedDirectory.getSharedDirectory().getId() == iva_id) {
                return lob_sharedDirectory.getTree();
            }
        }
        return null;
    }
}
