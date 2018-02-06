package dao.enums;

public enum ColNameSharedDirectoryMember {
    Id("id"), GroupId("groupId"), Member("member");

    private final String colName;

    ColNameSharedDirectoryMember(String colName) {
        this.colName = colName;
    }

    public String getColName() {
        return colName;
    }
}
