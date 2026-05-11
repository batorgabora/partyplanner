package server.dao;

import server.database.DataBaseConnection;
import shared.model.Option;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OptionDAO {

  public Option getById(String optionid) {
    String sql = "SELECT * FROM \"option\" WHERE optionid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, optionid);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) return mapRow(rs);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return null;
  }

  public List<Option> getByParty(String partyid) {
    String sql = "SELECT * FROM \"option\" WHERE partyid = ?";
    List<Option> options = new ArrayList<>();
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, partyid);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) options.add(mapRow(rs));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return options;
  }

  public void create(String optionid, String proposal, String partyid) {
    String sql = "INSERT INTO \"option\" (optionid, proposal, partyid) VALUES (?, ?, ?)";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, optionid);
      ps.setString(2, proposal);
      ps.setString(3, partyid);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void delete(String optionid) {
    String deleteVotes = "DELETE FROM voteoption WHERE optionid = ?";
    String deleteOption = "DELETE FROM \"option\" WHERE optionid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps1 = conn.prepareStatement(deleteVotes);
        PreparedStatement ps2 = conn.prepareStatement(deleteOption)) {
      ps1.setString(1, optionid);
      ps1.executeUpdate();
      ps2.setString(1, optionid);
      ps2.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private Option mapRow(ResultSet rs) throws SQLException {
    return new Option(rs.getString("optionid"), rs.getString("proposal"), rs.getString("partyid"));
  }
}
