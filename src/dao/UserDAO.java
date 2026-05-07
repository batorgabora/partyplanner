package dao;

import database.DataBaseConnection;
import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

  public User getById(int userid) {
    String sql = "SELECT * FROM \"user\" WHERE userid = ?";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, userid);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) return mapRow(rs);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return null;
  }

  public User getByUsername(String username) {
    String sql = "SELECT * FROM \"user\" WHERE username = ?";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, username);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) return mapRow(rs);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return null;
  }

  public List<User> getAll() {
    String sql = "SELECT * FROM \"user\"";
    List<User> users = new ArrayList<>();
    try (Connection conn = DataBaseConnection.getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql)) {
      while (rs.next()) users.add(mapRow(rs));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return users;
  }

  public int create(String username, String mail, String hashpass) {
    String sql = "INSERT INTO \"user\" (username, mail, hashpass) VALUES (?, ?, ?) RETURNING userid";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, username);
      ps.setString(2, mail);
      ps.setString(3, hashpass);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) return rs.getInt(1);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    throw new RuntimeException("Failed to create user");
  }

  public void update(int userid, String username, String mail) {
    String sql = "UPDATE \"user\" SET username = ?, mail = ? WHERE userid = ?";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, username);
      ps.setString(2, mail);
      ps.setInt(3, userid);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void updatePassword(int userid, String hashpass) {
    String sql = "UPDATE \"user\" SET hashpass = ? WHERE userid = ?";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, hashpass);
      ps.setInt(2, userid);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void delete(int userid) {
    String sql = "DELETE FROM \"user\" WHERE userid = ?";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, userid);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public User login(String username, String hashpass) {
    String sql = "SELECT * FROM \"user\" WHERE username = ? AND hashpass = ?";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, username);
      ps.setString(2, hashpass);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) return mapRow(rs);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return null;
  }

  private User mapRow(ResultSet rs) throws SQLException {
    return new User(rs.getString("username"), rs.getString("hashpass"));
  }
}
