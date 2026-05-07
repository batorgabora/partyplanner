package dao;

import database.DataBaseConnection;
import model.Party;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PartyDAO {

  public Party getById(int partyid) {
    String sql = "SELECT * FROM party WHERE partyid = ?";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, partyid);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) return mapRow(rs);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return null;
  }

  public List<Party> getAll() {
    String sql = "SELECT * FROM party";
    List<Party> parties = new ArrayList<>();
    try (Connection conn = DataBaseConnection.getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql)) {
      while (rs.next()) parties.add(mapRow(rs));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return parties;
  }

  public List<Party> getByUser(int userid) {
    String sql = "SELECT p.* FROM party p JOIN partyusers pu ON p.partyid = pu.partyid WHERE pu.userid = ?";
    List<Party> parties = new ArrayList<>();
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, userid);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) parties.add(mapRow(rs));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return parties;
  }

  public int create(String name, String description, LocalDate date) {
    String sql = "INSERT INTO party (name, description, date) VALUES (?, ?, ?) RETURNING partyid";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, name);
      ps.setString(2, description);
      ps.setDate(3, Date.valueOf(date));
      ResultSet rs = ps.executeQuery();
      if (rs.next()) return rs.getInt(1);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    throw new RuntimeException("Failed to create party");
  }

  public void update(int partyid, String name, String description, LocalDate date) {
    String sql = "UPDATE party SET name = ?, description = ?, date = ? WHERE partyid = ?";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, name);
      ps.setString(2, description);
      ps.setDate(3, Date.valueOf(date));
      ps.setInt(4, partyid);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void delete(int partyid) {
    String sql = "DELETE FROM party WHERE partyid = ?";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, partyid);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private Party mapRow(ResultSet rs) throws SQLException {
    return new Party(rs.getString("name"), rs.getString("description"), null, null);
  }
}