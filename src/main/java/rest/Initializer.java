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
import java.util.List;


@SuppressWarnings("ResultOfMethodCallIgnored")
public class Initializer extends HttpServlet {
    private static final String gva_rootDirectory = Utils.getRootDirectory();

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
            initUserTree(lob_user);
            initUsersSharedDirectories(lob_user);
        }

        initFileMapperCache();
    }

    private void initFileMapperCache() {
        FileMapperCache lob_fileMapperCache = FileMapperCache.getFileMapperCache();

        for (MappedFile lob_mappedFile : FileMapper.getAllFiles()) {
            lob_fileMapperCache.put(lob_mappedFile);
        }
        for (MappedFile f : FileMapperCache.getFileMapperCache().getAll()) {
            System.out.println(f);
        }
    }

//    private Collection<File> readAllFilesFromRootDirectory() {
//        return getAllFiles(new ArrayList<>(), new File(Utils.getRootDirectory()));
//    }
//
//    private Collection<File> getAllFiles(Collection<File> ico_files, File iob_pointer) {
//            ico_files.add(iob_pointer);
//
//        if (iob_pointer.isDirectory()) {
//            for (File lob_child : Objects.requireNonNull(iob_pointer.listFiles())) {
//                getAllFiles(ico_files, lob_child);
//            }
//        }
//
//        return ico_files;
//    }

    public static void initUserTree(User iob_user) {
        String lva_userRootDirectory;
        File lob_userRoorDirectoryFile;
        lva_userRootDirectory = gva_rootDirectory + iob_user.getName() + iob_user.getUserId();
        lob_userRoorDirectoryFile = new File(lva_userRootDirectory);

        lob_userRoorDirectoryFile.mkdir();
    }

    public static void initUsersSharedDirectories(User iob_user) {
        SharedDirectoryService lob_sharedDirectoryService = ServiceObjectBuilder.getSharedDirectoryServiceObject();

        File lob_userSharedDirectory =  new File(gva_rootDirectory + iob_user.getName() + iob_user.getUserId() + "_shared");

        if (!lob_userSharedDirectory.exists() || !lob_userSharedDirectory.isDirectory()) {
            lob_userSharedDirectory.mkdir();
        }

        for (SharedDirectory lob_sharedDirectory : lob_sharedDirectoryService.getSharedDirectory(iob_user)) {
            initSharedDirectory(lob_sharedDirectory, iob_user);
        }
    }

    public static void initSharedDirectory(SharedDirectory iob_sharedDirectory, User iob_user) {
        File lob_file;
        String lob_userSharedDirectoryPath = gva_rootDirectory + iob_user.getName() + iob_user.getUserId() + "_shared";
        String lva_userSharedDirectory = lob_userSharedDirectoryPath + "\\" + iob_sharedDirectory.getId();
        lob_file = new File(lva_userSharedDirectory);
        lob_file.mkdir();
    }

    @Override
    public void destroy() {
        try {
            for (MappedFile lob_mappedFile : FileMapper.getAllFiles()) {
                FileMapper.removeFile(lob_mappedFile.getFilePath().toString());
            }

            for (MappedFile lob_mappedFile : FileMapperCache.getFileMapperCache().getAll()) {
                FileMapper.addFile(lob_mappedFile);
            }
        } catch (Exception ignore) {

        }
    }
}
