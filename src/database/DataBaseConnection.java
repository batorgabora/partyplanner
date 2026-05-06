package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {
  private static final String URL = "jdbc:postgresql://localhost:5432/partyplanner";
  private static final String USER = "postgres";
  private static final String PASSWORD = "baller";

  public static Connection getConnection() throws SQLException {
    Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
    conn.createStatement().execute("SET search_path TO party_planner");
    return conn;
  }
}