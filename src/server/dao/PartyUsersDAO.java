package server.dao;

import server.database.DataBaseConnection;
import shared.model.Participant;
import shared.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PartyUsersDAO {

  private static final Logger log = Logger.getLogger(PartyUsersDAO.class.getName());

  public void add(String userid, String partyid, String role) {
    String sql = "INSERT INTO party_planner.partyusers (userid, partyid, role, status) VALUES (?, ?, ?, null) ON CONFLICT (userid, partyid) DO UPDATE SET role = EXCLUDED.role, status = null";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userid);
      ps.setString(2, partyid);
      ps.setString(3, role);
      ps.executeUpdate();
    } catch (SQLException e) {
      log.severe("add failed for userid=" + userid + ", partyid=" + partyid + ": " + e.getMessage());
    }
  }

  public void remove(String userid, String partyid) {
    String deleteVotes  = "DELETE FROM party_planner.voteoption WHERE userid = ? AND optionid IN (SELECT optionid FROM party_planner.option WHERE partyid = ?)";
    String deleteClaims = "DELETE FROM party_planner.claimitem WHERE userid = ? AND itemid IN (SELECT itemid FROM party_planner.item WHERE partyid = ?)";
    String deleteUser   = "DELETE FROM party_planner.partyusers WHERE userid = ? AND partyid = ?";

    System.out.println("remove called for userid=" + userid + " partyid=" + partyid);

    try (Connection conn = DataBaseConnection.getInstance().getConnection()) {
      conn.setAutoCommit(false);
      try (
          PreparedStatement psVotes  = conn.prepareStatement(deleteVotes);
          PreparedStatement psClaims = conn.prepareStatement(deleteClaims);
          PreparedStatement psUser   = conn.prepareStatement(deleteUser)
      ) {
        psVotes.setString(1, userid);
        psVotes.setString(2, partyid);
        int votesDeleted = psVotes.executeUpdate();
        System.out.println("votes deleted: " + votesDeleted);

        psClaims.setString(1, userid);
        psClaims.setString(2, partyid);
        int claimsDeleted = psClaims.executeUpdate();
        System.out.println("claims updated: " + claimsDeleted);

        psUser.setString(1, userid);
        psUser.setString(2, partyid);
        int userDeleted = psUser.executeUpdate();
        System.out.println("partyusers deleted: " + userDeleted);

        conn.commit();
      } catch (SQLException e) {
        conn.rollback();
        System.out.println("SQL error: " + e.getMessage());
      }
    } catch (SQLException e) {
      System.out.println("Connection error: " + e.getMessage());
    }
  }

  public String getRole(String userid, String partyid) {
    String sql = "SELECT role FROM party_planner.partyusers WHERE userid = ? AND partyid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userid);
      ps.setString(2, partyid);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) return rs.getString("role");
    } catch (SQLException e) {
      log.severe("getRole failed for userid=" + userid + ", partyid=" + partyid + ": " + e.getMessage());
    }
    return null;
  }

  public boolean isMember(String userid, String partyid) {
    String sql = "SELECT 1 FROM party_planner.partyusers WHERE userid = ? AND partyid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userid);
      ps.setString(2, partyid);
      ResultSet rs = ps.executeQuery();
      return rs.next();
    } catch (SQLException e) {
      log.severe("isMember failed for userid=" + userid + ", partyid=" + partyid + ": " + e.getMessage());
    }
    return false;
  }

  public List<Participant> getParticipantsByParty(String partyid) {
    String sql = "SELECT u.*, pu.role FROM party_planner.\"user\" u JOIN party_planner.partyusers pu ON u.userid = pu.userid WHERE pu.partyid = ? AND (pu.status != 'declined' OR pu.status IS NULL)";
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
        participants.add(new Participant(null, user));
      }
    } catch (SQLException e) {
      log.severe("getParticipantsByParty failed for partyid=" + partyid + ": " + e.getMessage());
    }
    return participants;
  }

  public void removeByParty(String partyid) {
    String sql = "DELETE FROM party_planner.partyusers WHERE partyid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, partyid);
      ps.executeUpdate();
    } catch (SQLException e) {
      log.severe("removeByParty failed for partyId=" + partyid);
    }
  }

  public void updateStatus(String userid, String partyid, String status) {
    String sql = "UPDATE party_planner.partyusers SET status = ? WHERE userid = ? AND partyid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, status);
      ps.setString(2, userid);
      ps.setString(3, partyid);
      ps.executeUpdate();
    } catch (SQLException e) {
      log.severe("updateStatus failed for userId=" + userid + " partyId=" + partyid + " and status=" + status);
    }
  }

  public String getStatus(String userid, String partyid) {
    String sql = "SELECT status FROM party_planner.partyusers WHERE userid = ? AND partyid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userid);
      ps.setString(2, partyid);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) return rs.getString("status");
    } catch (SQLException e) {
      log.severe("getStatus failed for userId=" + userid + " partyId=" + partyid);
    }
    return null; // not null was returned "" before
  }

  public List<String> getOrganizerIds(String partyid) {
    String sql = "SELECT userid FROM party_planner.partyusers WHERE partyid = ? AND role = 'organizer'";
    List<String> ids = new ArrayList<>();
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, partyid);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) ids.add(rs.getString("userid"));
    } catch (SQLException e) {
      log.severe("getOrganizerIds failed for partyid=" + partyid + ": " + e.getMessage());
    }
    return ids;
  }
}