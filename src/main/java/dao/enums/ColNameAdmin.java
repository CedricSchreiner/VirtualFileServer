package dao.enums;

public enum ColNameAdmin {
    Id("adminId"), UserId("userId");

    private String colName;

    ColNameAdmin(String colName) {
        this.colName = colName;
    }

    public String getColName() {
        return colName;
    }
}
