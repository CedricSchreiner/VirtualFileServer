package utilities;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import javax.validation.constraints.Null;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

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
}
