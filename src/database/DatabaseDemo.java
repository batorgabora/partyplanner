package database;

import java.sql.*;

public class DatabaseDemo {

  public static void main(String[] args) throws SQLException {

    Connection conn = DataBaseConnection.getConnection();
    System.out.println("Connected!");

    PreparedStatement insert = conn.prepareStatement(
        "INSERT INTO \"user\" (username, mail, hashpass) VALUES (?, ?, ?)"
    );
    insert.setString(1, "testuser");
    insert.setString(2, "test@test.com");
    insert.setString(3, "password123");
    insert.executeUpdate();
    System.out.println("User inserted!");

    Statement select = conn.createStatement();
    ResultSet rs = select.executeQuery("SELECT * FROM \"user\"");
    System.out.println("--- Users in database ---");
    while (rs.next()) {
      System.out.println("ID: " + rs.getInt("userid") +
          " | Username: " + rs.getString("username") +
          " | Mail: " + rs.getString("mail"));
    }

    conn.close();
  }
}