package server.dao;

import server.database.DataBaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class VoteOptionDAO {

  private static final Logger log = Logger.getLogger(VoteOptionDAO.class.getName());

  public void addVote(int optionid, int userid) {
    String sql = "INSERT INTO party_planner.voteoption (optionid, userid) VALUES (?, ?)";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, optionid);
      ps.setInt(2, userid);
      ps.executeUpdate();
    } catch (SQLException e) {
      log.severe("addVote failed for optionid=" + optionid + ", userid=" + userid + ": " + e.getMessage());
    }
  }

  public void removeVote(int optionid, int userid) {
    String sql = "DELETE FROM party_planner.voteoption WHERE optionid = ? AND userid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, optionid);
      ps.setInt(2, userid);
      ps.executeUpdate();
    } catch (SQLException e) {
      log.severe("removeVote failed for optionid=" + optionid + ", userid=" + userid + ": " + e.getMessage());
    }
  }

  public boolean hasVoted(int optionid, int userid) {
    String sql = "SELECT 1 FROM party_planner.voteoption WHERE optionid = ? AND userid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, optionid);
      ps.setInt(2, userid);
      ResultSet rs = ps.executeQuery();
      return rs.next();
    } catch (SQLException e) {
      log.severe("hasVoted failed for optionid=" + optionid + ", userid=" + userid + ": " + e.getMessage());
    }
    return false;
  }

  public int getVoteCount(int optionid) {
    String sql = "SELECT COUNT(*) FROM party_planner.voteoption WHERE optionid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, optionid);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) return rs.getInt(1);
    } catch (SQLException e) {
      log.severe("getVoteCount failed for optionid=" + optionid + ": " + e.getMessage());
    }
    return 0;
  }

  public ArrayList<Integer> getVotersForOption(String optionId) {
    String sql = "SELECT userid FROM party_planner.voteoption WHERE optionid = ?";
    ArrayList<Integer> voters = new ArrayList<>();
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, optionId);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) voters.add(rs.getInt("userid"));
    } catch (SQLException e) {
      log.severe("getVotersForOption failed for optionId=" + optionId + ": " + e.getMessage());
    }
    return voters;
  }
}