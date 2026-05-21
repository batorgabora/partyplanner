package server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {
  private static final String URL = "jdbc:postgresql://ep-solitary-sunset-alixchh4-pooler.c-3.eu-central-1.aws.neon.tech/party_planner?sslmode=require&channel_binding=require";
  private static final String USER = "neondb_owner";
  private static final String PASSWORD = "npg_A6rYzvCW5Day";

  private static final DataBaseConnection instance = new DataBaseConnection();

  // each thread gets its own connection — no sharing between threads
  private static final ThreadLocal<Connection> threadLocalConnection = new ThreadLocal<>();

  private DataBaseConnection() {}

  public static DataBaseConnection getInstance() {
    return instance;
  }

  public Connection getConnection() throws SQLException {
    Connection conn = threadLocalConnection.get();
    if (conn == null || conn.isClosed()) {
      conn = DriverManager.getConnection(URL, USER, PASSWORD);
      conn.createStatement().execute("SET search_path TO party_planner");
      threadLocalConnection.set(conn);
    }
    return conn;
  }
}