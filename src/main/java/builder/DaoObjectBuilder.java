package builder;

import dao.classes.UserDaoImpl;
import dao.interfaces.UserDao;

public class DaoObjectBuilder {
    /**
     * Generates an UserDao object
     * @return UserDaoImpl
     */
    public static UserDao getUserDaoObject() {
        return new UserDaoImpl();
    }
}
