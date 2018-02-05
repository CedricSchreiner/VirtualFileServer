package models.classes;

import fileTree.interfaces.Tree;
import fileTree.models.TreeImpl;
import models.interfaces.User;

import java.io.File;
import java.io.IOException;

public class UserTree {
    private Tree gob_tree;
    private User gob_user;

    public UserTree(User iob_user, String iva_userDirectoryPath) throws IOException{
        this.gob_user = iob_user;
        this.gob_tree = new TreeImpl(iva_userDirectoryPath);

        try {
            //TODO open the directory for the specific user
            File lob_userDirectory = new File(iva_userDirectoryPath);

            if (lob_userDirectory.exists() && lob_userDirectory.isDirectory()) {
                addFilesToTree(lob_userDirectory);
            } else {
                lob_userDirectory.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Tree getTree() {
        return gob_tree;
    }

    public void setTree(Tree iob_tree) {
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
            addFile(io_file, true);
            File[] lo_fileList = io_file.listFiles();
            if (lo_fileList != null) {
                //add all files in the directory
                for (File lo_directoryChildFile : lo_fileList) {
                    //if the directory contains another directory, do the same for it
                    if (lo_directoryChildFile.isDirectory()) {
                        addFilesToTree(lo_directoryChildFile);
                    } else {
                        //add normal file
                        addFile(lo_directoryChildFile, false);
                    }
                }
            }
        } else {
            addFile(io_file, false);
        }
    }


    private void addFile(File io_file, boolean iva_isDirectory) {
        this.gob_tree.addFile(io_file, iva_isDirectory);
    }
}
