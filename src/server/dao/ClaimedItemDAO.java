package server.dao;

import server.database.DataBaseConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ClaimedItemDAO {

  private static final Logger log = Logger.getLogger(ClaimedItemDAO.class.getName());

  public void claim(String userid, String itemid, int quantityClaimed) {
    String sql = "INSERT INTO party_planner.claimitem (userid, itemid, quantityclaimed) VALUES (?, ?, ?)";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userid);
      ps.setString(2, itemid);
      ps.setInt(3, quantityClaimed);
      ps.executeUpdate();
    } catch (SQLException e) {
      log.severe("claim failed for userid=" + userid + ", itemid=" + itemid + ": " + e.getMessage());
    }
  }

  public void unclaim(String userid, String itemid) {
    String sql = "DELETE FROM party_planner.claimitem WHERE userid = ? AND itemid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userid);
      ps.setString(2, itemid);
      ps.executeUpdate();
    } catch (SQLException e) {
      log.severe("unclaim failed for userid=" + userid + ", itemid=" + itemid + ": " + e.getMessage());
    }
  }

  public void updateClaim(String userid, String itemid, int quantityClaimed) {
    String sql = "UPDATE party_planner.claimitem SET quantityclaimed = ? WHERE userid = ? AND itemid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, quantityClaimed);
      ps.setString(2, userid);
      ps.setString(3, itemid);
      ps.executeUpdate();
    } catch (SQLException e) {
      log.severe("updateClaim failed for userid=" + userid + ", itemid=" + itemid + ": " + e.getMessage());
    }
  }

  public int getTotalClaimed(String itemid) {
    String sql = "SELECT COALESCE(SUM(quantityclaimed), 0) FROM party_planner.claimitem WHERE itemid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, itemid);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) return rs.getInt(1);
    } catch (SQLException e) {
      log.severe("getTotalClaimed failed for itemid=" + itemid + ": " + e.getMessage());
    }
    return 0;
  }

  public Map<String, Integer> getClaimsForItem(String itemid) {
    String sql = "SELECT userid, quantityclaimed FROM party_planner.claimitem WHERE itemid = ?";
    Map<String, Integer> claims = new HashMap<>();
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, itemid);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) claims.put(rs.getString("userid"), rs.getInt("quantityclaimed"));
    } catch (SQLException e) {
      log.severe("getClaimsForItem failed for itemid=" + itemid + ": " + e.getMessage());
    }
    return claims;
  }
}