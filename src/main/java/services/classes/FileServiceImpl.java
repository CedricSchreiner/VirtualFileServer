package services.classes;

import com.thoughtworks.xstream.XStream;
import fileTree.interfaces.FileNode;
import fileTree.interfaces.Tree;
import fileTree.interfaces.TreeDifference;
import fileTree.classes.FileNodeImpl;
import fileTree.classes.TreeImpl;
import models.classes.FileTreeCollection;
import models.classes.User;
import models.constants.CommandConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import services.interfaces.FileService;
import utilities.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.Collection;
import java.util.List;

import static services.classes.NotifyService.notifyClients;
import static services.constants.FileServiceConstants.*;
import static utilities.Utils.convertRelativeToAbsolutePath;

public class FileServiceImpl implements FileService{

    private static FileTreeCollection gob_fileTreeCollection = FileTreeCollection.getInstance();

    /**
     * download a file from the server
     *
     * @param iva_filePath    path of the file
     * @param iob_user        user who wants to download the file
     * @param iva_directoryId id of the directory where the file is
     * @return the file if it is saved at the given path, otherwise null
     */
    @Override
    public File downloadFile(String iva_filePath, User iob_user, int iva_directoryId) {
        if (iob_user == null) {
            return null;
        }

        Tree tree;
        iva_filePath = convertRelativeToAbsolutePath(iva_filePath, iob_user, iva_directoryId);

        if (iva_filePath == null) {
            return null;
        }

        if (iva_directoryId < 0) {
            tree = FileTreeCollection.getInstance().getTreeFromUser(iob_user);
        } else {
            tree = FileTreeCollection.getInstance().getSharedDirectoryTree(iva_directoryId);
        }

        if (tree == null) {
            return null;
        }

        return tree.getFile(iva_filePath);
    }

