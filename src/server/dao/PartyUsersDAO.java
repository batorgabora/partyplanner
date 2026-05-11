package server.dao;

import server.database.DataBaseConnection;
import shared.model.Participant;
import shared.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartyUsersDAO {

  public void add(String userid, String partyid, String role) {
    String sql = "INSERT INTO partyusers (userid, partyid, role) VALUES (?, ?, ?)";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
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
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
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
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
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
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userid);
      ps.setString(2, partyid);
      ResultSet rs = ps.executeQuery();
      return rs.next();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public List<Participant> getParticipantsByParty(String partyid) {
    String sql = "SELECT u.*, pu.role FROM \"user\" u JOIN partyusers pu ON u.userid = pu.userid WHERE pu.partyid = ?";
    List<Participant> participants = new ArrayList<>();
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, partyid);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        User user = new User(
            rs.getString("userid"),
            rs.getString("username"),
            rs.getString("hashpass"),
            rs.getString("mail")
        );
        Participant p = new Participant(null, user); // party is null to avoid circular loading
        participants.add(p);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return participants;
  }

  public void removeByParty(String partyid) {
    String sql = "DELETE FROM partyusers WHERE partyid = ?";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, partyid);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public List<String> getOrganizerIds(String partyid) {
    String sql = "SELECT userid FROM partyusers WHERE partyid = ? AND role = 'organizer'";
    List<String> ids = new ArrayList<>();
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
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
