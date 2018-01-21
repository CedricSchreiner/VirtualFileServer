package builder;

import services.classes.UserServiceImpl;
import services.interfaces.UserService;

public class ServiceObjectBuilder {
    public static UserService getUserServiceObject() {
        return new UserServiceImpl();
    }
}
