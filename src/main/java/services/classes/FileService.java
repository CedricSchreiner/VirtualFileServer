package services.classes;

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
     * @param iob_inputList
     */
    public static void addNewFile(List<InputPart> iob_inputList) {
        //-------------------------------------Variables------------------------------------------
        InputStream lob_inputStream;
        String lva_filePath;
        //----------------------------------------------------------------------------------------
        for (int i = 0; i < (iob_inputList.size() / 2); i += 2) {
            try {
//            MultivaluedMap<String, String> lob_contentHeader = iob_inputList.get(i).getHeaders();
//            lva_fileName = getFileName(lob_contentHeader);

            //convert the uploaded file to inputstream
            lob_inputStream = iob_inputList.get(i).getBody(InputStream.class,null);
            byte[] lob_bytes = IOUtils.toByteArray(lob_inputStream);

            //get the path of the file
//            MultivaluedMap<String, String> lob_filePathHeader = iob_inputList.get(i + 1).getHeaders();

            //convert the uploaded file to inputstream
            InputStream inputStream = iob_inputList.get(i + 1).getBody(InputStream.class,null);
            byte[] bytes = IOUtils.toByteArray(inputStream);
            lva_filePath = new String(bytes);

            lva_filePath = Initializer.getUserBasePath() + "\\" + lva_filePath;

            writeFile(lob_bytes, lva_filePath);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    private static String getFileName(MultivaluedMap<String, String> iob_header) {
//        String[] lva_contentDisposition = iob_header.getFirst(GC_CONTENT_DISPOSITION).split(GC_SEMICOLON);
//
//        for (String lva_filename : lva_contentDisposition) {
//            if ((lva_filename.trim().startsWith(GC_FILENAME))) {
//
//                String[] lva_name = lva_filename.split(GC_EQUALS);
//                return  lva_name[1].trim().replaceAll(GC_QUOTATION_MARK, GC_SPACE);
//            }
//        }
//        return GC_UNKNOWN;
//    }

    /**
     * save the file on the server
     * @param iva_content bytes of the file
     * @param iva_filename name of the file
     * @throws IOException
     */
    private static void writeFile(byte[] iva_content, String iva_filename) throws IOException {
        File lob_file = new File(iva_filename);

        FileOutputStream lob_fileOutputStream = new FileOutputStream(lob_file);

        lob_fileOutputStream.write(iva_content);
        lob_fileOutputStream.flush();
        lob_fileOutputStream.close();
    }
}
