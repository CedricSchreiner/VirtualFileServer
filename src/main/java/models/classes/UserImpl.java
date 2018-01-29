package models.classes;

import models.exceptions.UserException;
import models.interfaces.User;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

import static models.constants.UserConstants.*;
import static utilities.Utils.isStringEmpty;

@XmlRootElement
public class UserImpl implements User{
    private String email;
    private String password;
    private String name;
    private boolean gva_isAdmin;
    private int gva_userId;
    private int gva_adminId;

    public UserImpl() {
    }

    public UserImpl (String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
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

        this.email = iva_email;
    }

    public void setPassword(String iva_password) {
        if (isStringEmpty(iva_password)) {
            throw new UserException(GC_INVALID_PASSWORD);
        }

        this.password = iva_password;
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

        this.name = iva_name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
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
        return name;
    }

    public boolean isEmpty() {
        return email == null && password == null && name == null;
    }
    @Override
    public String toString() {
        return "UserImpl{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", gva_isAdmin=" + gva_isAdmin +
                ", gva_userId=" + gva_userId +
                ", gva_adminId=" + gva_adminId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserImpl user = (UserImpl) o;
        return Objects.equals(email, user.email) &&
                Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {

        return Objects.hash(email, password);
    }
}
