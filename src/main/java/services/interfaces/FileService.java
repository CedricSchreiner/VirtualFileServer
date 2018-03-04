package services.interfaces;

import fileTree.interfaces.TreeDifference;
import models.classes.User;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;

import java.io.File;
import java.util.List;

public interface FileService {

    /**
     * download a file from the server
     * @param iva_filePath path of the file
     * @param iob_user user who wants to download the file
     * @param iva_directoryId id of the directory where the file is
     * @return the file if it is saved at the given path, otherwise null
     */
    File downloadFile(String iva_filePath, User iob_user, int iva_directoryId);

    /**
     * add a new file to the user directory
     *
     * @param ico_inputList contains file content and path
     * @param iva_directoryId id of the directory
     */
    boolean addNewFile(List<InputPart> ico_inputList, String iva_filePath, User iob_user, int iva_directoryId);

    /**
     * delete a file
     *
     * @param iva_filePath the path of the file
     * @param iob_user     the user who wants to delete the file
     * @param iva_directoryId id of the directory
     * @return true if the deletion was successful, otherwise false
     */
    boolean deleteFile(String iva_filePath, User iob_user, int iva_directoryId);

    /**
     * move a or rename existing file to a new location
     *
     * @param iva_filePath    the current path of the file
     * @param iva_newFilePath the new path of the file
     * @param iob_user        the user who wants to move or rename the file
     * @param iva_directoryId id of the source directory
     * @param iva_destinationDirectoryId id of the destination directory
     * @return true of the file was successfully moved or renamed, otherwise false
     */
    boolean moveFile(String iva_filePath, String iva_newFilePath, User iob_user, int iva_directoryId, int iva_destinationDirectoryId);

    /**
     * delete only the directory and move the files that the directory contains to the directories parent
     *
     * @param iva_filePath the current path of the directory
     * @param iob_user     the user who wants to delete the directory
     * @param iva_directoryId id of the source directory
     * @return true if the directory was deleted, otherwise false
     */
    boolean deleteDirectoryOnly(String iva_filePath, User iob_user, int iva_directoryId);

    /**
     * create a directory on the server
     *
     * @param iva_filePath path of the directory
     * @param iob_user     the user who wants to create a new directory
     * @param iva_directoryId id of the source directory
     * @return true if the directory was created, otherwise false
     */
    boolean createDirectory(String iva_filePath, User iob_user, int iva_directoryId);

    /**
     * rename a flle on the server
     * @param iva_filePath path of the file
     * @param iob_user the user who wants to rename a file
     * @param iva_newFileName the new name of the file
     * @param iva_directoryId id of the source directory
     * @return true if the file was renamed, otherwise false
     */
    boolean renameFile(String iva_filePath, String iva_newFileName, User iob_user, int iva_directoryId);

    /**
     * Compare the user tree with another tree
     * @param iva_xmlTreeToCompare tree as xml string
     * @param iob_user user who wants the result of the tree comparison
     * @param iva_directoryId > 0: shared directory
     *                        = 0: public directory
     *                        < 0: private directory
     * @return the result of the comparison
     */
    TreeDifference compareTrees(String iva_xmlTreeToCompare, User iob_user, int iva_directoryId);
}
