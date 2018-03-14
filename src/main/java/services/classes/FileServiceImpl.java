package services.classes;

import builder.ServiceObjectBuilder;
import cache.FileMapperCache;
import com.thoughtworks.xstream.XStream;
import models.classes.*;
import models.constants.CommandConstants;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import services.interfaces.FileService;
import services.interfaces.SharedDirectoryService;
import utilities.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import static models.constants.CommandConstants.GC_RENAME;
import static services.classes.NotifyService.notifyClients;
import static utilities.Utils.convertRelativeToAbsolutePath;

public class FileServiceImpl implements FileService {

    private static Collection<File> readAllFilesFromDirectory(File iob_file) {
        return getAllFiles(new ArrayList<>(), iob_file);
    }

    private static Collection<File> getAllFiles(Collection<File> ico_files, File iob_pointer) {
        ico_files.add(iob_pointer);

        if (iob_pointer.isDirectory()) {
            for (File lob_child : Objects.requireNonNull(iob_pointer.listFiles())) {
                getAllFiles(ico_files, lob_child);
            }
        }

        return ico_files;
    }

    /**
     * download a file from the server
     *
     * @param iva_filePath    path of the file
     * @param iob_user        user who wants to download the file
     * @param iva_directoryId id of the directory where the file is
     * @return the file if it is saved at the given path, otherwise null
     */
    @Override
    public DownloadContent downloadFile(String iva_filePath, User iob_user, int iva_directoryId) {
        File lob_file;
        DownloadContent rob_downloadContent;
        int lva_version;

        if (iob_user == null) {
            return null;
        }

        iva_filePath = convertRelativeToAbsolutePath(iva_filePath, iob_user, iva_directoryId);

        if (iva_filePath == null) {
            return null;
        }

        lob_file = new File(iva_filePath);


        if (lob_file.exists()) {
            lva_version = FileMapperCache.getFileMapperCache().get(lob_file.toPath()).getVersion();
            rob_downloadContent = new DownloadContent(lob_file, lva_version);
            return rob_downloadContent;
        }

        return null;
    }

