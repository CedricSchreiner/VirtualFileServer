package dao.enums;

public enum ColNameSharedDirectory {
    Id("id"), Owner("owner"), GroupName("groupName"), ;

    private final String colName;

    ColNameSharedDirectory(String colName) {
        this.colName = colName;
    }

    public String getColName() {
        return colName;
    }
}
