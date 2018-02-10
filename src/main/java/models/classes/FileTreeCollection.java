package models.classes;

import fileTree.interfaces.Tree;

import java.util.ArrayList;
import java.util.Collection;

public class FileTreeCollection {
    private static FileTreeCollection ourInstance = new FileTreeCollection();
    private Collection<UserTree> gco_treeCollection;

    private FileTreeCollection() {
        this.gco_treeCollection = new ArrayList<>();
    }

    public static FileTreeCollection getInstance() {
        return ourInstance;
    }

    public void addTreeToCollection(UserTree iob_userTree) {
        this.gco_treeCollection.add(iob_userTree);
    }

    public Tree getTreeFromUser(User iob_user) {
        //--------------------Variables--------------------
        String lob_userEmail = iob_user.getEmail();
        String lob_userTreeEmail;
        //-------------------------------------------------

        for (UserTree userTree : this.gco_treeCollection) {
            lob_userTreeEmail = userTree.getUser().getEmail();
            if (lob_userEmail.equals(lob_userTreeEmail)) {
                return userTree.getTree();
            }
        }
        return null;
    }
}
