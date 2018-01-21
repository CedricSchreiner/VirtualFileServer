package dao.classes;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    private static DatabaseConnection gob_ourInstance = new DatabaseConnection();
    private Connection gob_connection;

    public static DatabaseConnection getInstance() {
        if (gob_ourInstance == null) {
            gob_ourInstance = new DatabaseConnection();
        }
        return gob_ourInstance;
    }

    private DatabaseConnection() {}

    public Connection getConnection() {
        if (this.gob_connection == null) {
            try {
                Class.forName("org.sqlite.JDBC");
                gob_connection = DriverManager.getConnection("jdbc:sqlite:" + this.getClass().getProtectionDomain().getCodeSource().getLocation() + "database.db");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return this.gob_connection;
    }
}
