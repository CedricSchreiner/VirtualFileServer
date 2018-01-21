package models.classes;

import models.exceptions.UserException;
import models.interfaces.User;

import static models.constants.UserConstants.*;
import static utilities.Utils.isStringEmpty;

public class UserImpl implements User{
    private String gva_email;
    private String gva_password;
    private String gva_name;
    private boolean gva_isAdmin;
    private int gva_userId;
    private int gva_adminId;

    public UserImpl() {
    }

    public UserImpl(String iva_email, String iva_password, String iva_name, boolean iva_isAdmin, int iva_userId, int iva_adminId) {
        setEmail(iva_email);
        setPassword(iva_password);
        setName(iva_name);
        this.gva_isAdmin = iva_isAdmin;
        setUserId(iva_userId);

        if (iva_isAdmin) {
            setAdminId(iva_adminId);
        }
    }

    public void setEmail(String iva_email) {
        if (isStringEmpty(iva_email)) {
            throw new UserException(GC_INVALID_EMAIL);
        }

        this.gva_email = iva_email;
    }

    public void setPassword(String iva_password) {
        if (isStringEmpty(iva_password)) {
            throw new UserException(GC_INVALID_PASSWORD);
        }

        this.gva_password = iva_password;
    }

    public void setIsAdmin(boolean iva_isAdmin) {
        this.gva_isAdmin = iva_isAdmin;
    }

    public void setUserId(int iva_userId) {
        if (iva_userId < 0) {
            throw new UserException(GC_INVALID_ID);
        }

        this.gva_userId = iva_userId;
    }

    public void setAdminId(int iva_adminId) {
        if (iva_adminId < 0) {
            throw new UserException(GC_INVALID_ID);
        }

        this.gva_adminId = iva_adminId;
    }

    public void setName(String iva_name) {
        if (isStringEmpty(iva_name)) {
            throw new UserException(GC_INVALID_NAME);
        }

        this.gva_name = iva_name;
    }

    public String getEmail() {
        return this.gva_email;
    }

    public String getPassword() {
        return this.gva_password;
    }

    public boolean getIsAdmin() {
        return this.gva_isAdmin;
    }

    public int getUserId() {
        return this.gva_userId;
    }

    public int getAdminId() {
        return this.gva_adminId;
    }

    public String getName() {
        return gva_name;
    }

    @Override
    public String toString() {
        return "UserImpl{" +
                "gva_email='" + gva_email + '\'' +
                ", gva_password='" + gva_password + '\'' +
                ", gva_name='" + gva_name + '\'' +
                ", gva_isAdmin=" + gva_isAdmin +
                ", gva_userId=" + gva_userId +
                ", gva_adminId=" + gva_adminId +
                '}';
    }
}
