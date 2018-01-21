package dao.enums;

public enum TableName {
    Users("users"), Admin("admin");

    private String tableName;

    TableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }
}
