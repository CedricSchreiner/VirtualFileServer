package dao.classes;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    private static DatabaseConnection gob_ourInstance = new DatabaseConnection();

    public static DatabaseConnection getInstance() {
        if (gob_ourInstance == null) {
            gob_ourInstance = new DatabaseConnection();
        }
        return gob_ourInstance;
    }

    private DatabaseConnection() {}

    public Connection getConnection() {
        Connection gob_connection;

        try {
            Class.forName("org.sqlite.JDBC");
            gob_connection = DriverManager.getConnection("jdbc:sqlite:" + DatabaseConnection.class.getClassLoader().getResource("database.db"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return gob_connection;
    }
}
