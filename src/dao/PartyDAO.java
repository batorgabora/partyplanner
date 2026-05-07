package dao;

import database.DataBaseConnection;
import model.Party;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PartyDAO {

  public Party getById(String partyid) {
    String sql = "SELECT * FROM party WHERE partyid = ?";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, partyid);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) return mapRow(rs);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return null;
  }

  public ArrayList<Party> getAll() {
    String sql = "SELECT * FROM party";
    ArrayList<Party> parties = new ArrayList<>();
    try (Connection conn = DataBaseConnection.getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql)) {
      while (rs.next()) parties.add(mapRow(rs));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return parties;
  }

  public ArrayList<Party> getByUser(String userid) {
    String sql = "SELECT p.* FROM party p JOIN partyusers pu ON p.partyid = pu.partyid WHERE pu.userid = ?";
    ArrayList<Party> parties = new ArrayList<>();
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userid);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) parties.add(mapRow(rs));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return parties;
  }

  public void create(String partyid, String name, String description, LocalDate date) {
    String sql = "INSERT INTO party (partyid, name, description, date) VALUES (?, ?, ?, ?)";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, partyid);
      ps.setString(2, name);
      ps.setString(3, description);
      ps.setDate(4, Date.valueOf(date));
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void update(String partyid, String name, String description, LocalDate date) {
    String sql = "UPDATE party SET name = ?, description = ?, date = ? WHERE partyid = ?";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, name);
      ps.setString(2, description);
      ps.setDate(3, Date.valueOf(date));
      ps.setString(4, partyid);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void delete(String partyid) {
    String sql = "DELETE FROM party WHERE partyid = ?";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, partyid);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  // TODO: update after Party model gains String partyid and LocalDate date fields
  private Party mapRow(ResultSet rs) throws SQLException {
    return new Party(rs.getString("name"), rs.getString("description"), null, null);
  }
}