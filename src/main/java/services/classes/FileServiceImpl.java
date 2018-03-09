package services.classes;

import builder.ServiceObjectBuilder;
import cache.Cache;
import com.thoughtworks.xstream.XStream;
import fileTree.interfaces.FileNode;
import fileTree.interfaces.Tree;
import fileTree.interfaces.TreeDifference;
import fileTree.models.FileNodeImpl;
import fileTree.models.TreeImpl;
import models.classes.Command;
import models.classes.FileTreeCollection;
import models.classes.SharedDirectory;
import models.classes.User;
import models.constants.CommandConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import services.interfaces.FileService;
import services.interfaces.SharedDirectoryService;
import services.interfaces.UserService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
     * @param iva_ipAddr Address of the user who send the request
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
    public boolean addNewFile(List<InputPart> ico_inputList, String iva_filePath, User iob_user, int iva_directoryId, String iva_ipAddr) {
        String lva_relativePath = iva_filePath;

        if (iob_user == null) {
            return false;
        }

        try {
            byte[] lar_fileContentBytes = getFileContent(0, ico_inputList);
            iva_filePath = convertRelativeToAbsolutePath(iva_filePath, iob_user, iva_directoryId);

            if (iva_filePath == null) {
                return false;
            }

            if (writeFile(lar_fileContentBytes, iva_filePath, iob_user, iva_directoryId)) {
                notifyClients(lva_relativePath, iob_user, CommandConstants.GC_ADD, iva_directoryId, iva_ipAddr);
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
     * @return true if the deletion was successful, otherwise false
     */
    public boolean deleteFile(String iva_filePath, User iob_user, int iva_directoryId) {
        Tree lob_tree;

        if (iob_user == null) {
            return false;
        }

        lob_tree = getTreeFromDirectoryId(iob_user, iva_directoryId);
        iva_filePath = convertRelativeToAbsolutePath(iva_filePath, iob_user, iva_directoryId);

        return iva_filePath != null && lob_tree.deleteFile(iva_filePath);

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
    public int moveFile(String iva_filePath, String iva_newFilePath, User iob_user, int iva_sourceDirectoryId, int iva_destinationDirectoryId) {
        Tree lob_sourceTree = getTreeFromDirectoryId(iob_user, iva_sourceDirectoryId);
        Tree lob_destinationTree = getTreeFromDirectoryId(iob_user, iva_destinationDirectoryId);
        File lob_file;
        Collection<File> lco_files;

        if (iob_user == null || lob_sourceTree == null || lob_destinationTree == null) {
            return GC_MISSING_OR_WRONG_ARGUMENT;
        }

        iva_filePath = convertRelativeToAbsolutePath(iva_filePath, iob_user, iva_sourceDirectoryId);
        iva_newFilePath = convertRelativeToAbsolutePath(iva_newFilePath, iob_user, iva_destinationDirectoryId);

        if (iva_filePath == null || iva_newFilePath == null) {
            return GC_MISSING_OR_WRONG_ARGUMENT;
        }

        if (lob_sourceTree == lob_destinationTree) {
            if(lob_sourceTree.moveFile(iva_filePath, iva_newFilePath, false)) {
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

        try {
            if (lob_file.isDirectory()) {
                FileUtils.moveDirectoryToDirectory(lob_file, new File(iva_newFilePath), false);
            } else {
                FileUtils.moveFileToDirectory(lob_file, new File(iva_newFilePath), false);
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            return GC_ERROR;
        }


        for (File lob_child : lco_files) {
            String path = lob_child.getAbsolutePath();
            path = path.replace(lva_oldFileParent, lob_destinationTree.getRoot().getAbsolutePath());
            File lob_newFile = new File(path);
            lob_destinationTree.addFile(lob_newFile, lob_child.isDirectory());
        }

        if (lob_sourceTree.deleteFile(lob_file)) {
            return GC_SUCCESS;
        }

        return GC_ERROR;
    }

    /**
     * delete only the directory and move the files that the directory contains to the directories parent
     * @param iva_filePath the current path of the directory
     * @param iob_user the user who wants to delete the directory
     * @param iva_directoryId id of the source directory
     * @return true if the directory was deleted, otherwise false
     */
    public boolean deleteDirectoryOnly(String iva_filePath, User iob_user, int iva_directoryId) {
        Tree lob_tree;
        if (iob_user == null) {
            return false;
        }

        //TODO check if is allow to move the file to the destination
        iva_filePath = convertRelativeToAbsolutePath(iva_filePath, iob_user, iva_directoryId);
        lob_tree = getTreeFromDirectoryId(iob_user, iva_directoryId);

        if (iva_filePath == null || lob_tree == null) {
            return false;
        }
//        iva_filePath = createUserFilePath(iva_filePath, iob_user);
//        gob_fileTreeCollection = FileTreeCollection.getInstance();
//        return gob_fileTreeCollection.getTreeFromUser(iob_user).deleteDirectoryOnly(iva_filePath);
        return lob_tree.deleteDirectoryOnly(iva_filePath);
    }

    /**
     * create a directory on the server
     *
     * @param iva_filePath path of the directory
     * @param iob_user     the user who wants to create a new directory
     * @param iva_directoryId id of the source directory
     * @return true if the directory was created, otherwise false
     */
    public boolean createDirectory(String iva_filePath, User iob_user, int iva_directoryId) {
        Tree lob_tree;
        if (iob_user == null) {
            return false;
        }

        //TODO check if is allow to move the file to the destination
//        iva_filePath = createUserFilePath(iva_filePath, iob_user);
        iva_filePath = convertRelativeToAbsolutePath(iva_filePath, iob_user, iva_directoryId);

        if (iva_filePath == null) {
            return false;
        }

        File lob_newDirectory = new File(iva_filePath);
        lob_tree = getTreeFromDirectoryId(iob_user, iva_directoryId);
        return lob_tree.addFile(lob_newDirectory, true);
//        return gob_fileTreeCollection.getTreeFromUser(iob_user).addFile(lob_newDirectory, true);
    }

    /**
     * rename a flle on the server
     * @param iva_filePath path of the file
     * @param iob_user the user who wants to rename a file
     * @param iva_newFileName the new name of the file
     * @param iva_directoryId id of the source directory
     * @return true if the file was renamed, otherwise false
     */
    @Override
    public boolean renameFile(String iva_filePath, String iva_newFileName, User iob_user, int iva_directoryId) {
        Tree lob_tree;

        if (iob_user == null) {
            return false;
        }

        iva_filePath = convertRelativeToAbsolutePath(iva_filePath, iob_user, iva_directoryId);

        if (iva_filePath == null) {
            return false;
        }

        lob_tree = getTreeFromDirectoryId(iob_user, iva_directoryId);
        return lob_tree.renameFile(iva_filePath, iva_newFileName);
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


    private byte[] getFileContent(int iva_index, List<InputPart> ico_inputList) throws IOException{
        return IOUtils.toByteArray(
                ico_inputList.get(iva_index).getBody(InputStream.class,null)
        );
    }

    /**
     * save the file on the server
     * @param iva_content bytes of the file
     * @param iva_filename name of the file
     */
    private boolean writeFile(byte[] iva_content, String iva_filename, User iob_user, int iva_directoryId) throws IOException {
        //----------------------------------------Vaiables--------------------------------------------
        File lob_file = new File(iva_filename);
        FileOutputStream lob_fileOutputStream;
        Tree lob_tree;
        //--------------------------------------------------------------------------------------------

        lob_tree = getTreeFromDirectoryId(iob_user, iva_directoryId);

        if (lob_tree.getFile(iva_filename) == null) {
            if (!lob_tree.addFile(lob_file, false)){
                return false;
            }
        }

        lob_fileOutputStream = new FileOutputStream(lob_file);

        lob_fileOutputStream.write(iva_content);
        lob_fileOutputStream.flush();
        lob_fileOutputStream.close();
        return true;
    }

    private void notifyClients(String iva_relativeFilePath, User iob_user, String iva_command, int iva_directoryId, String iva_ipAddr, String... iar_information) {
        List<String> lli_ipList = new ArrayList<>();
        UserService lob_userService;
        SharedDirectoryService lob_sharedDirectoryService;
        SharedDirectory lob_sharedDirectory;
        Command lob_command;

        if (iva_directoryId < 0) {
            lli_ipList = addIpAddrFromUser(iob_user, iob_user, lli_ipList, iva_ipAddr);
        }

        if (iva_directoryId == 0) {
            lob_userService = ServiceObjectBuilder.getUserServiceObject();

            for (User lob_user : lob_userService.getAllUser()) {
                lli_ipList = addIpAddrFromUser(iob_user, lob_user, lli_ipList, iva_ipAddr);
            }
        }

        if (iva_directoryId > 0) {
            lob_sharedDirectoryService = ServiceObjectBuilder.getSharedDirectoryServiceObject();
            lob_sharedDirectory = lob_sharedDirectoryService.getSharedDirectoryById(iva_directoryId);

            for (User lob_member : lob_sharedDirectory.getMembers()) {
                lli_ipList = addIpAddrFromUser(iob_user, lob_member, lli_ipList, iva_ipAddr);
            }
        }

        lob_command = new Command(iva_relativeFilePath, iva_command, iva_directoryId, iar_information);
        NotifyService notifyService = new NotifyService(lli_ipList, lob_command);
        notifyService.setName("NotifyService");
        notifyService.start();
    }

    private List<String> addIpAddrFromUser(User iob_client, User iob_user, List<String> ili_list, String iva_ipAddr) {
        Cache lob_cache = Cache.getIpCache();

        if (!iob_user.getEmail().equals(iob_client.getEmail())) {
            ili_list.addAll(lob_cache.get(iob_user.getEmail()));
        } else {
            for (String lva_ip : lob_cache.get(iob_user.getEmail())) {
                if (!lva_ip.equals(iva_ipAddr)) {
                    ili_list.add(lva_ip);
                }
            }
        }

        return ili_list;
    }

    private Tree getTreeFromDirectoryId(User iob_User, int iva_directoryId) {
        if (iva_directoryId < 0) {
            return gob_fileTreeCollection.getTreeFromUser(iob_User);
        }

        return gob_fileTreeCollection.getSharedDirectoryTree(iva_directoryId);
    }
}
