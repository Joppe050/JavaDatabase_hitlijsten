package sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    public static Connection conn = null;

    public static void open() {
        if (conn!=null) {
            throw new Error("Database connection already opened.");
        }
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/hitdossier?user=root");
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new Error("Fout bij verbinden MySQL.");
        }
    }

    public static void close() {
        if (conn!=null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            conn = null;
        }
    }
}
