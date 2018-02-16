package rest.constants;

public abstract class SharedDirectoryConstants {

// ---------------------------------------------------------------------------------------------------------------------
// Resource paths
// ---------------------------------------------------------------------------------------------------------------------

    public static final String GC_SHARED_DIRECTORY_BASE_PATH            = "sharedDirectory/auth";
    public static final String GC_GET_ALL_SHARED_DIRECTORIES_FROM_USER  = "getAllSharedDirectoriesFromUser/";
    public static final String GC_ADD_NEW_SHARED_DIRECTORY              = "addNewSharedDirectory/";
    public static final String GC_ADD_NEW_MEMBER_TO_SHARED_DIRECTORY    = "addNewMemberToSharedDirectory";
    public static final String GC_DELETE_SHARED_DIRECTORY               = "deleteSharedDirectory/";
    public static final String GC_REMOVE_MEMBER_FROM_SHARED_DIRECTORY   = "removeMemberFromSharedDirectory";

    public static final String GC_REMOVE_MEMBER_PARAM                   = "/{sharedDirectoryId}";
    public static final String GC_REMOVE_MEMBER                         = "sharedDirectoryId";

// ---------------------------------------------------------------------------------------------------------------------

    public static final String GC_S_DIR_SUCCESSFULLY_DELETED = "Shared directory successfully deleted!";
    public static final String GC_S_DIR_NOT_DELETED = "Shared directory not deleted!";
    public static final String GC_S_DIR_MEMBER_REMOVED = "Member successfully removed from shared directory";
    public static final String GC_S_DIR_MEMBER_NOT_REMOVED = "Member could not be removed!";
    public static final String GC_S_DIR_SUCCESSFULLY_ADDED = "Shared directory successfully added!";
    public static final String GC_S_DIR_COULD_NOT_BE_ADDED = "Shared directory could not be added!";
    public static final String GC_S_DIR_MEMBER_SUCCESSFULLY_ADDED = "Member successfully added!";
    public static final String GC_S_DIR_MEMBER_NOT_ADDED = "Member could not be added!";
}
