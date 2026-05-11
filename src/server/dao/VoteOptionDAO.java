package server.dao;

import server.database.DataBaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VoteOptionDAO {

  public void addVote(int optionid, int userid) {
    String sql = "INSERT INTO voteoption (optionid, userid) VALUES (?, ?)";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, optionid);
      ps.setInt(2, userid);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void removeVote(int optionid, int userid) {
    String sql = "DELETE FROM voteoption WHERE optionid = ? AND userid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, optionid);
      ps.setInt(2, userid);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean hasVoted(int optionid, int userid) {
    String sql = "SELECT 1 FROM voteoption WHERE optionid = ? AND userid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, optionid);
      ps.setInt(2, userid);
      ResultSet rs = ps.executeQuery();
      return rs.next();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public int getVoteCount(int optionid) {
    String sql = "SELECT COUNT(*) FROM voteoption WHERE optionid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, optionid);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) return rs.getInt(1);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return 0;
  }

  public ArrayList<Integer> getVotersForOption(String optionId) {
    String sql = "SELECT userid FROM voteoption WHERE optionid = ?";
    List<Integer> voters = new ArrayList<>();
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, optionId);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) voters.add(rs.getInt("userid"));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return new ArrayList<>();
  }
}
