package services.classes;

import fileTree.interfaces.NodeInterface;
import fileTree.models.NodeFactory;
import fileTree.models.Tree;
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

    /**
     * add a new file to the user directory
     *
     * @param ico_inputList contains file content and path
     */
    public static void addNewFile(List<InputPart> ico_inputList) {
        //-------------------------------------Variables------------------------------------------
        InputStream lob_fileContentInputStream;
        String lva_filePath;
        String lva_userInformations;
        byte[] lar_fileContentBytes;
        byte[] lar_userInformation;
        //----------------------------------------------------------------------------------------
        for (int i = 0; i < (ico_inputList.size() / 2); i += 2) {
            try {
            //get the file content
            lar_fileContentBytes = getFileContent(i, ico_inputList);

            //get the absolute path of the file
            lva_filePath = getFilePath(i + 1, ico_inputList);

            //get the user who owns the file
            User user = getUser(i + 2, ico_inputList);

            lva_filePath = Initializer.getUserBasePath() + "\\" + lva_filePath;

            writeFile(lar_fileContentBytes, lva_filePath);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
    private static void writeFile(byte[] iva_content, String iva_filename) throws IOException {
        //----------------------------------------Vaiables--------------------------------------------
        File lob_file = new File(iva_filename);
        FileOutputStream lob_fileOutputStream;
        NodeInterface lob_newFileNode = NodeFactory.createFileNode(lob_file.getName(), lob_file.getAbsolutePath(), lob_file.length());
        //--------------------------------------------------------------------------------------------
        Tree tree = new Tree();
        tree.addNode(lob_newFileNode);
        //TODO Dateigröße aktualisieren, momentan immer 0

        lob_fileOutputStream = new FileOutputStream(lob_file);

        lob_fileOutputStream.write(iva_content);
        lob_fileOutputStream.flush();
        lob_fileOutputStream.close();
    }
}
