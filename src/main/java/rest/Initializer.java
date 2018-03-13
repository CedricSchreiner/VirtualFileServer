package rest;

import builder.ServiceObjectBuilder;
import cache.FileMapperCache;
import models.classes.*;
import services.interfaces.SharedDirectoryService;
import services.interfaces.UserService;
import utilities.Utils;
import xmlTools.FileMapper;

import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static rest.constants.InitializerConstants.GC_FILE_BASE_PATH;


public class Initializer extends HttpServlet {
    private static final String gva_rootDirectory = Utils.getRootDirectory();

//    //TODO change the path at the end of the project
//    public static String getUserBasePath() {
//        return GC_FILE_BASE_PATH.replace("$", System.getProperty("user.name"));
//    }

    /**
     * Add all file paths, from the files that are saved by the user, in the associated tree
     */
    public void init() {
        //---------------------------------Variables-------------------------------------------------
        final UserService lob_userService = ServiceObjectBuilder.getUserServiceObject();
        List<User> lco_userList = lob_userService.getAllUser();
        File lob_rootDirectory = new File(Utils.getRootDirectory());
        File lob_publicDirectory;
        //-------------------------------------------------------------------------------------------

        if (!lob_rootDirectory.exists() || !lob_rootDirectory.isDirectory()) {
            lob_rootDirectory.mkdir();
        }

        lob_publicDirectory = new File(gva_rootDirectory + "\\Public");

        if (!lob_publicDirectory.exists() || !lob_publicDirectory.isDirectory()) {
            lob_publicDirectory.mkdir();
        }


        for (User lob_user : lco_userList) {
            try {
                initUserTree(lob_user);
                initUsersSharedDirectories(lob_user);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        initFileMapperCache();
    }

    private void initFileMapperCache() {
        FileMapperCache lob_fileMapperCache = FileMapperCache.getFileMapperCache();
        Collection<File> lco_files = readAllFilesFromRootDirectory();
        MappedFile lob_mappedFile;
        long lva_lastModified;

        for (File lob_file : lco_files) {
            lob_mappedFile = FileMapper.getFile(lob_file.toPath().toString());
            try {
                lva_lastModified = Files.readAttributes(lob_file.toPath(), BasicFileAttributes.class).lastModifiedTime().toMillis();
                if (lob_mappedFile.getFilePath() == null) {
                    lob_mappedFile = new MappedFile(lob_file.toPath(), 1, lva_lastModified);
                    lob_fileMapperCache.put(lob_mappedFile);
                } else {
                    if (lob_mappedFile.getLastModified() < lva_lastModified) {
                        lob_mappedFile.setLastModified(lva_lastModified);
                        lob_mappedFile.setVersion(lob_mappedFile.getVersion() + 1);
                    }
                    lob_fileMapperCache.put(lob_mappedFile);
                }
            } catch (IOException ignore) {

            }
        }

        for (MappedFile f : FileMapperCache.getFileMapperCache().getAll()) {
            System.out.println(f);
        }
    }

    private Collection<File> readAllFilesFromRootDirectory() {
        return getAllFiles(new ArrayList<>(), new File(Utils.getRootDirectory()));
    }

    private Collection<File> getAllFiles(Collection<File> ico_files, File iob_pointer) {
            ico_files.add(iob_pointer);

        if (iob_pointer.isDirectory()) {
            for (File lob_child : Objects.requireNonNull(iob_pointer.listFiles())) {
                getAllFiles(ico_files, lob_child);
            }
        }

        return ico_files;
    }

    public static void initUserTree(User iob_user) throws IOException{
        String lva_userRootDirectory;
        File lob_userRoorDirectoryFile;
//        UserTree lob_userTree;
//        FileTreeCollection lob_collection = FileTreeCollection.getInstance();

        lva_userRootDirectory = gva_rootDirectory + iob_user.getName() + iob_user.getUserId();
        lob_userRoorDirectoryFile = new File(lva_userRootDirectory);

        lob_userRoorDirectoryFile.mkdir();

//        lob_userTree =  new UserTree(iob_user, lva_userRootDirectory);
//        lob_collection.addUserTreeToCollection(lob_userTree);
    }

    public static void initUsersSharedDirectories(User iob_user) throws IOException{
//        FileTreeCollection lob_collection = FileTreeCollection.getInstance();
        SharedDirectoryService lob_sharedDirectoryService = ServiceObjectBuilder.getSharedDirectoryServiceObject();
//        SharedDirectoryTree lob_sharedDirectoryTree;

        File lob_userSharedDirectory =  new File(gva_rootDirectory + iob_user.getName() + iob_user.getUserId() + "_shared");

        if (!lob_userSharedDirectory.exists() || !lob_userSharedDirectory.isDirectory()) {
            lob_userSharedDirectory.mkdir();
        }

        for (SharedDirectory lob_sharedDirectory : lob_sharedDirectoryService.getSharedDirectory(iob_user)) {
            initSharedDirectory(lob_sharedDirectory, iob_user);
//            lob_collection.addSharedDirectoryTree(lob_sharedDirectoryTree);
        }
    }

    public static void initSharedDirectory(SharedDirectory iob_sharedDirectory, User iob_user) throws IOException {
        File lob_file;
        String lob_userSharedDirectoryPath = gva_rootDirectory + iob_user.getName() + iob_user.getUserId() + "_shared";
        String lva_userSharedDirectory = lob_userSharedDirectoryPath + "\\" + iob_sharedDirectory.getId();
        lob_file = new File(lva_userSharedDirectory);
        lob_file.mkdir();
//        return new SharedDirectoryTree(iob_sharedDirectory, lva_userSharedDirectory);
    }

    @Override
    public void destroy() {
        try {
            for (MappedFile lob_mappedFile : FileMapper.getAllFiles()) {
                FileMapper.removeFile(lob_mappedFile.getFilePath().toString());
            }

            for (MappedFile lob_mappedFile : FileMapperCache.getFileMapperCache().getAll()) {
//            FileMapper.removeFile(lob_mappedFile.getFilePath().toString());
                FileMapper.addFile(lob_mappedFile);
            }
        } catch (Exception ignore) {

        }
    }
}
