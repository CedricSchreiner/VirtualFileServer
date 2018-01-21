package builder;

import services.classes.UserServiceImpl;
import services.interfaces.UserService;

public class ServiceObjectBuilder {
    /**
     * Generates an UserService object
     * @return UserServiceImpl
     */
    public static UserService getUserServiceObject() {
        return new UserServiceImpl();
    }
}
