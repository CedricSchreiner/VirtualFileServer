package builder;

import dao.classes.AdminDaoImpl;
import dao.classes.UserDaoImpl;
import dao.interfaces.AdminDao;
import dao.interfaces.UserDao;

public class DaoObjectBuilder {
    /**
     * Generates an UserDao object
     * @return UserDaoImpl
     */
    public static UserDao getUserDaoObject() {
        return new UserDaoImpl();
    }

    public static AdminDao getAdminDaoObject() {
        return new AdminDaoImpl();
    }
}
