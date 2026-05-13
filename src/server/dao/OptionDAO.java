package server.dao;

import server.database.DataBaseConnection;
import shared.model.Option;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class OptionDAO {

  private static final Logger log = Logger.getLogger(OptionDAO.class.getName());

  public Option getById(String optionid) {
    String sql = "SELECT * FROM \"option\" WHERE optionid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, optionid);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) return mapRow(rs);
    } catch (SQLException e) {
      log.severe("getById failed for optionid=" + optionid + ": " + e.getMessage());
    }
    return null;
  }

  public List<Option> getByParty(String partyId) {
    String sql = """
        SELECT o.optionid, o.proposal, o.partyid, COUNT(v.userid) as votecount
        FROM "option" o
        LEFT JOIN voteoption v ON o.optionid = v.optionid
        WHERE o.partyid = ?
        GROUP BY o.optionid, o.proposal, o.partyid
        ORDER BY votecount DESC
    """;
    List<Option> options = new ArrayList<>();
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, partyId);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        options.add(new Option(
            rs.getString("optionid"),
            rs.getString("proposal"),
            rs.getString("partyid"),
            rs.getInt("votecount")
        ));
      }
    } catch (SQLException e) {
      log.severe("getByParty failed: " + e.getMessage());
    }
    return options;
  }

  public void vote(String optionId, String userId) {
    String sql = "INSERT INTO voteoption (optionid, userid) VALUES (?, ?) ON CONFLICT DO NOTHING";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, optionId);
      ps.setString(2, userId);
      ps.executeUpdate();
    } catch (SQLException e) {
      log.severe("vote failed: " + e.getMessage());
    }
  }

  public void removeVote(String optionId, String userId) {
    String sql = "DELETE FROM voteoption WHERE optionid = ? AND userid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, optionId);
      ps.setString(2, userId);
      ps.executeUpdate();
    } catch (SQLException e) {
      log.severe("removeVote failed: " + e.getMessage());
    }
  }

  public boolean hasVoted(String userId, String partyId) {
    String sql = """
        SELECT COUNT(*) FROM voteoption v
        JOIN "option" o ON v.optionid = o.optionid
        WHERE v.userid = ? AND o.partyid = ?
    """;
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userId);
      ps.setString(2, partyId);
      ResultSet rs = ps.executeQuery();
      return rs.next() && rs.getInt(1) > 0;
    } catch (SQLException e) {
      log.severe("hasVoted failed: " + e.getMessage());
      return false;
    }
  }

  public String getTopVoted(String partyId) {
    String sql = """
        SELECT o.proposal
        FROM "option" o
        LEFT JOIN voteoption v ON o.optionid = v.optionid
        WHERE o.partyid = ?
        GROUP BY o.optionid, o.proposal
        ORDER BY COUNT(v.userid) DESC
        LIMIT 1
    """;
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, partyId);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) return rs.getString("proposal");
    } catch (SQLException e) {
      log.severe("getTopVoted failed: " + e.getMessage());
    }
    return null;
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
      log.severe("create failed for optionid=" + optionid + ": " + e.getMessage());
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
      log.severe("delete failed for optionid=" + optionid + ": " + e.getMessage());
    }
  }

  private Option mapRow(ResultSet rs) throws SQLException {
    return new Option(rs.getString("optionid"), rs.getString("proposal"), rs.getString("partyid"));
  }
}