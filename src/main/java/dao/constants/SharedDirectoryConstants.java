package dao.constants;

import dao.enums.ColNameSharedDirectory;
import dao.enums.ColNameSharedDirectoryMember;
import dao.enums.TableName;

public abstract class SharedDirectoryConstants {
    public static final String GC_TABLE_SHARED_DIRECTORY = TableName.SharedDirectory.getTableName();

    public static final String GC_COL_SHARED_D_ID = ColNameSharedDirectory.Id.getColName();
    public static final String GC_COL_SHARED_D_OWNER = ColNameSharedDirectory.Owner.getColName();
    public static final String GC_COL_SHARED_D_GROUP_NAME = ColNameSharedDirectory.GroupName.getColName();

    public static final String GC_TABLE_SHARED_DIRECTORY_MEMBER = TableName.SharedDirectoryMember.getTableName();

    public static final String GC_COL_SHARED_D_MEMBER_ID = ColNameSharedDirectoryMember.Id.getColName();
    public static final String GC_COL_SHARED_D_MEMBER_GROUP_ID = ColNameSharedDirectoryMember.GroupId.getColName();
    public static final String GC_COL_SHARED_D_MEMBER_MEMBER_ID = ColNameSharedDirectoryMember.Member.getColName();
}
