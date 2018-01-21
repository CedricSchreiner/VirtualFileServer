package builder;

import models.classes.UserImpl;
import models.interfaces.User;

public class ModelObjectBuilder {
    /**
     * Generates an User object
     * @return UserImpl
     */
    public static User getUserModel() {
        return new UserImpl();
    }

    /**
     * Generates an User object
     * @param iva_email email
     * @param iva_password password
     * @param iva_name name
     * @param iva_isAdmin isAdmin
     * @param iva_userId userId
     * @param iva_adminId adminId
     * @return UserImpl
     */
    public static User getUserModel(String iva_email, String iva_password, String iva_name, boolean iva_isAdmin,
                                    int iva_userId, int iva_adminId) {
        return new UserImpl(iva_email, iva_password, iva_name, iva_isAdmin, iva_userId, iva_adminId);
    }
}
