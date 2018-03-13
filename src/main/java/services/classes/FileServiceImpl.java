package services.classes;

import cache.FileMapperCache;
import com.thoughtworks.xstream.XStream;
import models.classes.MappedFile;
import models.classes.TreeDifference;
import models.classes.User;
import models.constants.CommandConstants;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static services.classes.NotifyService.notifyClients;
import static utilities.Utils.convertRelativeToAbsolutePath;

public class FileServiceImpl implements FileService {

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
        File rob_file;
        if (iob_user == null) {
            return null;
        }

        iva_filePath = convertRelativeToAbsolutePath(iva_filePath, iob_user, iva_directoryId);

        if (iva_filePath == null) {
            return null;
        }

        rob_file = new File(iva_filePath);

        if (rob_file.exists()) {
            return rob_file;
        }

        return null;
    }

    /**
     * add a new file to the user directory
     *
     * @param ico_inputList   contains file content and path
     * @param iva_directoryId id of the directory
     * @param iva_ipAddr      Address of the user who send the request
     * @return 0 everything went fine, the file was updated
     * 1 user is null
     * 2 the path could not be converted to a absolute path
     * 3 other error
     */
    public int addNewFile(List<InputPart> ico_inputList, String iva_filePath, User iob_user, int iva_directoryId, String iva_ipAddr, long iva_lastModified) {
        String lva_relativeFilePathForClient;
        File lob_newFile;
        int lva_result;

        if (iob_user == null) {
            return 1;
        }

        try {
            byte[] lar_fileContentBytes = getFileContent(ico_inputList);
            iva_filePath = convertRelativeToAbsolutePath(iva_filePath, iob_user, iva_directoryId);

            if (iva_filePath == null) {
                return 2;
            }

            lva_result = writeFile(lar_fileContentBytes, iva_filePath, iob_user, iva_directoryId, iva_lastModified);

            if (lva_result == 0) {
                lob_newFile = new File(iva_filePath);
                lva_relativeFilePathForClient = Utils.buildRelativeFilePathForClient(lob_newFile, iva_directoryId);
                notifyClients(lva_relativeFilePathForClient, iob_user, CommandConstants.GC_ADD, iva_directoryId, iva_ipAddr);
            }

            return lva_result;

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 3;
    }

    /**
     * delete a file
     *
     * @param iva_filePath    the path of the file
     * @param iob_user        the user who wants to delete the file
     * @param iva_directoryId id of the directory
     * @param iva_ipAddr      Address of the user who send the request
     * @return true if the deletion was successful, otherwise false
     */
    @Override
    public boolean deleteFile(String iva_filePath, User iob_user, int iva_directoryId, String iva_ipAddr) {
        return false;
    }

    /**
     * move a or rename existing file to a new location
     *
     * @param iva_filePath               the current path of the file
     * @param iva_newFilePath            the new path of the file
     * @param iob_user                   the user who wants to move or rename the file
     * @param iva_directoryId            id of the source directory
     * @param iva_destinationDirectoryId id of the destination directory
     * @param iva_ipAddr                 Address of the user who send the request
     * @return true of the file was successfully moved or renamed, otherwise false
     */
    @Override
    public int moveFile(String iva_filePath, String iva_newFilePath, User iob_user, int iva_directoryId, int iva_destinationDirectoryId, String iva_ipAddr) {
        return 0;
    }

    /**
     * delete only the directory and move the files that the directory contains to the directories parent
     *
     * @param iva_filePath    the current path of the directory
     * @param iob_user        the user who wants to delete the directory
     * @param iva_directoryId id of the source directory
     * @param iva_ipAddr      Address of the user who send the request
     * @return true if the directory was deleted, otherwise false
     */
    @Override
    public boolean deleteDirectoryOnly(String iva_filePath, User iob_user, int iva_directoryId, String iva_ipAddr) {
        return false;
    }

    /**
     * create a directory on the server
     *
     * @param iva_filePath    path of the directory
     * @param iob_user        the user who wants to create a new directory
     * @param iva_directoryId id of the source directory
     * @param iva_ipAddr      Address of the user who send the request
     * @return true if the directory was created, otherwise false
     */
    @Override
    public boolean createDirectory(String iva_filePath, User iob_user, int iva_directoryId, String iva_ipAddr) {
        return false;
    }

    /**
     * rename a flle on the server
     *
     * @param iva_filePath    path of the file
     * @param iva_newFileName the new name of the file
     * @param iob_user        the user who wants to rename a file
     * @param iva_directoryId id of the source directory
     * @param iva_ipAddr      Address of the user who send the request
     * @return true if the file was renamed, otherwise false
     */
    @Override
    public boolean renameFile(String iva_filePath, String iva_newFileName, User iob_user, int iva_directoryId, String iva_ipAddr) {
        return false;
    }

    /**
     * Compare the user tree with another tree
     *
     * @param iva_xmlTreeToCompare tree as xml string
     * @param iob_user             user who wants the result of the tree comparison
     * @param iva_directoryId      > 0: shared directory
     *                             = 0: public directory
     *                             < 0: private directory
     * @return the result of the comparison
     */
    @Override
    public TreeDifference compareFiles(String iva_xmlTreeToCompare, User iob_user, int iva_directoryId) {
        XStream lob_xmlParser = new XStream();
        XStream.setupDefaultSecurity(lob_xmlParser);
        Class[] lar_allowedClasses = {MappedFile.class};
        lob_xmlParser.allowTypes(lar_allowedClasses);
        FileMapperCache lob_fileMapperCache = FileMapperCache.getFileMapperCache();
        TreeDifference lob_treeDifference = new TreeDifference();
        ArrayList<String> lli_updateList = new ArrayList<>();
        ArrayList<String> lli_deleteList = new ArrayList<>();
        ArrayList<String> lli_insertList = new ArrayList<>();

        Collection<MappedFile> lob_serverMappedFiles;
        Collection<MappedFile> lob_clientMappedFiles;
        lob_serverMappedFiles = lob_fileMapperCache.getAll();
        lob_clientMappedFiles = (Collection<MappedFile>) lob_xmlParser.fromXML(iva_xmlTreeToCompare);


        lob_serverMappedFiles.removeIf(lob_serverMappedFile -> {
            for (Iterator<MappedFile> lob_mappedFileIterator = lob_clientMappedFiles.iterator();
                 lob_mappedFileIterator.hasNext(); ) {
                MappedFile lob_clientMappedFile;
                lob_clientMappedFile = lob_mappedFileIterator.next();

                if (lob_clientMappedFile.getFilePath().equals(lob_serverMappedFile.getFilePath())) {
                    if (lob_clientMappedFile.getVersion() == lob_serverMappedFile.getVersion()) {
                        lob_mappedFileIterator.remove();
                        return true;

                    } else if (lob_clientMappedFile.getVersion() < lob_serverMappedFile.getVersion()) {
                        lli_updateList.add(lob_clientMappedFile.getFilePath() + "|"
                                + lob_clientMappedFile.getVersion());

                        return true;
                    }
                }
            }

            return false;
        });

        for (MappedFile lob_mappedFile : lob_clientMappedFiles) {
            lli_insertList.add(lob_mappedFile.getFilePath().toString());
        }

        for (MappedFile lob_mappedFile : lob_serverMappedFiles) {
            lli_deleteList.add(lob_mappedFile.getFilePath().toString());
        }

        lob_treeDifference.setFilesToUpdate(lli_updateList);
        lob_treeDifference.setFilesToInsert(lli_insertList);
        lob_treeDifference.setFilesToDelete(lli_deleteList);

        return lob_treeDifference;
    }

//    /**
//     * delete a file
//     * @param iva_filePath the path of the file
//     * @param iob_user the user who wants to delete the file
//     * @param iva_directoryId id of the directory
//     * @param iva_ipAddr Address of the user who send the request
//     * @return true if the deletion was successful, otherwise false
//     */
//    public boolean deleteFile(String iva_filePath, User iob_user, int iva_directoryId, String iva_ipAddr) {
//        Tree lob_tree;
//        String lva_relativeFilePathForClient;
//        File lob_fileToDelete;
//
//        if (iob_user == null) {
//            return false;
//        }
//
//        try {
//            lob_tree = getTreeFromDirectoryId(iob_user, iva_directoryId);
//            iva_filePath = convertRelativeToAbsolutePath(iva_filePath, iob_user, iva_directoryId);
//
//            if (iva_filePath == null) {
//                return false;
//            }
//
//            lob_fileToDelete = new File(iva_filePath);
//            if (lob_tree.deleteFile(lob_fileToDelete)) {
//                lva_relativeFilePathForClient = Utils.buildRelativeFilePathForClient(lob_fileToDelete, iva_directoryId);
//                notifyClients(lva_relativeFilePathForClient, iob_user, CommandConstants.GC_DELETE, iva_directoryId, iva_ipAddr);
//                return true;
//            }
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//
//        return false;
//
//    }
//
//    /**
//     * move a or rename existing file to a new location
//     *
//     * @param iva_filePath    the current path of the file
//     * @param iva_newFilePath the new path of the file
//     * @param iob_user        the user who wants to move or rename the file
//     * @param iva_sourceDirectoryId id of the source directory
//     * @param iva_destinationDirectoryId id of the destination directory
//     * @return true of the file was successfully moved or renamed, otherwise false
//     */
//    public int moveFile(String iva_filePath, String iva_newFilePath, User iob_user, int iva_sourceDirectoryId, int iva_destinationDirectoryId, String iva_ipAddr) {
//        Tree lob_sourceTree = getTreeFromDirectoryId(iob_user, iva_sourceDirectoryId);
//        Tree lob_destinationTree = getTreeFromDirectoryId(iob_user, iva_destinationDirectoryId);
//        String lva_relativeFilePathForClient;
//        String lva_newRelativeFilePathForClient;
//        File lob_file;
//        File lob_newFile;
//        Collection<File> lco_files;
//
//        if (iob_user == null || lob_sourceTree == null || lob_destinationTree == null) {
//            return GC_MISSING_OR_WRONG_ARGUMENT;
//        }
//
//        try {
//            iva_filePath = convertRelativeToAbsolutePath(iva_filePath, iob_user, iva_sourceDirectoryId);
//            iva_newFilePath = convertRelativeToAbsolutePath(iva_newFilePath, iob_user, iva_destinationDirectoryId);
//
//            if (iva_filePath == null || iva_newFilePath == null) {
//                return GC_MISSING_OR_WRONG_ARGUMENT;
//            }
//
//            if (lob_sourceTree == lob_destinationTree) {
//                if(lob_sourceTree.moveFile(iva_filePath, iva_newFilePath, false)) {
//                    lob_file = new File(iva_filePath);
//                    lva_relativeFilePathForClient = Utils.buildRelativeFilePathForClient(lob_file, iva_sourceDirectoryId);
//                    lva_newRelativeFilePathForClient = Utils.buildRelativeFilePathForClient(new File(iva_newFilePath), iva_destinationDirectoryId);
//                    notifyClients(lva_relativeFilePathForClient, iob_user, CommandConstants.GC_MOVE, iva_sourceDirectoryId,iva_ipAddr, lva_newRelativeFilePathForClient);
//                    return GC_SUCCESS;
//                }
//                return GC_ERROR;
//            }
//
//            lob_file = lob_sourceTree.getFile(iva_filePath);
//            String lva_oldFileParent = iva_filePath.replaceFirst("\\\\[^\\\\]*$", "");
//
//            if (lob_file == null) {
//                return GC_MISSING_OR_WRONG_ARGUMENT;
//            }
//
//            //collect the files before they are moved
//            lco_files = lob_sourceTree.getDirectory(lob_file);
//
//
//            if (lob_file.isDirectory()) {
//                FileUtils.moveDirectoryToDirectory(lob_file, new File(iva_newFilePath), false);
//            } else {
//                FileUtils.moveFileToDirectory(lob_file, new File(iva_newFilePath), false);
//            }
//
//            for (File lob_child : lco_files) {
//                String path = lob_child.getAbsolutePath();
////                path = path.replace(lva_oldFileParent, lob_destinationTree.getRoot().getAbsolutePath());
//                path = path.replace(lva_oldFileParent, iva_newFilePath);
//                lob_newFile = new File(path);
//                lob_destinationTree.addFile(lob_newFile, lob_child.isDirectory());
//            }
//
//            if (lob_sourceTree.deleteFile(lob_file)) {
//                System.out.println("-------------------------------");
//                lva_relativeFilePathForClient = Utils.buildRelativeFilePathForClient(lob_file, iva_sourceDirectoryId);
//                notifyClients(lva_relativeFilePathForClient, iob_user, CommandConstants.GC_DELETE, iva_sourceDirectoryId, iva_ipAddr);
//                iva_newFilePath += "\\" + lob_file.getName();
//
//                lob_newFile = new File(iva_newFilePath);
//                lco_files = lob_destinationTree.getDirectory(lob_newFile);
//
//                for (File lob_child : lco_files) {
//                    lva_relativeFilePathForClient = Utils.buildRelativeFilePathForClient(lob_child, iva_destinationDirectoryId);
//                    notifyClients(lva_relativeFilePathForClient, iob_user, CommandConstants.GC_ADD, iva_destinationDirectoryId, iva_ipAddr);
//                }
//                return GC_SUCCESS;
//            }
//        } catch (IOException ex) {
//            System.err.println(ex.getMessage());
//            return GC_ERROR;
//        }
//
//        return GC_ERROR;
//    }
//
//    /**
//     * delete only the directory and move the files that the directory contains to the directories parent
//     * @param iva_filePath the current path of the directory
//     * @param iob_user the user who wants to delete the directory
//     * @param iva_directoryId id of the source directory
//     * @param iva_ipAddr Address of the user who send the request
//     * @return true if the directory was deleted, otherwise false
//     */
//    public boolean deleteDirectoryOnly(String iva_filePath, User iob_user, int iva_directoryId, String iva_ipAddr) {
//        Tree lob_tree;
//        String lva_relativeFilePathForClient;
//
//        if (iob_user == null) {
//            return false;
//        }
//
//        try {
//            iva_filePath = convertRelativeToAbsolutePath(iva_filePath, iob_user, iva_directoryId);
//            lob_tree = getTreeFromDirectoryId(iob_user, iva_directoryId);
//
//            if (iva_filePath == null || lob_tree == null) {
//                return false;
//            }
//
//            if (lob_tree.deleteDirectoryOnly(iva_filePath)) {
//                lva_relativeFilePathForClient = Utils.buildRelativeFilePathForClient(new File(iva_filePath), iva_directoryId);
//                notifyClients(lva_relativeFilePathForClient, iob_user, CommandConstants.GC_DELETE_DIR, iva_directoryId, iva_ipAddr);
//                return true;
//            }
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//
//        return false;
//    }
//
//    /**
//     * create a directory on the server
//     *
//     * @param iva_filePath path of the directory
//     * @param iob_user     the user who wants to create a new directory
//     * @param iva_directoryId id of the source directory
//     * @param iva_ipAddr Address of the user who send the request
//     * @return true if the directory was created, otherwise false
//     */
//    public boolean createDirectory(String iva_filePath, User iob_user, int iva_directoryId, String iva_ipAddr) {
//        Tree lob_tree;
//        String lva_relativeFilePathForClient;
//        if (iob_user == null) {
//            return false;
//        }
//
//        try {
//            iva_filePath = convertRelativeToAbsolutePath(iva_filePath, iob_user, iva_directoryId);
//
//            if (iva_filePath == null) {
//                return false;
//            }
//
//            File lob_newDirectory = new File(iva_filePath);
//            lob_tree = getTreeFromDirectoryId(iob_user, iva_directoryId);
//
//            if (lob_tree.addFile(lob_newDirectory, true)) {
//                lva_relativeFilePathForClient = Utils.buildRelativeFilePathForClient(lob_newDirectory, iva_directoryId);
//                notifyClients(lva_relativeFilePathForClient, iob_user, CommandConstants.GC_ADD, iva_directoryId, iva_ipAddr);
//                return true;
//            }
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        return false;
//    }
//
//    /**
//     * rename a flle on the server
//     * @param iva_filePath path of the file
//     * @param iob_user the user who wants to rename a file
//     * @param iva_newFileName the new name of the file
//     * @param iva_directoryId id of the source directory
//     * @param iva_ipAddr Address of the user who send the request
//     * @return true if the file was renamed, otherwise false
//     */
//    @Override
//    public boolean renameFile(String iva_filePath, String iva_newFileName, User iob_user, int iva_directoryId, String iva_ipAddr) {
//        Tree lob_tree;
//        String lva_relativeFilePathForClient;
//
//        if (iob_user == null) {
//            return false;
//        }
//
//        try {
//            iva_filePath = convertRelativeToAbsolutePath(iva_filePath, iob_user, iva_directoryId);
//
//            if (iva_filePath == null) {
//                return false;
//            }
//
//            lob_tree = getTreeFromDirectoryId(iob_user, iva_directoryId);
//
//            if (lob_tree.renameFile(iva_filePath, iva_newFileName)) {
//                lva_relativeFilePathForClient = Utils.buildRelativeFilePathForClient(new File(iva_filePath) ,iva_directoryId);
//                notifyClients(lva_relativeFilePathForClient, iob_user, CommandConstants.GC_RENAME, iva_directoryId, iva_ipAddr, iva_newFileName);
//                return true;
//            }
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        return false;
//    }
//
//    /**
//     * Compare the user tree with another tree
//     * @param iva_xmlTreeToCompare tree as xml string
//     * @param iob_user user who wants the result of the tree comparison
//     * @param iva_directoryId > 0: shared directory
//     *                        = 0: public directory
//     *                        < 0: private directory
//     * @return the result of the comparison
//     */
//    @Override
//    public TreeDifference compareTrees(String iva_xmlTreeToCompare, User iob_user, int iva_directoryId) {
//        //--------------------------------Variables-----------------------------------
//        XStream lob_xmlParser;
//        Tree lob_tree;
//        Tree lob_importedTree;
//        //----------------------------------------------------------------------------
//
//        if (iob_user == null) {
//            return null;
//        }
//
//        lob_xmlParser = new XStream();
//
//
//        XStream.setupDefaultSecurity(lob_xmlParser); // to be removed after 1.5
//
//        Class[] lar_allowedClasses = {Tree.class, TreeImpl.class, FileNode.class, FileNodeImpl.class};
//        lob_xmlParser.allowTypes(lar_allowedClasses);
//
//        lob_tree = (Tree) lob_xmlParser.fromXML(iva_xmlTreeToCompare);
//
//        if (lob_tree == null) {
//            return null;
//        }
//
//        if (iva_directoryId < 0) {
//            lob_importedTree = gob_fileTreeCollection.getTreeFromUser(iob_user);
//            return lob_importedTree.compareTrees(lob_tree);
//        } else {
//            lob_importedTree = gob_fileTreeCollection.getSharedDirectoryTree(iva_directoryId);
//            if (lob_importedTree == null) {
//                return null;
//            }
//            return lob_importedTree.compareTrees(lob_tree);
//        }
//    }


    private byte[] getFileContent(List<InputPart> ico_inputList) throws IOException {
        return IOUtils.toByteArray(
                ico_inputList.get(0).getBody(InputStream.class, null)
        );
    }

    /**
     * save the file on the server
     *
     * @param iva_content  bytes of the file
     * @param iva_filename name of the file
     * @return 0 file was written
     * 1 the file that was send is older
     * 2 error while writing to the file
     */
    private int writeFile(byte[] iva_content, String iva_filename, User iob_user, int iva_directoryId, long iva_lastModified) {
        //----------------------------------------Variables--------------------------------------------
        File lob_file = new File(iva_filename);
        FileOutputStream lob_fileOutputStream;
        MappedFile lob_mappedFile;
        //---------------------------------------------------------------------------------------------

        if (lob_file.exists()) {
            lob_mappedFile = FileMapperCache.getFileMapperCache().get(lob_file.toPath());
            if (lob_mappedFile.getLastModified() < iva_lastModified) {
                lob_mappedFile.setLastModified(iva_lastModified);
                lob_mappedFile.setVersion(lob_mappedFile.getVersion() + 1);
            } else {
                return 1;
            }
        }

        try {
            lob_fileOutputStream = new FileOutputStream(lob_file);
            lob_fileOutputStream.write(iva_content);
            lob_fileOutputStream.flush();
            lob_fileOutputStream.close();

            Files.setLastModifiedTime(lob_file.toPath(), FileTime.fromMillis(iva_lastModified));
        } catch (IOException ex) {
            ex.printStackTrace();
            return 2;
        }
        return 0;
    }

//    private Tree getTreeFromDirectoryId(User iob_User, int iva_directoryId) {
//        if (iva_directoryId < 0) {
//            return gob_fileTreeCollection.getTreeFromUser(iob_User);
//        }
//
//        return gob_fileTreeCollection.getSharedDirectoryTree(iva_directoryId);
//    }
}
