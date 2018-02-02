package services.classes;

import fileTree.interfaces.NodeInterface;
import fileTree.models.NodeFactory;
import fileTree.models.Tree;
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
        InputStream lob_fileNameInputStream;
        String lva_filePath;
        byte[] lar_fileContentBytes;
        byte[] lar_fileNameBytes;
        //----------------------------------------------------------------------------------------
        for (int i = 0; i < (ico_inputList.size() / 2); i += 2) {
            try {
            //convert the uploaded file to inputstream
            lob_fileContentInputStream = ico_inputList.get(i).getBody(InputStream.class,null);
            lar_fileContentBytes = IOUtils.toByteArray(lob_fileContentInputStream);

            //convert the uploaded file to inputstream
            lob_fileNameInputStream = ico_inputList.get(i + 1).getBody(InputStream.class,null);
            lar_fileNameBytes = IOUtils.toByteArray(lob_fileNameInputStream);
            lva_filePath = new String(lar_fileNameBytes);

            lva_filePath = Initializer.getUserBasePath() + "\\" + lva_filePath;

            writeFile(lar_fileContentBytes, lva_filePath);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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


        lob_fileOutputStream = new FileOutputStream(lob_file);

        lob_fileOutputStream.write(iva_content);
        lob_fileOutputStream.flush();
        lob_fileOutputStream.close();
    }
}
