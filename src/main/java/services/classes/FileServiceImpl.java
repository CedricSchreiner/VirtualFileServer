package services.classes;

import com.thoughtworks.xstream.XStream;
import fileTree.interfaces.FileNode;
import fileTree.interfaces.Tree;
import fileTree.interfaces.TreeDifference;
import fileTree.models.FileNodeImpl;
import fileTree.models.TreeImpl;
import models.classes.FileTreeCollection;
import models.classes.SharedDirectory;
import models.classes.User;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import services.interfaces.FileService;
import utilities.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

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
        iva_filePath = createFilePath(iva_filePath, iob_user, iva_directoryId);

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
     */
    public boolean addNewFile(List<InputPart> ico_inputList, String iva_filePath, User iob_user, int iva_directoryId) {
        if (iob_user == null) {
            return false;
        }

        try {
            byte[] lar_fileContentBytes = getFileContent(0, ico_inputList);
            iva_filePath = createFilePath(iva_filePath, iob_user, iva_directoryId);

            return iva_filePath != null && writeFile(lar_fileContentBytes, iva_filePath, iob_user, iva_directoryId);

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
        iva_filePath = createFilePath(iva_filePath, iob_user, iva_directoryId);

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
    public boolean moveFile(String iva_filePath, String iva_newFilePath, User iob_user, int iva_sourceDirectoryId, int iva_destinationDirectoryId) {
        Tree lob_sourceTree = getTreeFromDirectoryId(iob_user, iva_sourceDirectoryId);
        Tree lob_destinationTree = getTreeFromDirectoryId(iob_user, iva_destinationDirectoryId);
        File lob_file;
        Collection<File> lco_files;

        if (iob_user == null || lob_sourceTree == null || lob_destinationTree == null) {
            return false;
        }

        //TODO check if is allow to move the file to the destination
        iva_filePath = createFilePath(iva_filePath, iob_user, iva_sourceDirectoryId);
        iva_newFilePath = createFilePath(iva_newFilePath, iob_user, iva_destinationDirectoryId);

        if (iva_filePath == null || iva_newFilePath == null) {
            return false;
        }

        if (lob_sourceTree == lob_destinationTree) {
            return lob_sourceTree.moveFile(iva_filePath, iva_newFilePath, false);
        }

        lob_file = lob_sourceTree.getFile(iva_filePath);

        if (lob_file == null) {
            return false;
        }

        lco_files = lob_sourceTree.getDirectory(lob_file);


        try {
            if (lob_file.isDirectory()) {
                FileUtils.moveDirectoryToDirectory(lob_file, new File(iva_newFilePath), false);
            } else {
                FileUtils.moveFileToDirectory(lob_file, new File(iva_newFilePath), false);
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            return false;
        }

        for (File lob_child : lco_files) {
            String path = lob_child.getAbsolutePath();
            path = path.replace(lob_sourceTree.getRoot().getAbsolutePath(), lob_destinationTree.getRoot().getAbsolutePath());
            File lob_newFile = new File(path);
            lob_destinationTree.addFile(lob_newFile, lob_newFile.isDirectory());
        }

        return lob_sourceTree.deleteFile(lob_file);

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
        iva_filePath = createFilePath(iva_filePath, iob_user, iva_directoryId);
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
        iva_filePath = createFilePath(iva_filePath, iob_user, iva_directoryId);

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

        iva_filePath = createFilePath(iva_filePath, iob_user, iva_directoryId);

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

        if (!lob_tree.addFile(lob_file, false)){
            return false;
        }

        lob_fileOutputStream = new FileOutputStream(lob_file);

        lob_fileOutputStream.write(iva_content);
        lob_fileOutputStream.flush();
        lob_fileOutputStream.close();
        return true;
    }

    private String createUserFilePath(String iva_relativePath, User iob_user) {
        return Utils.getRootDirectory() + iob_user.getName() + iob_user.getUserId() + "\\" + iva_relativePath;
    }

    private String createFilePath(String iva_filePath, User iob_user, int iva_directoryId) {
        String lva_rootPath = Utils.getRootDirectory();

        if (iva_directoryId < 0) {
            iva_filePath = iva_filePath.replaceFirst("^Private", "");
            iva_filePath = iob_user.getName() + iob_user.getUserId() + iva_filePath;
            return lva_rootPath + iva_filePath.replaceFirst("^Private", "");
        }

        if (iva_directoryId == 0) {
            return lva_rootPath + iva_filePath;
        } else {
            SharedDirectoryServiceImpl service = new SharedDirectoryServiceImpl();
            SharedDirectory lob_sharedDirectory  = service.getSharedDirectoryById(iva_directoryId);
            if (lob_sharedDirectory == null) {
                return null;
            }

            User lob_owner = lob_sharedDirectory.getOwner();
            iva_filePath = iva_filePath.replaceFirst("^Shared", "");
            return lob_owner.getName() + lob_owner.getUserId() + "_shared" + iva_filePath;
        }
    }

    private Tree getTreeFromDirectoryId(User iob_User, int iva_directoryId) {
        if (iva_directoryId < 0) {
            return gob_fileTreeCollection.getTreeFromUser(iob_User);
        }

        return gob_fileTreeCollection.getSharedDirectoryTree(iva_directoryId);
    }
}
