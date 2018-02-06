package builder;

import dao.classes.AdminDaoImpl;
import dao.classes.SharedDirectoryDaoImpl;
import dao.classes.UserDaoImpl;
import dao.interfaces.AdminDao;
import dao.interfaces.SharedDirectoryDao;
import dao.interfaces.UserDao;

public class DaoObjectBuilder {
    /**
     * Generates an UserDao object
     * @return UserDaoImpl
     */
    public static UserDao getUserDaoObject() {
        return new UserDaoImpl();
    }

    /**
     * Generates an AdminDao object
     * @return AdminDaoImpl
     */
    public static AdminDao getAdminDaoObject() {
        return new AdminDaoImpl();
    }

    public static SharedDirectoryDao getSharedDirectoryObject() {
        return new SharedDirectoryDaoImpl();
    }
}
