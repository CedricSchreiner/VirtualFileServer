package services.classes;

import fileTree.interfaces.Tree;
import models.classes.FileTreeCollection;
import models.classes.UserImpl;
import models.interfaces.User;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import rest.Initializer;
import services.interfaces.FileService;
import utilities.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FileServiceImpl implements FileService{

    private static FileTreeCollection gob_fileTreeCollection;

    /**
     * add a new file to the user directory
     *
     * @param ico_inputList contains file content and path
     */
    public boolean addNewFile(List<InputPart> ico_inputList) {
        //-------------------------------------Variables------------------------------------------
        String lva_filePath;
        byte[] lar_fileContentBytes;
        User lob_user ;
        User lob_dbUser;
        UserServiceImpl lob_userService = new UserServiceImpl();
        //----------------------------------------------------------------------------------------
        for (int i = 0; i < (ico_inputList.size() / 2); i += 3) {
            try {
            //get the file content
            lar_fileContentBytes = getFileContent(i, ico_inputList);

            //get the absolute path of the file
            lva_filePath = getFilePath(i + 1, ico_inputList);

            //get the user who owns the file
            lob_user = getUser(i + 2, ico_inputList);

            if (lob_user != null) {
                lob_dbUser = lob_userService.getUserByEmail(lob_user.getEmail());
                if (lob_dbUser.getEmail() != null) {
                    if (PasswordService.checkPasswordEquals(lob_user.getPassword(), lob_dbUser.getPassword())) {
                        lva_filePath = Utils.getRootDirectory() + lob_user.getName() + lob_user.getUserId() + "\\" + lva_filePath;

                        return writeFile(lar_fileContentBytes, lva_filePath, lob_user);
                    }
                }
            }

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * rename a existing file
     * @param iva_filePath the path of the file
     * @param iva_newFileName new name of the file
     * @param iob_user the user that wants to change the file name
     * @return true if the renaming was successful, otherwise false
     */
    public boolean renameFile(String iva_filePath, String iva_newFileName, User iob_user) {
        //---------------------------------------Variables----------------------------------------
        File lob_file;
        //----------------------------------------------------------------------------------------

        if (iob_user == null) {
            return false;
        }

        iva_filePath = createUserFilePath(iva_filePath, iob_user);
        gob_fileTreeCollection = FileTreeCollection.getInstance();
        lob_file = gob_fileTreeCollection.getTreeFromUser(iob_user).getFile(iva_filePath);
        return lob_file.renameTo(new File(lob_file.getParent() + iva_newFileName));
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
        gob_fileTreeCollection = FileTreeCollection.getInstance();
        return gob_fileTreeCollection.getTreeFromUser(iob_user).deleteFile(iva_filePath);
    }

    /**
     * move a existing file to a new location
     * @param iva_filePath the current path of the file
     * @param iva_newFilePath the new path of the file
     * @param iob_user the user who wants to move the file
     * @return true of the file was successfully moved, otherwise false
     */
    public boolean moveFile(String iva_filePath, String iva_newFilePath, User iob_user) {
        if (iob_user == null) {
            return false;
        }

        //TODO check if is allow to move the file to the destination
        iva_filePath = createUserFilePath(iva_filePath, iob_user);
        gob_fileTreeCollection = FileTreeCollection.getInstance();
        return gob_fileTreeCollection.getTreeFromUser(iob_user).moveFile(iva_filePath, iva_newFilePath);
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
        gob_fileTreeCollection = FileTreeCollection.getInstance();
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
        gob_fileTreeCollection = FileTreeCollection.getInstance();
        File lob_newDirectory = new File(iva_filePath);
        return gob_fileTreeCollection.getTreeFromUser(iob_user).addFile(lob_newDirectory, true);
    }

    private byte[] getFileContent(int iva_index, List<InputPart> ico_inputList) throws IOException{
        return IOUtils.toByteArray(
                ico_inputList.get(iva_index).getBody(InputStream.class,null)
        );
    }

    private String getFilePath(int iva_index, List<InputPart> ico_inputList) throws IOException{
        return new String(
                IOUtils.toByteArray(ico_inputList.get(iva_index).getBody(InputStream.class, null))
        );
    }

    private User getUser(int iva_index, List<InputPart> ico_inputList) throws IOException {
        //----------------------------------------------Variables------------------------------------------
        String lva_userString = new String(
                IOUtils.toByteArray(ico_inputList.get(iva_index).getBody(InputStream.class, null))
        );
        //-------------------------------------------------------------------------------------------------
        return createUserFromString(lva_userString);
    }

    private User createUserFromString(String iva_userString) {
        //-----------------------------Variables------------------------
        User rob_user = null;
        try {
            String[] lar_userAttributes = iva_userString.split("\\|", 6);
            String lva_email = lar_userAttributes[0];
            String lva_password = lar_userAttributes[1];
            String lva_name = lar_userAttributes[2];
            boolean lva_isAdmin = Boolean.getBoolean(lar_userAttributes[3]);
            int lva_userId = Integer.valueOf(lar_userAttributes[4]);
            int lva_adminId = Integer.valueOf(lar_userAttributes[5]);
            rob_user = new UserImpl(lva_email, lva_password, lva_name, lva_isAdmin, lva_userId, lva_adminId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //--------------------------------------------------------------

        return rob_user;
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
        return Initializer.getUserBasePath() + iob_user.getName() + iob_user.getUserId() + "\\" + iva_relativePath;
    }
}
