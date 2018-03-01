package services.classes;

import com.thoughtworks.xstream.XStream;
import fileTree.interfaces.Tree;
import fileTree.interfaces.TreeDifference;
import models.classes.FileTreeCollection;
import models.classes.User;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import services.interfaces.FileService;
import utilities.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FileServiceImpl implements FileService{

    private static FileTreeCollection gob_fileTreeCollection = FileTreeCollection.getInstance();

    /**
     * add a new file to the user directory
     *
     * @param ico_inputList contains file content and path
     */
    public boolean addNewFile(List<InputPart> ico_inputList, String iva_filePath, User iob_user) {
        if (iob_user == null) {
            return false;
        }

        try {
            byte[] lar_fileContentBytes = getFileContent(0, ico_inputList);
            iva_filePath = createUserFilePath(iva_filePath, iob_user);
            return writeFile(lar_fileContentBytes, iva_filePath, iob_user);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    /**
     * delete a file
     * @param iva_filePath the path of the file
     * @param iob_user the user who wants to delete the file
     * @return true if the deletion was successful, otherwise false
     */
    public boolean deleteFile(String iva_filePath, User iob_user) {
        if (iob_user == null) {
            return false;
        }

        iva_filePath = createUserFilePath(iva_filePath, iob_user);
//        gob_fileTreeCollection = FileTreeCollection.getInstance();
        return gob_fileTreeCollection.getTreeFromUser(iob_user).deleteFile(iva_filePath);
    }

    /**
     * move a or rename existing file to a new location
     *
     * @param iva_filePath    the current path of the file
     * @param iva_newFilePath the new path of the file
     * @param iob_user        the user who wants to move or rename the file
     * @return true of the file was successfully moved or renamed, otherwise false
     */
    public boolean moveFile(String iva_filePath, String iva_newFilePath, User iob_user) {
        if (iob_user == null) {
            return false;
        }

        //TODO check if is allow to move the file to the destination
        iva_filePath = createUserFilePath(iva_filePath, iob_user);
        String lva_newFilePath = createUserFilePath(iva_newFilePath, iob_user);
        if (iva_newFilePath.isEmpty()) {
            lva_newFilePath = lva_newFilePath.replaceFirst("\\\\$", "");
        }

//        gob_fileTreeCollection = FileTreeCollection.getInstance();
        return gob_fileTreeCollection.getTreeFromUser(iob_user).moveFile(iva_filePath, lva_newFilePath, false);
    }

    /**
     * delete only the directory and move the files that the directory contains to the directories parent
     * @param iva_filePath the current path of the directory
     * @param iob_user the user who wants to delete the directory
     * @return true if the directory was deleted, otherwise false
     */
    public boolean deleteDirectoryOnly(String iva_filePath, User iob_user) {
        if (iob_user == null) {
            return false;
        }

        //TODO check if is allow to move the file to the destination
        iva_filePath = createUserFilePath(iva_filePath, iob_user);
//        gob_fileTreeCollection = FileTreeCollection.getInstance();
        return gob_fileTreeCollection.getTreeFromUser(iob_user).deleteDirectoryOnly(iva_filePath);
    }

    /**
     * create a directory on the server
     * @param iva_filePath path of the directory
     * @param iob_user th user wo wants to create a new directory
     * @return true if the directory was created, otherwise false
     */
    public boolean createDirectory(String iva_filePath, User iob_user) {
        if (iob_user == null) {
            return false;
        }

        //TODO check if is allow to move the file to the destination
        iva_filePath = createUserFilePath(iva_filePath, iob_user);
//        gob_fileTreeCollection = FileTreeCollection.getInstance();
        File lob_newDirectory = new File(iva_filePath);
        return gob_fileTreeCollection.getTreeFromUser(iob_user).addFile(lob_newDirectory, true);
    }

    /**
     * rename a flle on the server
     *
     * @param iva_filePath path of the file
     * @param iob_user     the user who wants to rename a file
     * @param iva_newFileName the new name of the file
     * @return true if the file was renamed, otherwise false
     */
    @Override
    public boolean renameFile(String iva_filePath, String iva_newFileName, User iob_user) {
        if (iob_user == null) {
            return false;
        }

        iva_filePath = createUserFilePath(iva_filePath, iob_user);
//        gob_fileTreeCollection = FileTreeCollection.getInstance();
        return gob_fileTreeCollection.getTreeFromUser(iob_user).renameFile(iva_filePath, iva_newFileName);
    }

    /**
     * Compare the user tree with another tree
     *
     * @param iva_xmlTreeToCompare tree as xml string
     * @param iob_user             user who wants the result of the tree comparison
     * @return the result of the comparison
     */
    @Override
    public TreeDifference compareTrees(String iva_xmlTreeToCompare, User iob_user) {
        //--------------------------------Variables-----------------------------------
        XStream lob_xmlParser;
        Tree lob_tree;
        Tree iob_userTree;
        //----------------------------------------------------------------------------

        if (iob_user == null) {
            return null;
        }

        lob_xmlParser = new XStream();
        lob_tree = (Tree) lob_xmlParser.fromXML(iva_xmlTreeToCompare);

        if (lob_tree == null) {
            return null;
        }

        iob_userTree = gob_fileTreeCollection.getTreeFromUser(iob_user);

        return iob_userTree.compareTrees(lob_tree);
    }


    private byte[] getFileContent(int iva_index, List<InputPart> ico_inputList) throws IOException{
        return IOUtils.toByteArray(
                ico_inputList.get(iva_index).getBody(InputStream.class,null)
        );
    }

    /**
     * save the file on the server
     * @param iva_content bytes of the file
     * @param iva_filename name of the file
     * @throws IOException
     */
    private boolean writeFile(byte[] iva_content, String iva_filename, User iob_user) throws IOException {
        //----------------------------------------Vaiables--------------------------------------------
        File lob_file = new File(iva_filename);
        FileOutputStream lob_fileOutputStream;
        FileTreeCollection lob_fileTrees = FileTreeCollection.getInstance();
        Tree lob_tree = lob_fileTrees.getTreeFromUser(iob_user);
        //--------------------------------------------------------------------------------------------

        if (!lob_tree.addFile(lob_file, false)){
            return false;
        }

        lob_fileOutputStream = new FileOutputStream(lob_file);

        lob_fileOutputStream.write(iva_content);
        lob_fileOutputStream.flush();
        lob_fileOutputStream.close();
        return true;
    }

    private String createUserFilePath(String iva_relativePath, User iob_user) {
        return Utils.getRootDirectory() + iob_user.getName() + iob_user.getUserId() + "\\" + iva_relativePath;
    }
}
