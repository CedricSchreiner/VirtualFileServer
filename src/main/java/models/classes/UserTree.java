package models.classes;

import fileTree.interfaces.TreeInterface;

public class UserTree {
    private TreeInterface gob_tree;
    private UserImpl gob_user;

    public UserTree(UserImpl iob_user, TreeInterface iob_tree) {
        this.gob_tree = iob_tree;
        this.gob_user = iob_user;
    }

    public TreeInterface getTree() {
        return gob_tree;
    }

    public void setTree(TreeInterface iob_tree) {
        this.gob_tree = iob_tree;
    }

    public UserImpl getUser() {
        return gob_user;
    }

    public void setUser(UserImpl iob_user) {
        this.gob_user = iob_user;
    }
}
