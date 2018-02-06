package dao.enums;

public enum TableName {
    User("User"), Admin("Admin"), SharedDirectory("SharedDirectory"), SharedDirectoryMember("SharedDirectoryMember");

    private final String tableName;

    TableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }
}
