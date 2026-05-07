package dao;

import database.DataBaseConnection;
import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDAO {

  public User getById(String userid) {
    String sql = "SELECT * FROM \"user\" WHERE userid = ?";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userid);
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

  public ArrayList<User> getAll() {
    String sql = "SELECT * FROM \"user\"";
    ArrayList<User> users = new ArrayList<>();
    try (Connection conn = DataBaseConnection.getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql)) {
      while (rs.next()) users.add(mapRow(rs));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return users;
  }

  public String create(String userid, String username, String mail, String hashpass) {
    String sql = "INSERT INTO \"user\" (userid, username, mail, hashpass) VALUES (?, ?, ?, ?)";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userid);
      ps.setString(2, username);
      ps.setString(3, mail);
      ps.setString(4, hashpass);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) return rs.getString(1);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return null;
  }

  public void update(String username, String mail, String hashpass) {
    String userid = getByUsername(username).getId();
    String sql = "UPDATE \"user\" SET username = ?, mail = ?, hashpass = ? WHERE userid = ?";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, username);
      ps.setString(2, mail);
      ps.setString(3, hashpass);
      ps.setString(4, userid);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void updatePassword(String userid, String hashpass) {
    String sql = "UPDATE \"user\" SET hashpass = ? WHERE userid = ?";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, hashpass);
      ps.setString(2, userid);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void delete(String userid) {
    String sql = "DELETE FROM \"user\" WHERE userid = ?";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userid);
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
    return new User(rs.getString("userid"), rs.getString("username"),rs.getString("mail"), rs.getString("hashpass"));
  }
}
