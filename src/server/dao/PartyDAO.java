package server.dao;

import server.database.DataBaseConnection;
import shared.model.Organizer;
import shared.model.Party;
import shared.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PartyDAO {

  private static final Logger log = Logger.getLogger(PartyDAO.class.getName());

  public Party getById(String partyid) {
    String sql = "SELECT * FROM party_planner.party WHERE partyid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, partyid);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) return mapRow(rs);
    } catch (SQLException e) {
      log.severe("getById failed for partyid=" + partyid + ": " + e.getMessage());
    }
    return null;
  }

  public ArrayList<Party> getAll() {
    String sql = "SELECT * FROM party_planner.party";
    ArrayList<Party> parties = new ArrayList<>();
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql)) {
      while (rs.next()) parties.add(mapRow(rs));
    } catch (SQLException e) {
      log.severe("getAll failed: " + e.getMessage());
    }
    return parties;
  }

  public ArrayList<Party> getAcceptedByUser(String userid) {
    String sql = "SELECT p.* FROM party_planner.party p JOIN party_planner.partyusers pu ON p.partyid = pu.partyid " +
        "WHERE pu.userid = ? AND (pu.status = 'accepted' OR pu.role = 'organizer')";
    ArrayList<Party> parties = new ArrayList<>();
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userid);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        Party p = mapRow(rs);
        System.out.println("Loaded accepted party: " + p.getName());
        parties.add(p);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return parties;
  }

  public ArrayList<Party> getInvitedByUser(String userid) {
    String sql = "SELECT p.* FROM party_planner.party p JOIN party_planner.partyusers pu ON p.partyid = pu.partyid " +
        "WHERE pu.userid = ? AND pu.status IS NULL AND pu.role = 'participant'";
    ArrayList<Party> parties = new ArrayList<>();
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userid);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        Party p = mapRow(rs);
        System.out.println("Loaded invite: " + p.getName());
        parties.add(p);
      }
    } catch (SQLException e) {
      log.severe("getByUser failed for userid=" + userid + ": " + e.getMessage());
    }
    return parties;
  }

  public void create(String partyid, String name, String description, String location, LocalDate date) {
    String insertParty  = "INSERT INTO party_planner.party (partyid, name, description, location, date) VALUES (?, ?, ?, ?, ?)";
    String insertOption = "INSERT INTO party_planner.option (optionid, proposal, partyid) VALUES (?, ?, ?)";

    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement psParty  = conn.prepareStatement(insertParty);
        PreparedStatement psOption = conn.prepareStatement(insertOption)) {

      psParty.setString(1, partyid);
      psParty.setString(2, name);
      psParty.setString(3, description);
      psParty.setString(4, location);
      psParty.setDate(5, Date.valueOf(date));
      psParty.executeUpdate();

      psOption.setString(1, "opt-" + java.util.UUID.randomUUID().toString());
      psOption.setString(2, date.toString());
      psOption.setString(3, partyid);
      psOption.executeUpdate();

    } catch (SQLException e) {
      log.severe("create failed for partyid=" + partyid + ": " + e.getMessage());
    }
  }

  public void update(String partyid, String name, String description, String location) {
    String sql = "UPDATE party_planner.party SET name = ?, description = ?, location = ? WHERE partyid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, name);
      ps.setString(2, description);
      ps.setString(3, location);
      ps.setString(4, partyid);
      ps.executeUpdate();
    } catch (SQLException e) {
      log.severe("update failed for partyid=" + partyid + ": " + e.getMessage());
    }
  }

  public void updateDate(String partyid, String date) {
    String sql = "UPDATE party_planner.party SET date = ? WHERE partyid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      try {
        ps.setDate(1, Date.valueOf(LocalDate.parse(date)));
      } catch (Exception e) {
        System.out.println("wrong time format");
        return;
      }
      ps.setString(2, partyid);
      ps.executeUpdate();
    } catch (SQLException e) {
      log.severe("updateDate failed for partyid=" + partyid + ": " + e.getMessage());
    }
  }

  public void delete(String partyid) {
    try (Connection conn = DataBaseConnection.getInstance().getConnection()) {
      // delete votes on options belonging to this party
      try (PreparedStatement ps = conn.prepareStatement(
          "DELETE FROM party_planner.voteoption WHERE optionid IN (SELECT optionid FROM \"option\" WHERE partyid = ?)")) {
        ps.setString(1, partyid);
        ps.executeUpdate();
      }
      // delete options
      try (PreparedStatement ps = conn.prepareStatement(
          "DELETE FROM party_planner.\"option\" WHERE partyid = ?")) {
        ps.setString(1, partyid);
        ps.executeUpdate();
      }
      // delete claims on items belonging to this party
      try (PreparedStatement ps = conn.prepareStatement(
          "DELETE FROM party_planner.claimitem WHERE itemid IN (SELECT itemid FROM item WHERE partyid = ?)")) {
        ps.setString(1, partyid);
        ps.executeUpdate();
      }
      // delete items
      try (PreparedStatement ps = conn.prepareStatement(
          "DELETE FROM party_planner.item WHERE partyid = ?")) {
        ps.setString(1, partyid);
        ps.executeUpdate();
      }
      // delete partyusers
      try (PreparedStatement ps = conn.prepareStatement(
          "DELETE FROM party_planner.partyusers WHERE partyid = ?")) {
        ps.setString(1, partyid);
        ps.executeUpdate();
      }
      // finally delete the party
      try (PreparedStatement ps = conn.prepareStatement(
          "DELETE FROM party_planner.party WHERE partyid = ?")) {
        ps.setString(1, partyid);
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      log.severe("delete failed for partyid=" + partyid + ": " + e.getMessage());
    }
  }


  private Party mapRow(ResultSet rs) throws SQLException {
    String partyId = rs.getString("partyid");
    String name = rs.getString("name");
    String description = rs.getString("description");
    String location = rs.getString("location");
    LocalDate date = rs.getDate("date") != null ? rs.getDate("date").toLocalDate() : null;
    User organizer = getOrganizerForParty(partyId);
    return new Party(partyId, name, description, location, date, organizer);
  }

  private User getOrganizerForParty(String partyId) {
    String sql = "SELECT userid FROM party_planner.partyusers WHERE partyid = ? AND role = 'organizer'";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, partyId);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        return new UserDAO().getById(rs.getString("userid"));
      }
    } catch (SQLException e) {
      log.severe("getOrganizerForParty failed for partyId=" + partyId + ": " + e.getMessage());
    }
    return null;
  }
}