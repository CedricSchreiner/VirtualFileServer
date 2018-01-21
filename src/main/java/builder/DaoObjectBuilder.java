package builder;

import dao.classes.UserDaoImpl;
import dao.interfaces.UserDao;

public class DaoObjectBuilder {
    public static UserDao getUserDaoObject() {
        return new UserDaoImpl();
    }
}
