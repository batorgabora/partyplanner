package server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {
  private static final String URL = "jdbc:postgresql://ep-solitary-sunset-alixchh4-pooler.c-3.eu-central-1.aws.neon.tech/party_planner?sslmode=require&channel_binding=require";
  private static final String USER = "neondb_owner";
  private static final String PASSWORD = "npg_A6rYzvCW5Day";

  private static DataBaseConnection instance;
  private Connection connection;

  private DataBaseConnection() {
    try {
      connection = DriverManager.getConnection(URL, USER, PASSWORD);
      connection.createStatement().execute("SET search_path TO party_planner");
    } catch (SQLException e) {
      throw new RuntimeException("Could not connect to database.", e);
    }
  }

  public static synchronized DataBaseConnection getInstance() {
    if (instance == null) {
      instance = new DataBaseConnection();
    }
    return instance;
  }

  public Connection getConnection() {
    try {
      if (connection == null || connection.isClosed()) {
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
        connection.createStatement().execute("SET search_path TO party_planner");
      }
    } catch (SQLException e)
    {
      throw new RuntimeException("Could not reconnect to database.", e);
    }
    return connection;
  }
}