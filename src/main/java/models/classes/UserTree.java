package models.classes;

import fileTree.interfaces.NodeInterface;
import fileTree.interfaces.TreeInterface;
import fileTree.models.Node;
import models.interfaces.User;
import fileTree.models.Tree;

import java.io.File;

public class UserTree {
    private TreeInterface gob_tree;
    private User gob_user;

    public UserTree(User iob_user) {
        this.gob_user = iob_user;
        this.gob_tree = new Tree();
        try {
            //TODO open the directory for the specific user
            addFilesToTree(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void addFilesToTree(File io_file) {
        //we have to add the child nodes of the file if the file is a direcotry
        if (io_file.isDirectory()) {
            //add the directory itself
            addFile(io_file);
            File[] lo_fileList = io_file.listFiles();
            if (lo_fileList != null) {
                //add all files in the directory
                for (File lo_directoryChildFile : lo_fileList) {
                    //if the directory contains another directory, do the same for it
                    if (lo_directoryChildFile.isDirectory()) {
                        addFilesToTree(lo_directoryChildFile);
                    } else {
                        //add normal file
                        addFile(lo_directoryChildFile);
                    }
                }
            }
        } else {
            addFile(io_file);
        }
    }


    private void addFile(File io_file) {
        NodeInterface lob_node = new Node(io_file.getName(), io_file.getAbsolutePath(), io_file.isDirectory(), io_file.length());
        this.gob_tree.addNode(lob_node);
    }
}
