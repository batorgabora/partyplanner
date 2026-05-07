package dao;

import database.DataBaseConnection;
import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartyUsersDAO {

  public void add(String userid, String partyid, String role) {
    String sql = "INSERT INTO partyusers (userid, partyid, role) VALUES (?, ?, ?)";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userid);
      ps.setString(2, partyid);
      ps.setString(3, role);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void remove(String userid, String partyid) {
    String sql = "DELETE FROM partyusers WHERE userid = ? AND partyid = ?";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userid);
      ps.setString(2, partyid);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public String getRole(String userid, String partyid) {
    String sql = "SELECT role FROM partyusers WHERE userid = ? AND partyid = ?";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userid);
      ps.setString(2, partyid);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) return rs.getString("role");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return null;
  }

  public boolean isMember(String userid, String partyid) {
    String sql = "SELECT 1 FROM partyusers WHERE userid = ? AND partyid = ?";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userid);
      ps.setString(2, partyid);
      ResultSet rs = ps.executeQuery();
      return rs.next();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public List<User> getUsersInParty(String partyid) {
    String sql = "SELECT u.* FROM \"user\" u JOIN partyusers pu ON u.userid = pu.userid WHERE pu.partyid = ?";
    List<User> users = new ArrayList<>();
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, partyid);
      ResultSet rs = ps.executeQuery();
      // TODO: update after User model gains String userid and String mail fields
      //while (rs.next()) users.add(new User(rs.getString("username"), rs.getString("hashpass")));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return users;
  }

  public List<String> getOrganizerIds(String partyid) {
    String sql = "SELECT userid FROM partyusers WHERE partyid = ? AND role = 'organizer'";
    List<String> ids = new ArrayList<>();
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, partyid);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) ids.add(rs.getString("userid"));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return ids;
  }
}