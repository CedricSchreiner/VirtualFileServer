package constants;

public abstract class UserDaoConstants {
    public static final String GC_GET_USER = "SELECT * FROM User NATURAL JOIN Admin WHERE email = ?";
    public static final String GC_UPDATE_USER = "UPDATE user SET email = ?, password = ?, isAdmin = ? WHERE userId = ?";
    public static final String GC_DELETE_USER = "DELETE FROM user WHERE userId = ?";
}
