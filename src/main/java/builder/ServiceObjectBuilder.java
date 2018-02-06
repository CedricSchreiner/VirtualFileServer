package builder;

import services.classes.AdminServiceImpl;
import services.classes.SharedDirectoryImpl;
import services.classes.UserServiceImpl;
import services.interfaces.AdminService;
import services.interfaces.UserService;

public class ServiceObjectBuilder {
    /**
     * Generates an UserService object
     * @return UserServiceImpl
     */
    public static UserService getUserServiceObject() {
        return new UserServiceImpl();
    }

    /**
     * Generates an AdminService object
     * @return AdminServiceImpl
     */
    public static AdminService getAdminServiceObject() {
        return new AdminServiceImpl();
    }

    /**
     * Generates an SharedDirectoryService
     * @return SharedDirectoryImpl
     */
    public static SharedDirectoryImpl getSharedDirectoryServiceObject(){
        return new SharedDirectoryImpl();
    }
}
