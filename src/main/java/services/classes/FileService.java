package services.classes;

import fileTree.interfaces.Tree;
import models.classes.FileTreeCollection;
import models.classes.UserImpl;
import models.interfaces.User;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import rest.Initializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FileService {

    private static FileTreeCollection gob_fileTreeCollection;

    /**
     * add a new file to the user directory
     *
     * @param ico_inputList contains file content and path
     */
    public static void addNewFile(List<InputPart> ico_inputList) {
        //-------------------------------------Variables------------------------------------------
        String lva_filePath;
        byte[] lar_fileContentBytes;
        User lob_user ;
        User lob_dbUser;
        UserServiceImpl lob_userService = new UserServiceImpl();
        //----------------------------------------------------------------------------------------
        for (int i = 0; i < (ico_inputList.size() / 2); i += 2) {
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
                        lva_filePath = Initializer.getUserBasePath() + "\\" + lva_filePath;

                        writeFile(lar_fileContentBytes, lva_filePath, lob_user);
                    }
                }
            }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean renameFile(String iva_filePath, String iva_newFileName, User iob_user) {
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

    public static boolean deleteFile(String iva_filePath, User iob_user) {
        if (iob_user == null) {
            return false;
        }

        iva_filePath = createUserFilePath(iva_filePath, iob_user);
        gob_fileTreeCollection = FileTreeCollection.getInstance();
        return gob_fileTreeCollection.getTreeFromUser(iob_user).deleteFile(iva_filePath);
    }

    public static boolean moveFile(String iva_filePath, String iva_newFilePath, User iob_user) {
        if (iob_user == null) {
            return false;
        }

        //TODO check if is allow to move the file to the destination
        iva_filePath = createUserFilePath(iva_filePath, iob_user);
        gob_fileTreeCollection = FileTreeCollection.getInstance();
        return gob_fileTreeCollection.getTreeFromUser(iob_user).moveFile(iva_filePath, iva_newFilePath);
    }

    public static boolean deleteDirectoryOnly(String iva_filePath, User iob_user) {
        if (iob_user == null) {
            return false;
        }

        //TODO check if is allow to move the file to the destination
        iva_filePath = createUserFilePath(iva_filePath, iob_user);
        gob_fileTreeCollection = FileTreeCollection.getInstance();
        return gob_fileTreeCollection.getTreeFromUser(iob_user).deleteDirectoryOnly(iva_filePath);
    }

    public static boolean createDirectory(String iva_filePath, User iob_user) {
        if (iob_user == null) {
            return false;
        }

        //TODO check if is allow to move the file to the destination
        iva_filePath = createUserFilePath(iva_filePath, iob_user);
        gob_fileTreeCollection = FileTreeCollection.getInstance();
        File lob_newDirectory = new File(iva_filePath);
        return gob_fileTreeCollection.getTreeFromUser(iob_user).addFile(lob_newDirectory, true);
    }

    private static byte[] getFileContent(int iva_index, List<InputPart> ico_inputList) throws IOException{
        return IOUtils.toByteArray(
                ico_inputList.get(iva_index).getBody(InputStream.class,null)
        );
    }

    private static String getFilePath(int iva_index, List<InputPart> ico_inputList) throws IOException{
        return new String(
                IOUtils.toByteArray(ico_inputList.get(iva_index).getBody(InputStream.class, null))
        );
    }

    private static User getUser(int iva_index, List<InputPart> ico_inputList) throws IOException {
        //----------------------------------------------Variables------------------------------------------
        String lva_userString = new String(
                IOUtils.toByteArray(ico_inputList.get(iva_index).getBody(InputStream.class, null))
        );
        //-------------------------------------------------------------------------------------------------
        return createUserFromString(lva_userString);
    }

    private static User createUserFromString(String iva_userString) {
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
    private static void writeFile(byte[] iva_content, String iva_filename, User iob_user) throws IOException {
        //----------------------------------------Vaiables--------------------------------------------
        File lob_file = new File(iva_filename);
        FileOutputStream lob_fileOutputStream;
        FileTreeCollection lob_fileTrees = FileTreeCollection.getInstance();
        Tree lob_tree = lob_fileTrees.getTreeFromUser(iob_user);
        //--------------------------------------------------------------------------------------------

        lob_tree.addFile(lob_file, false);
        lob_fileOutputStream = new FileOutputStream(lob_file);

        lob_fileOutputStream.write(iva_content);
        lob_fileOutputStream.flush();
        lob_fileOutputStream.close();
    }

    private static String createUserFilePath(String iva_relativePath, User iob_user) {
        return Initializer.getUserBasePath() + iob_user.getName() + iob_user.getUserId() + "\\" + iva_relativePath;
    }
}
