package dao.constants;

import dao.enums.ColNameUser;
import dao.enums.TableName;

public abstract class UserDaoConstants {
    public static final String TABLE_USER = TableName.User.getTableName();

    public static final String COL_USER_ID = ColNameUser.Id.getColName();
    public static final String COL_USER_EMAIL = ColNameUser.Email.getColName();
    public static final String COL_USER_PASSWORD = ColNameUser.Password.getColName();
    public static final String COL_USER_NAME = ColNameUser.Name.getColName();
}
