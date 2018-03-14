package utilities;

import builder.ServiceObjectBuilder;
import models.classes.SharedDirectory;
import models.classes.User;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import services.classes.SharedDirectoryServiceImpl;

import java.io.File;
import java.io.IOException;
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
        Document doc;
        String rootDirectoryPath = "";

        try {
            String filePath = Objects.requireNonNull(Utils.class.getClassLoader()
                    .getResource("virtualFileServerConfig.xml")).getPath();
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

    /**
     * Convert a file from the server to a relative file path that can be send to the client
     * Private File: C:\VirtualFileSystem\{User}\textFile.txt -> Private\textFile.txt
     * Public File: C:\VirtualFileSystem\Public\textFile.txt -> Public\textFile.txt
     * Shared Directory: C:\VirtualFileSystem\{User}_shared\textFile.txt -> Shared\textFile.txt
     * @param iob_file file on the server
     * @param iva_directoryId directory id in which the file is
     * @return a relative file path for the client
     */
    public static String buildRelativeFilePathForClient(File iob_file, int iva_directoryId) throws IOException{
        String rva_relativeFilePath;
        String lva_rootDirectory = getRootDirectory();

        rva_relativeFilePath = iob_file.getCanonicalPath();
        rva_relativeFilePath = rva_relativeFilePath.replace(lva_rootDirectory, "");

        if (iva_directoryId < 0) {
            rva_relativeFilePath = rva_relativeFilePath.replaceFirst("^[^\\\\]*", "Private");
        }

        if (iva_directoryId == 0) {
            rva_relativeFilePath = rva_relativeFilePath.replaceFirst("^[^\\\\]*", "Public");
        }

        if (iva_directoryId > 0) {
            rva_relativeFilePath = rva_relativeFilePath.replaceFirst("^[^\\\\]*", "Shared");
        }

        return rva_relativeFilePath;
    }

    public static String convertServerToRelativeClientPath(String iva_path) {
        String lva_relativeClientPath;
        iva_path = iva_path.replace(getRootDirectory(), "");
        lva_relativeClientPath = iva_path.replaceFirst("^[^_\\\\]*\\\\", "\\\\");

        if (iva_path.startsWith("Public")) {
            return iva_path;
        }

        //the path has changed -> private directory
        if (!lva_relativeClientPath.equals(iva_path)) {
            lva_relativeClientPath = lva_relativeClientPath.replaceFirst("^[^\\\\]*", "Private");
            return lva_relativeClientPath;
        }

        lva_relativeClientPath = iva_path.replaceFirst("^.*_shared", "Shared");

        if (!lva_relativeClientPath.equals(iva_path)) {
            lva_relativeClientPath = lva_relativeClientPath.replaceFirst("^[^\\\\]*", "Shared");
        }

        return lva_relativeClientPath;
    }

    public static String convertRelativeToAbsolutePath (String iva_relativeFilePath, User iob_user, int iva_directoryId) {
        String lva_rootDirectory = Utils.getRootDirectory();
        String rva_absolutePath = "";
        SharedDirectoryServiceImpl service;
        SharedDirectory lob_sharedDirectory;
        User lob_owner;

        if (iva_directoryId < 0) {
            iva_relativeFilePath = iva_relativeFilePath.replaceFirst("^[^\\\\]*", "");
            rva_absolutePath = lva_rootDirectory + iob_user.getName() + iob_user.getUserId() + iva_relativeFilePath;
        }

        if (iva_directoryId == 0) {
            iva_relativeFilePath = iva_relativeFilePath.replaceFirst("[^\\\\]*", "Public");
            rva_absolutePath = lva_rootDirectory + iva_relativeFilePath;
        }

        if (iva_directoryId > 0) {
            service = ServiceObjectBuilder.getSharedDirectoryServiceObject();
            lob_sharedDirectory = service.getSharedDirectoryById(iva_directoryId);

            if (lob_sharedDirectory == null) {
                return null;
            }

            iva_relativeFilePath = iva_relativeFilePath.replaceFirst("^[^\\\\]*\\\\[^\\\\]*", Integer.toString(lob_sharedDirectory.getId()));
            lob_owner = lob_sharedDirectory.getOwner();

            rva_absolutePath = lva_rootDirectory + lob_owner.getName() + lob_owner.getUserId() + "_shared\\" + iva_relativeFilePath;
        }

        return rva_absolutePath;
    }

//    /**
//     * Convert a file from the server to a relative file path that can be send to the client
//     * Private File: C:\VirtualFileSystem\{User}\textFile.txt -> Private\textFile.txt
//     * Public File: C:\VirtualFileSystem\Public\textFile.txt -> Public\textFile.txt
//     * Shared Directory: C:\VirtualFileSystem\{User}_shared\textFile.txt -> Shared\textFile.txt
//     * @param iob_file
//     * @param iva_directoryId
//     * @return
//     */
//    public static String buildRelativeFilePathForClient(File iob_file, int iva_directoryId) {
//        String rva_
//    }
}
