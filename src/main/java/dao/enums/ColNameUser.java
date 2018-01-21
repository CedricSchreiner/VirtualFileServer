package dao.enums;

public enum ColNameUser {
    Email("email"), Password("password"), Name("name"), Id("userId");

    private final String colName;

    ColNameUser(String colName) {
        this.colName = colName;
    }

    public String getColName() {
            return colName;
    }
}
