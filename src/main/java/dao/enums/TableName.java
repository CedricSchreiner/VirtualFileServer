package dao.enums;

public enum TableName {
    User("User"), Admin("Admin");

    private String tableName;

    TableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }
}
