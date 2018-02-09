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
        iva_userDirectoryPath += iob_user.getName() + iob_user.getUserId();

        try {
            File lob_userDirectory = new File(iva_userDirectoryPath);

            if (!lob_userDirectory.exists() || !lob_userDirectory.isDirectory()) {
                lob_userDirectory.mkdir();
            }

            this.gob_tree = new TreeImpl(iva_userDirectoryPath);
            addFilesToTree(lob_userDirectory);

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

    private void addFilesToTree(File iob_file) {
        //we have to add the child nodes of the file if the file is a direcotry
        if (iob_file.isDirectory()) {
            //add the directory itself
            addFile(iob_file, true);
            File[] lob_fileList = iob_file.listFiles();
            if (lob_fileList != null) {
                //add all files in the directory
                for (File lob_directoryChildFile : lob_fileList) {
                    //if the directory contains another directory, do the same for it
                    if (lob_directoryChildFile.isDirectory()) {
                        addFilesToTree(lob_directoryChildFile);
                    } else {
                        //add normal file
                        addFile(lob_directoryChildFile, false);
                    }
                }
            }
        } else {
            addFile(iob_file, false);
        }
    }


    private void addFile(File iob_file, boolean iva_isDirectory) {
        this.gob_tree.addFile(iob_file, iva_isDirectory);
    }
}
