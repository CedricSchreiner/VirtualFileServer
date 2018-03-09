package models.classes;

import fileTree.interfaces.Tree;
import fileTree.classes.TreeImpl;

import java.io.File;
import java.io.IOException;

public class ObjectTree {
    private Tree gob_tree;

    public ObjectTree(String iva_rootDirectory) throws IOException {
        File lob_rootFile = new File(iva_rootDirectory);

        if (!lob_rootFile.exists() || !lob_rootFile.isDirectory()) {
            lob_rootFile.mkdir();
        }
        this.gob_tree = new TreeImpl(iva_rootDirectory);
    }

    protected void addFilesToTree(File iob_file) {
        //we have to add the child nodes of the file if the file is a directory
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

    public Tree getTree() {
        return gob_tree;
    }

    public void setTree(Tree iob_tree) {
        this.gob_tree = iob_tree;
    }

    private void addFile(File iob_file, boolean iva_isDirectory) {
        this.gob_tree.addFile(iob_file, iva_isDirectory);
    }
}
