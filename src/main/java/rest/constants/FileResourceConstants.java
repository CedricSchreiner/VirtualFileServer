package rest.constants;

public abstract class FileResourceConstants {

    public static final String GC_FILE_RESOURCE_PATH = "/auth/files";
    public static final String GC_FILE_UPLOAD_PATH = "/upload";
    public static final String GC_FILE_DOWNLOAD_PATH = "/download";
    public static final String GC_FILE_RENAME_PATH = "/moveOrRename";
    public static final String GC_FILE_DELETE_PATH = "/delete";
    public static final String GC_FILE_MOVE_PATH = "/move";
    public static final String GC_FILE_REMOVE_DIR_ONLY_PATH = "/removeDirectoryOnly";
    public static final String GC_CREATE_DIRECTORY_PATH = "/createDirectory";

    public static final String GC_PATH_PARAMETER_FILE_PATH = "/{path}";
    public static final String GC_PATH_PARAMETER_NEW_FILE_NAME = "/{newName}";
    public static final String GC_PATH_PARAMETER_NEW_FILE_PATH = "/{newPath}";

    public static final String GC_PARAMETER_PATH_NAME = "path";
    public static final String GC_PARAMETER_NEW_FILE_NAME = "newName";
    public static final String GC_PARAMETER_NEW_FILE_PATH = "newPath";

    public static final String GC_ATTACHMENT = "attachment";
    public static final String GC_CONTENT_DISPOSITION = "Content-Disposition";

    public static final String FILE_UPLOADED = "File successfully uploaded";
    public static final String FILE_RENAMED = "File successfully renamed";
    public static final String FILE_DELETED = "File successfully deleted";
    public static final String FILE_MOVED = "File successfully moved";
    public static final String DIRECTORY_DELETED = "Removed the directory and moved all files up";
}
