package services.classes;

import builder.ServiceObjectBuilder;
import cache.FileMapperCache;
import com.thoughtworks.xstream.XStream;
import models.classes.*;
import models.constants.CommandConstants;
import org.apache.commons.io.FileUtils;
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

import static models.constants.CommandConstants.*;
import static services.classes.NotifyService.notifyClients;
import static utilities.Utils.convertRelativeToAbsolutePath;

public class FileServiceImpl implements FileService {

    public static Collection<File> readAllFilesFromDirectory(File iob_file) {
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
     * @param iva_ipAddress      Address of the user who send the request
     * @param iva_version     version of the file
     * @return 0 everything went fine, the file was updated
     * 1 user is null
     * 2 the path could not be converted to a absolute path
     * 3 other error
     */
    public int addNewFile(List<InputPart> ico_inputList, String iva_filePath, User iob_user, int iva_directoryId,
                          String iva_ipAddress, int iva_version) {
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
                notifyClients(lva_relativeFilePathForClient, iob_user, CommandConstants.GC_ADD, iva_directoryId, iva_ipAddress);
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
     * @param iva_ipAddress      Address of the user who send the request
     * @return true if the deletion was successful, otherwise false
     */
    @Override
    public boolean deleteFile(String iva_filePath, User iob_user, int iva_directoryId, String iva_ipAddress) {
        FileMapperCache lob_fileMapperCache = FileMapperCache.getFileMapperCache();
        String lva_serverPath;
        File lob_fileToDelete;
        Collection<File> lco_fileList;

        lva_serverPath = Utils.getRootDirectory() + iva_filePath;
        lob_fileToDelete = new File(lva_serverPath);

        lco_fileList = readAllFilesFromDirectory(lob_fileToDelete);

        try {
            if (lob_fileToDelete.isDirectory()) {
                FileUtils.deleteDirectory(lob_fileToDelete);
            } else {
                if (!lob_fileToDelete.delete()) {
                    return false;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }

        for (File lob_tmpFile : lco_fileList) {
            lob_fileMapperCache.remove(lob_tmpFile.toPath());
            System.out.println("Delete:" + lob_tmpFile);
        }

        try {
            notifyClients(Utils.buildRelativeFilePathForClient(lob_fileToDelete, iva_directoryId), iob_user,
                    GC_DELETE, iva_directoryId, iva_ipAddress);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        print();

        return true;
    }

    /**
     * move a or rename existing file to a new location
     *
     * @param iva_filePath               the current path of the file
     * @param iva_newFilePath            the new path of the file
     * @param iob_user                   the user who wants to move or rename the file
     * @param iva_sourceDirectoryId      id of the source directory
     * @param iva_destinationDirectoryId id of the destination directory
     * @param iva_ipAddress                 Address of the user who send the request
     * @return true of the file was successfully moved or renamed, otherwise false
     */
    @Override
    public int moveFile(String iva_filePath, String iva_newFilePath, User iob_user, int iva_sourceDirectoryId,
                        int iva_destinationDirectoryId, String iva_ipAddress) {

        FileMapperCache lob_fileMapperCache = FileMapperCache.getFileMapperCache();
        String lva_serverPath;
        String lva_newServerPath;
        Collection<File> lco_fileList;
        MappedFile lob_mappedFile;
        String lob_newFilePath;
        String lva_fileName;
        Path lob_oldMappedFilePath;
        Path lob_newMappedFilePath;
        File lob_oldFile;
        File lob_newFile;

        lva_serverPath = Utils.getRootDirectory() + iva_filePath;
        lva_newServerPath = Utils.getRootDirectory() + iva_newFilePath;
        lva_fileName = new File(lva_serverPath).getName();
        lco_fileList = readAllFilesFromDirectory(new File(lva_serverPath));

        lob_oldFile = new File(lva_serverPath);
        lob_newFile = new File(lva_newServerPath);

        try {
            if (lob_oldFile.isDirectory()) {
                FileUtils.moveDirectoryToDirectory(lob_oldFile, lob_newFile, false);
            } else {
                FileUtils.moveFileToDirectory(lob_oldFile, lob_newFile, false);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return 1;
        }

        for (File lob_tmpFile : lco_fileList) {
            lob_mappedFile = lob_fileMapperCache.get(lob_tmpFile.toPath());
            lob_oldMappedFilePath = lob_mappedFile.getFilePath();
            lob_newFilePath = lob_oldMappedFilePath.toString().replace(lva_serverPath, lva_newServerPath + "\\" + lva_fileName);
            lob_newMappedFilePath = new File(lob_newFilePath).toPath();
            lob_mappedFile.setFilePath(lob_newMappedFilePath);
            lob_mappedFile.setVersion(1);
            lob_fileMapperCache.updateKeyAndValue(lob_oldMappedFilePath, lob_newMappedFilePath, lob_mappedFile);
            System.out.println("Move: " + lob_oldMappedFilePath + " --> " + lob_newMappedFilePath);
        }

        String lva_oldRelativeFilePathForClient = Utils.convertServerToRelativeClientPath(lob_oldFile.toString());
        String lva_newRelativeFilePathForClient = Utils.convertServerToRelativeClientPath(lob_newFile.toString());
        if (iva_sourceDirectoryId == iva_destinationDirectoryId) {
            notifyClients(lva_oldRelativeFilePathForClient, iob_user, CommandConstants.GC_MOVE, iva_sourceDirectoryId, iva_ipAddress, lva_newRelativeFilePathForClient);
        } else {
            notifyClients(lva_oldRelativeFilePathForClient, iob_user, CommandConstants.GC_DELETE, iva_sourceDirectoryId, iva_ipAddress);


            String lva_oldSourceDirectory = lob_oldFile.getParent();
            for (File lob_file : lco_fileList) {
                lva_newRelativeFilePathForClient = lob_file.toString().replace(lva_oldSourceDirectory, lob_newFile.toString());
                lva_newRelativeFilePathForClient = Utils.convertServerToRelativeClientPath(lva_newRelativeFilePathForClient);
                notifyClients(lva_newRelativeFilePathForClient, iob_user, CommandConstants.GC_ADD, iva_destinationDirectoryId, iva_ipAddress);
            }


        }
        return 0;
    }

    /**
     * delete only the directory and move the files that the directory contains to the directories parent
     *
     * @param iva_filePath    the current path of the directory
     * @param iob_user        the user who wants to delete the directory
     * @param iva_directoryId id of the source directory
     * @param iva_ipAddress      Address of the user who send the request
     * @return true if the directory was deleted, otherwise false
     */
    @Override
    public boolean deleteDirectoryOnly(String iva_filePath, User iob_user, int iva_directoryId, String iva_ipAddress) {
        final String lva_newDirectoryName = "_-_DirectoryToDelete";

        FileMapperCache lob_fileMapperCache = FileMapperCache.getFileMapperCache();
        String lva_serverPath;
        File lob_directoryToDelete;
        String lva_basePath;
        int lva_counter = 1;
        Collection<File> lco_fileList;
        MappedFile lob_mappedFile;
        Path lob_oldMappedFilePath;
        File lob_renamedFile;
        File[] lar_fileList;

        lva_serverPath = Utils.getRootDirectory() + iva_filePath;
        lob_directoryToDelete = new File(lva_serverPath);
        lva_basePath = lob_directoryToDelete.getParentFile().getPath();

        if (!lob_directoryToDelete.renameTo(lob_renamedFile = new File(lva_basePath + "\\" + lva_newDirectoryName))) {
            while (!lob_directoryToDelete.renameTo(lob_renamedFile = new File(lva_basePath + "\\" + lva_newDirectoryName
                    + lva_counter))) {

                lva_counter++;
            }
        }

        lco_fileList = readAllFilesFromDirectory(new File(lva_serverPath));

        for (File lob_tmpFile : lco_fileList) {
            lob_mappedFile = lob_fileMapperCache.get(lob_tmpFile.toPath());
            lob_oldMappedFilePath = lob_mappedFile.getFilePath();
            if (lob_oldMappedFilePath.toString().equals(lva_serverPath)) {
                lob_fileMapperCache.remove(lob_mappedFile.getFilePath());
            } else {
                lob_mappedFile.setFilePath(new File(StringUtils.substringBeforeLast(lob_mappedFile.getFilePath().toString(),
                        "\\")).toPath());
                lob_mappedFile.setVersion(1);
                lob_fileMapperCache.updateKeyAndValue(lob_oldMappedFilePath, lob_mappedFile.getFilePath(), lob_mappedFile);
            }

        }

        lar_fileList = lob_renamedFile.listFiles();

        for (File lob_file : Objects.requireNonNull(lar_fileList)) {
            try {
                if (lob_file.isDirectory()) {
                    FileUtils.moveDirectory(lob_file, new File(lva_basePath));
                } else {
                    FileUtils.moveFile(lob_file, new File(lva_basePath));
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            }
        }

        try {
            notifyClients(Utils.buildRelativeFilePathForClient(lob_directoryToDelete, iva_directoryId), iob_user,
                    GC_DELETE_DIR, iva_directoryId, iva_ipAddress);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return !lob_directoryToDelete.delete();
    }
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
        String lva_relativeClientPath;
        Path lva_oldFilePath;

        lva_serverPath = Utils.getRootDirectory() +  iva_filePath;
        lob_file = new File(lva_serverPath);
        lva_newFilePath = StringUtils.substringBeforeLast(lva_serverPath, "\\") + "\\" + iva_newFileName;

        lco_subDirectories = readAllFilesFromDirectory(lob_file);

        // Update file name in explorer
        lob_renamedFile = new File(lva_newFilePath);
        if (!lob_file.renameTo(lob_renamedFile)) {
            return false;
        }

        // Update file mapper cache for all sub directories
        for (File lob_tmpFile : lco_subDirectories) {
            lob_cachedFile = lob_fileMapperCache.get(lob_tmpFile.toPath());
            if (lob_cachedFile != null) {
                lva_oldFilePath = lob_cachedFile.getFilePath();
                lva_renamedFile = lob_cachedFile.getFilePath().toString().replace(lva_serverPath, lva_newFilePath);
                lob_cachedFile.setFilePath(new File(lva_renamedFile).toPath());
                lob_fileMapperCache.updateKeyAndValue(lva_oldFilePath, lob_cachedFile.getFilePath(), lob_cachedFile);
                System.out.println("Rename: " + lva_oldFilePath + " --> " + lob_cachedFile.getFilePath());
            }
        }

        lob_fileToRename = lob_fileMapperCache.get(lob_renamedFile.toPath());

        // Update file mapper cache for renamed file and increment version
        //the version should be reset because it is a completely new file
        lob_fileToRename.setVersion(lob_fileToRename.getVersion() + 1);
        lva_relativeClientPath = Utils.convertServerToRelativeClientPath(lob_file.toString());
        notifyClients(lva_relativeClientPath,
                iob_user, GC_RENAME, iva_directoryId, iva_ipAddress, iva_newFileName);

        return true;
    }

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
                System.out.println("Added: " + lob_mappedFile);
                lva_relativeFilePathForClient = Utils.buildRelativeFilePathForClient(lob_newDirectory, iva_directoryId);
                notifyClients(lva_relativeFilePathForClient, iob_user, CommandConstants.GC_ADD, iva_directoryId, iva_ipAddr);
            }
            return 0;

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
            System.out.println("Added: " + lob_mappedFile);
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

    private void print() {
        System.out.println("\n-----------------------------------------------------------------------------------");
        for (MappedFile lob_mappedFile : FileMapperCache.getFileMapperCache().getAll()) {
            System.out.println(lob_mappedFile);
        }
    }
}
