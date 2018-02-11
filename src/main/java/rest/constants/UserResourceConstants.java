package rest.constants;

public abstract class UserResourceConstants {

// ---------------------------------------------------------------------------------------------------------------------
// Resource paths
// ---------------------------------------------------------------------------------------------------------------------

    public static final String GC_USER_RESOURCE_PATH        = "user/";
    public static final String GC_USER_LOGIN_PATH           = "/auth/login/";
    public static final String GC_USER_CHANGE_PASSWORD_PATH = "/auth/changePassword/";
    public static final String GC_USER_ADD_NEW_USER_PATH    = "addNewUser/";
    public static final String GC_USER_GET_ALL_USER_PATH    = "/adminAuth/getAllUser";

// ---------------------------------------------------------------------------------------------------------------------

    public static final String GC_PASSWORD_SUCCESSFULLY_CHANGED = "Password successfully changed!";
    public static final String GC_PASSWORD_NOT_CHANGED          = "Password could not be changed!";
    public static final String GC_USER_SUCCESSFULLY_ADDED       = "User successfully added!";
    public static final String GC_USER_NOT_ADDED                = "The user could not be added!";
    public static final String GC_USER_NOT_AUTHORISED           = "User cant access the resource";
}