    /**
     * add a new file to the user directory
     *
     * @param ico_inputList   contains file content and path
     * @param iva_directoryId id of the directory
     * @param iva_ipAddr      Address of the user who send the request
     * @param iva_version     version of the file
     * @return 0 everything went fine, the file was updated
     * 1 user is null
     * 2 the path could not be converted to a absolute path
     * 3 other error
     */
    public int addNewFile(List<InputPart> ico_inputList, String iva_filePath, User iob_user, int iva_directoryId, String iva_ipAddr, int iva_version) {
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

            lva_result = writeFile(lar_fileContentBytes, iva_filePath, iva_version);

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

    /**
     * rename a flle on the server
     *
     * @param iva_filePath    path of the file
     * @param iva_newFileName the new name of the file
     * @param iob_user        the user who wants to rename a file
     * @param iva_ipAddress      Address of the user who send the request
     * @param iva_directoryId id of the source directory
     * @return true if the file was renamed, otherwise false
     */
    @Override
    public boolean renameFile(String iva_filePath, String iva_newFileName, User iob_user, int iva_directoryId, String iva_ipAddress) {
        FileMapperCache lob_fileMapperCache = FileMapperCache.getFileMapperCache();
        MappedFile lob_fileToRename;
        MappedFile lob_cachedFile;
        String lva_serverPath;
        Collection<File> lco_subDirectories;
        File lob_file;
        File lob_renamedFile;
        String lva_renamedFile;
        String lva_newFilePath;
        Path lva_oldFilePath;

        lva_serverPath = Utils.getRootDirectory() +  iva_filePath;
        lob_file = new File(lva_serverPath);
        lva_newFilePath = StringUtils.substringBeforeLast(lva_serverPath, "\\") + "\\" + iva_newFileName;

        lco_subDirectories = readAllFilesFromDirectory(lob_file);

        // Update file mapper cache for all sub directories
        for (File lob_tmpFile : lco_subDirectories) {
            lob_cachedFile = lob_fileMapperCache.get(lob_tmpFile.toPath());
            if (lob_cachedFile != null) {
                lva_oldFilePath = lob_cachedFile.getFilePath();
                lva_renamedFile = lob_cachedFile.getFilePath().toString().replace(lva_serverPath, lva_newFilePath);
                lob_cachedFile.setFilePath(new File(lva_renamedFile).toPath());
                lob_fileMapperCache.updateKeyAndValue(lva_oldFilePath, lob_cachedFile.getFilePath(), lob_cachedFile);
            }
        }

        // Update file name in explorer
        lob_renamedFile = new File(lva_newFilePath);
        if (!lob_file.renameTo(lob_renamedFile)) {
            return false;
        }

        lob_fileToRename = lob_fileMapperCache.get(lob_renamedFile.toPath());

        // Update file mapper cache for renamed file and increment version
        lob_fileToRename.setVersion(lob_fileToRename.getVersion() + 1);

        try {
            notifyClients(Utils.buildRelativeFilePathForClient(lob_renamedFile, iva_directoryId),
                    iob_user, GC_RENAME, iva_directoryId, iva_ipAddress, iva_newFileName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return true;
    }
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

    /**
     * Compare the user tree with another tree
     *
     * @param iva_xmlTreeToCompare tree as xml string
     * @param iob_user             user who wants the result of the tree comparison
     * @return the result of the comparison
     */
    @Override
    public TreeDifference compareFiles(String iva_xmlTreeToCompare, User iob_user) {
        XStream lob_xmlParser = new XStream();
        XStream.setupDefaultSecurity(lob_xmlParser);
        Class[] lar_allowedClasses = {MappedFile.class};
        lob_xmlParser.allowTypes(lar_allowedClasses);
        FileMapperCache lob_fileMapperCache = FileMapperCache.getFileMapperCache();
        TreeDifference lob_treeDifference = new TreeDifference();
        ArrayList<String> lli_updateList = new ArrayList<>();
        ArrayList<String> lli_deleteList = new ArrayList<>();
        ArrayList<String> lli_insertList = new ArrayList<>();
        String lva_relativePathForClient;

        Collection<MappedFile> lob_serverMappedFiles;
        Collection<MappedFile> lob_clientMappedFiles;
        lob_serverMappedFiles = filterFilesForComparison(lob_fileMapperCache.getAll(), iob_user);
        //noinspection unchecked
        lob_clientMappedFiles = (Collection<MappedFile>) lob_xmlParser.fromXML(iva_xmlTreeToCompare);

        for (MappedFile lob_mappedFile : lob_clientMappedFiles) {
            lob_mappedFile.setFilePath(new File(Utils.getRootDirectory() +
                    "\\" + lob_mappedFile.getFilePath()).toPath());
        }


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
//                        lli_updateList.add(Utils.convertServerToRelativeClientPath(lob_clientMappedFile.getFilePath().toString()) + "|"
//                                + lob_serverMappedFile.getVersion());
                        lli_updateList.add(Utils.convertServerToRelativeClientPath(lob_clientMappedFile.getFilePath().toString()));
                        lob_mappedFileIterator.remove();
                        return true;
                    }
                }
            }

            return false;
        });

        for (MappedFile lob_mappedFile : lob_clientMappedFiles) {
            lva_relativePathForClient = Utils.convertServerToRelativeClientPath(lob_mappedFile.getFilePath().toString());
            lli_deleteList.add(lva_relativePathForClient);
        }

        for (MappedFile lob_mappedFile : lob_serverMappedFiles) {
            lva_relativePathForClient = Utils.convertServerToRelativeClientPath(lob_mappedFile.getFilePath().toString());
            lli_insertList.add(lva_relativePathForClient);
        }

        lob_treeDifference.setFilesToUpdate(lli_updateList);
        lob_treeDifference.setFilesToInsert(lli_insertList);
        lob_treeDifference.setFilesToDelete(lli_deleteList);

        return lob_treeDifference;
    }

    /**
     * create a directory on the server
     *
     * @param iva_filePath    path of the directory
     * @param iob_user        the user who wants to create a new directory
     * @param iva_directoryId id of the source directory
     * @param iva_ipAddr      Address of the user who send the request
     * @return 0 if the directory was created
     * 1 the directory already exists
     * 2 the directory could not be created
     * 3 missing information
     */
    public int createDirectory(String iva_filePath, User iob_user, int iva_directoryId, String iva_ipAddr) {
        String lva_relativeFilePathForClient;
        MappedFile lob_mappedFile;
        long lva_lastModified;
        if (iob_user == null) {
            return 3;
        }

        try {
            iva_filePath = convertRelativeToAbsolutePath(iva_filePath, iob_user, iva_directoryId);

            if (iva_filePath == null) {
                return 3;
            }

            File lob_newDirectory = new File(iva_filePath);

            if (lob_newDirectory.exists()) {
                return 1;
            } else {
                if (!lob_newDirectory.mkdir()) {
                    return 2;
                }

                lva_lastModified = Files.readAttributes(lob_newDirectory.toPath(), BasicFileAttributes.class).lastModifiedTime().toMillis();
                lob_mappedFile = new MappedFile(lob_newDirectory.toPath(), 1, lva_lastModified);
                FileMapperCache.getFileMapperCache().put(lob_mappedFile);

                lva_relativeFilePathForClient = Utils.buildRelativeFilePathForClient(lob_newDirectory, iva_directoryId);
                notifyClients(lva_relativeFilePathForClient, iob_user, CommandConstants.GC_ADD, iva_directoryId, iva_ipAddr);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return 3;
    }

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
    private int writeFile(byte[] iva_content, String iva_filename, int iva_version) {
        //----------------------------------------Variables--------------------------------------------
        File lob_file = new File(iva_filename);
        FileOutputStream lob_fileOutputStream;
        MappedFile lob_mappedFile;
        FileMapperCache lob_cache = FileMapperCache.getFileMapperCache();
        //---------------------------------------------------------------------------------------------

        if (lob_file.exists()) {
            lob_mappedFile = FileMapperCache.getFileMapperCache().get(lob_file.toPath());
            if (lob_mappedFile.getVersion() < iva_version) {
                lob_mappedFile.setVersion(iva_version);
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

            lob_mappedFile = new MappedFile(lob_file.toPath(), iva_version, 0);
            lob_cache.put(lob_mappedFile);
        } catch (IOException ex) {
            ex.printStackTrace();
            return 2;
        }
        return 0;
    }

    private Collection<MappedFile> filterFilesForComparison(Collection<MappedFile> ico_files, User iob_user) {
        Collection<MappedFile> lco_files = new ArrayList<>();
        File lob_privateDirectory = new File(Utils.getRootDirectory() + iob_user.getName() + iob_user.getUserId() + "\\");
        File lob_publicDirectory = new File(Utils.getRootDirectory() + "Public\\");
        File lob_sharedDirectory;
        String lva_sharedDirectoryPath = Utils.getRootDirectory();
        Collection<File> lco_sharedDirectories = new ArrayList<>();
        Collection<File> lco_filter = new ArrayList<>();

        lco_filter.add(lob_privateDirectory);
        lco_filter.add(lob_publicDirectory);

        SharedDirectoryService lob_sharedDirectoryService = ServiceObjectBuilder.getSharedDirectoryServiceObject();
        List<SharedDirectory> lli_directories = lob_sharedDirectoryService.getSharedDirectoriesOfUser(iob_user);

        for (SharedDirectory lob_directory : lli_directories) {
            lob_sharedDirectory = new File(lva_sharedDirectoryPath + lob_directory.getOwner().getName() + lob_directory.getOwner().getUserId() + "_shared\\" + lob_directory.getId() + "\\");
            lco_sharedDirectories.add(lob_sharedDirectory);
        }

        lco_filter.addAll(lco_sharedDirectories);

        for (MappedFile lob_mappedFile : ico_files) {
            for (File lob_file : lco_filter) {
                if (lob_mappedFile.getFilePath().startsWith(lob_file.toPath()) &&
                        lob_mappedFile.getFilePath().getNameCount() > lob_file.toPath().getNameCount()) {
                    lco_files.add(lob_mappedFile);
                }
            }
        }

        return lco_files;
    }
}
