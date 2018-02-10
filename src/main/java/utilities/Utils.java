package utilities;

import models.interfaces.User;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;

public class Utils {
    public static void check(boolean argument, String msg) {
        if (!argument) {
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Converts an integer to boolean
     *
     * @param iva_integerBoolean the integer
     * @return true if int = 1 otherwise false
     */
    public static boolean convertIntToBoolean(int iva_integerBoolean) {
        return iva_integerBoolean == 1;
    }

    /**
     * Checks if a string is empty
     *
     * @param iob_string the string to check
     * @return true if empty otherwise false
     */
    public static boolean isStringEmpty(String iob_string) {
        return iob_string.trim().isEmpty();
    }

    public static String getRootDirectory() {
        Document doc = null;
        String rootDirectoryPath = "";

        try {
            String filePath = Utils.class.getClassLoader().getResource("virtualFileServerConfig.xml").getPath();
            File file = new File(filePath);

            SAXBuilder builder = new SAXBuilder();
            doc = builder.build(file);

            Element element = doc.getRootElement();
            Element rootDirectory = element.getChild("rootDirectory");
            rootDirectoryPath = rootDirectory.getValue();

        } catch (JDOMException | IOException ex) {
            ex.printStackTrace();
        }

        return rootDirectoryPath;
    }

    /**
     * Its quiet likely that the server and the client have different paths to the same file.
     * To prevent errors we must create a relative path that contains just the start from the root directory to the file
     * @param iva_path the canonical path to a file
     * @param iob_user the user who owns the file
     * @return a relative path to the file that can be send to the client
     */
    public static String createRelativePath(String iva_path, User iob_user) {
        return getRootDirectory();
    }


}
