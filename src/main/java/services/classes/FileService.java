package services.classes;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;

import javax.ws.rs.core.MultivaluedMap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static services.constants.FileServiceConstants.*;

public class FileService {

    /**
     * add a new file to the user directory
     *
     * @param iob_inputList
     */
    public static void addNewFile(List<InputPart> iob_inputList) {
        String iva_filename;

        for (InputPart lob_inputPart : iob_inputList) {
            try {
                MultivaluedMap<String, String> header = lob_inputPart.getHeaders();
                iva_filename = getFileName(header);

                //convert the uploaded file to inputstream
                InputStream inputStream = lob_inputPart.getBody(InputStream.class,null);

                byte[] bytes = IOUtils.toByteArray(inputStream);

                //constructs upload file path
                iva_filename = "C:\\Users\\Cedric\\Documents\\" + iva_filename;

                writeFile(bytes, iva_filename);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private static String getFileName(MultivaluedMap<String, String> iob_header) {
        String[] lva_contentDisposition = iob_header.getFirst(GC_CONTENT_DISPOSITION).split(GC_SEMICOLON);

        for (String lva_filename : lva_contentDisposition) {
            if ((lva_filename.trim().startsWith(GC_FILENAME))) {

                String[] lva_name = lva_filename.split(GC_EQUALS);
                return  lva_name[1].trim().replaceAll(GC_QUOTATION_MARK, GC_SPACE);
            }
        }
        return GC_UNKNOWN;
    }

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
