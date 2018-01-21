package models.classes;

import fileTree.interfaces.TreeInterface;
import models.interfaces.User;

public class UserTree {
    private TreeInterface gob_tree;
    private User gob_user;

    public UserTree(User iob_user, TreeInterface iob_tree) {
        this.gob_tree = iob_tree;
        this.gob_user = iob_user;
    }

    public TreeInterface getTree() {
        return gob_tree;
    }

    public void setTree(TreeInterface iob_tree) {
        this.gob_tree = iob_tree;
    }

    public User getUser() {
        return gob_user;
    }

    public void setUser(User iob_user) {
        this.gob_user = iob_user;
    }
}