    /**
     * add a new file to the user directory
     *
     * @param ico_inputList contains file content and path
     * @param iva_directoryId id of the directory
     * @param iva_ipAddr Address of the user who send the request
     */
    public boolean addNewFile(List<InputPart> ico_inputList, String iva_filePath, User iob_user, int iva_directoryId, String iva_ipAddr, long iva_lastModified) {
        String lva_relativeFilePathForClient;
        File lob_newFile;

        if (iob_user == null) {
            return false;
        }

        try {
            byte[] lar_fileContentBytes = getFileContent(ico_inputList);
            iva_filePath = convertRelativeToAbsolutePath(iva_filePath, iob_user, iva_directoryId);

            if (iva_filePath == null) {
                return false;
            }

            lob_newFile = writeFile(lar_fileContentBytes, iva_filePath, iob_user, iva_directoryId, iva_lastModified);
            if (lob_newFile != null) {
                lva_relativeFilePathForClient = Utils.buildRelativeFilePathForClient(lob_newFile, iva_directoryId);
                notifyClients(lva_relativeFilePathForClient, iob_user, CommandConstants.GC_ADD, iva_directoryId, iva_ipAddr);
                return true;
            }

            return false;

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    /**
     * delete a file
     * @param iva_filePath the path of the file
     * @param iob_user the user who wants to delete the file
     * @param iva_directoryId id of the directory
     * @param iva_ipAddr Address of the user who send the request
     * @return true if the deletion was successful, otherwise false
     */
    public boolean deleteFile(String iva_filePath, User iob_user, int iva_directoryId, String iva_ipAddr) {
        Tree lob_tree;
        String lva_relativeFilePathForClient;
        File lob_fileToDelete;

        if (iob_user == null) {
            return false;
        }

        try {
            lob_tree = getTreeFromDirectoryId(iob_user, iva_directoryId);
            iva_filePath = convertRelativeToAbsolutePath(iva_filePath, iob_user, iva_directoryId);

            if (iva_filePath == null) {
                return false;
            }

            lob_fileToDelete = new File(iva_filePath);
            if (lob_tree.deleteFile(lob_fileToDelete)) {
                lva_relativeFilePathForClient = Utils.buildRelativeFilePathForClient(lob_fileToDelete, iva_directoryId);
                notifyClients(lva_relativeFilePathForClient, iob_user, CommandConstants.GC_DELETE, iva_directoryId, iva_ipAddr);
                return true;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return false;

    }

    /**
     * move a or rename existing file to a new location
     *
     * @param iva_filePath    the current path of the file
     * @param iva_newFilePath the new path of the file
     * @param iob_user        the user who wants to move or rename the file
     * @param iva_sourceDirectoryId id of the source directory
     * @param iva_destinationDirectoryId id of the destination directory
     * @return true of the file was successfully moved or renamed, otherwise false
     */
    public int moveFile(String iva_filePath, String iva_newFilePath, User iob_user, int iva_sourceDirectoryId, int iva_destinationDirectoryId, String iva_ipAddr) {
        Tree lob_sourceTree = getTreeFromDirectoryId(iob_user, iva_sourceDirectoryId);
        Tree lob_destinationTree = getTreeFromDirectoryId(iob_user, iva_destinationDirectoryId);
//        String lva_relativeFile = iva_filePath;
        String lva_relativeFilePathForClient = iva_newFilePath;
        File lob_file;
        File lob_newFile;
        Collection<File> lco_files;

        if (iob_user == null || lob_sourceTree == null || lob_destinationTree == null) {
            return GC_MISSING_OR_WRONG_ARGUMENT;
        }

        try {
            iva_filePath = convertRelativeToAbsolutePath(iva_filePath, iob_user, iva_sourceDirectoryId);
            iva_newFilePath = convertRelativeToAbsolutePath(iva_newFilePath, iob_user, iva_destinationDirectoryId);

            if (iva_filePath == null || iva_newFilePath == null) {
                return GC_MISSING_OR_WRONG_ARGUMENT;
            }

            if (lob_sourceTree == lob_destinationTree) {
                if(lob_sourceTree.moveFile(iva_filePath, iva_newFilePath, false)) {
                    lob_file = new File(iva_filePath);
                    lva_relativeFilePathForClient = Utils.buildRelativeFilePathForClient(lob_file, iva_sourceDirectoryId);
                    notifyClients(lva_relativeFilePathForClient, iob_user, CommandConstants.GC_MOVE, iva_sourceDirectoryId,iva_ipAddr, lva_relativeFilePathForClient);
                    return GC_SUCCESS;
                }
                return GC_ERROR;
            }

            lob_file = lob_sourceTree.getFile(iva_filePath);
            String lva_oldFileParent = iva_filePath.replaceFirst("\\\\[^\\\\]*$", "");

            if (lob_file == null) {
                return GC_MISSING_OR_WRONG_ARGUMENT;
            }

            //collect the files before they are moved
            lco_files = lob_sourceTree.getDirectory(lob_file);


            if (lob_file.isDirectory()) {
                FileUtils.moveDirectoryToDirectory(lob_file, new File(iva_newFilePath), false);
            } else {
                FileUtils.moveFileToDirectory(lob_file, new File(iva_newFilePath), false);
            }

            for (File lob_child : lco_files) {
                String path = lob_child.getAbsolutePath();
                path = path.replace(lva_oldFileParent, lob_destinationTree.getRoot().getAbsolutePath());
                lob_newFile = new File(path);
                lob_destinationTree.addFile(lob_newFile, lob_child.isDirectory());
            }

            if (lob_sourceTree.deleteFile(lob_file)) {
                System.out.println("-------------------------------");
                lva_relativeFilePathForClient = Utils.buildRelativeFilePathForClient(lob_file, iva_sourceDirectoryId);
                notifyClients(lva_relativeFilePathForClient, iob_user, CommandConstants.GC_DELETE, iva_sourceDirectoryId, iva_ipAddr);
                iva_newFilePath += "\\" + lob_file.getName();

                lob_newFile = new File(iva_newFilePath);
                lco_files = lob_destinationTree.getDirectory(lob_newFile);

                for (File lob_child : lco_files) {
                    lva_relativeFilePathForClient = Utils.buildRelativeFilePathForClient(lob_child, iva_destinationDirectoryId);
                    notifyClients(lva_relativeFilePathForClient, iob_user, CommandConstants.GC_ADD, iva_destinationDirectoryId, iva_ipAddr);
                }
                return GC_SUCCESS;
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            return GC_ERROR;
        }

        return GC_ERROR;
    }

    /**
     * delete only the directory and move the files that the directory contains to the directories parent
     * @param iva_filePath the current path of the directory
     * @param iob_user the user who wants to delete the directory
     * @param iva_directoryId id of the source directory
     * @param iva_ipAddr Address of the user who send the request
     * @return true if the directory was deleted, otherwise false
     */
    public boolean deleteDirectoryOnly(String iva_filePath, User iob_user, int iva_directoryId, String iva_ipAddr) {
        Tree lob_tree;
        String lva_relativePath = iva_filePath;

        if (iob_user == null) {
            return false;
        }

        iva_filePath = convertRelativeToAbsolutePath(iva_filePath, iob_user, iva_directoryId);
        lob_tree = getTreeFromDirectoryId(iob_user, iva_directoryId);

        if (iva_filePath == null || lob_tree == null) {
            return false;
        }

        if (lob_tree.deleteDirectoryOnly(iva_filePath)) {
            notifyClients(lva_relativePath, iob_user, CommandConstants.GC_DELETE_DIR, iva_directoryId, iva_ipAddr);
            return true;
        }
        return false;
    }

    /**
     * create a directory on the server
     *
     * @param iva_filePath path of the directory
     * @param iob_user     the user who wants to create a new directory
     * @param iva_directoryId id of the source directory
     * @param iva_ipAddr Address of the user who send the request
     * @return true if the directory was created, otherwise false
     */
    public boolean createDirectory(String iva_filePath, User iob_user, int iva_directoryId, String iva_ipAddr) {
        Tree lob_tree;
        String lva_relativePath = iva_filePath;
        if (iob_user == null) {
            return false;
        }

        iva_filePath = convertRelativeToAbsolutePath(iva_filePath, iob_user, iva_directoryId);

        if (iva_filePath == null) {
            return false;
        }

        File lob_newDirectory = new File(iva_filePath);
        lob_tree = getTreeFromDirectoryId(iob_user, iva_directoryId);

        if (lob_tree.addFile(lob_newDirectory, true)) {
            notifyClients(lva_relativePath, iob_user, CommandConstants.GC_ADD, iva_directoryId, iva_ipAddr);
            return true;
        }
        return false;
    }

    /**
     * rename a flle on the server
     * @param iva_filePath path of the file
     * @param iob_user the user who wants to rename a file
     * @param iva_newFileName the new name of the file
     * @param iva_directoryId id of the source directory
     * @param iva_ipAddr Address of the user who send the request
     * @return true if the file was renamed, otherwise false
     */
    @Override
    public boolean renameFile(String iva_filePath, String iva_newFileName, User iob_user, int iva_directoryId, String iva_ipAddr) {
        Tree lob_tree;
        String lva_relativePath = iva_filePath;

        if (iob_user == null) {
            return false;
        }

        iva_filePath = convertRelativeToAbsolutePath(iva_filePath, iob_user, iva_directoryId);

        if (iva_filePath == null) {
            return false;
        }

        lob_tree = getTreeFromDirectoryId(iob_user, iva_directoryId);

        if (lob_tree.renameFile(iva_filePath, iva_newFileName)) {
            notifyClients(lva_relativePath, iob_user, CommandConstants.GC_RENAME, iva_directoryId, iva_ipAddr, iva_newFileName);
            return true;
        }
        return false;
    }

    /**
     * Compare the user tree with another tree
     * @param iva_xmlTreeToCompare tree as xml string
     * @param iob_user user who wants the result of the tree comparison
     * @param iva_directoryId > 0: shared directory
     *                        = 0: public directory
     *                        < 0: private directory
     * @return the result of the comparison
     */
    @Override
    public TreeDifference compareTrees(String iva_xmlTreeToCompare, User iob_user, int iva_directoryId) {
        //--------------------------------Variables-----------------------------------
        XStream lob_xmlParser;
        Tree lob_tree;
        Tree lob_importedTree;
        //----------------------------------------------------------------------------

        if (iob_user == null) {
            return null;
        }

        lob_xmlParser = new XStream();


        XStream.setupDefaultSecurity(lob_xmlParser); // to be removed after 1.5

        Class[] lar_allowedClasses = {Tree.class, TreeImpl.class, FileNode.class, FileNodeImpl.class};
        lob_xmlParser.allowTypes(lar_allowedClasses);

        lob_tree = (Tree) lob_xmlParser.fromXML(iva_xmlTreeToCompare);

        if (lob_tree == null) {
            return null;
        }

        if (iva_directoryId < 0) {
            lob_importedTree = gob_fileTreeCollection.getTreeFromUser(iob_user);
            return lob_importedTree.compareTrees(lob_tree);
        } else {
            lob_importedTree = gob_fileTreeCollection.getSharedDirectoryTree(iva_directoryId);
            if (lob_importedTree == null) {
                return null;
            }
            return lob_importedTree.compareTrees(lob_tree);
        }
    }


    private byte[] getFileContent(List<InputPart> ico_inputList) throws IOException{
        return IOUtils.toByteArray(
                ico_inputList.get(0).getBody(InputStream.class,null)
        );
    }

    /**
     * save the file on the server
     * @param iva_content bytes of the file
     * @param iva_filename name of the file
     * @return return the new file or null
     */
    private File writeFile(byte[] iva_content, String iva_filename, User iob_user, int iva_directoryId, long iva_lastModified) throws IOException {
        //----------------------------------------Variables--------------------------------------------
        File lob_file = new File(iva_filename);
        FileOutputStream lob_fileOutputStream;
        Tree lob_tree;
        //---------------------------------------------------------------------------------------------

        lob_tree = getTreeFromDirectoryId(iob_user, iva_directoryId);

        if (lob_tree.getFile(iva_filename) == null) {
            if (!lob_tree.addFile(lob_file, false)){
                return null;
            }
        }

        lob_fileOutputStream = new FileOutputStream(lob_file);
        lob_fileOutputStream.write(iva_content);
        lob_fileOutputStream.flush();
        lob_fileOutputStream.close();

        Files.setLastModifiedTime(lob_file.toPath(), FileTime.fromMillis(iva_lastModified));
        return lob_file;
    }

    private Tree getTreeFromDirectoryId(User iob_User, int iva_directoryId) {
        if (iva_directoryId < 0) {
            return gob_fileTreeCollection.getTreeFromUser(iob_User);
        }

        return gob_fileTreeCollection.getSharedDirectoryTree(iva_directoryId);
    }
}
