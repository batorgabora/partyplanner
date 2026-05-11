package server.dao;

import server.database.DataBaseConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ClaimedItemDAO {

  public void claim(String userid, String itemid, int quantityClaimed) {
    String sql = "INSERT INTO claimitem (userid, itemid, quantityclaimed) VALUES (?, ?, ?)";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userid);
      ps.setString(2, itemid);
      ps.setInt(3, quantityClaimed);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void unclaim(String userid, String itemid) {
    String sql = "DELETE FROM claimitem WHERE userid = ? AND itemid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userid);
      ps.setString(2, itemid);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void updateClaim(String userid, String itemid, int quantityClaimed) {
    String sql = "UPDATE claimitem SET quantityclaimed = ? WHERE userid = ? AND itemid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, quantityClaimed);
      ps.setString(2, userid);
      ps.setString(3, itemid);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public int getTotalClaimed(String itemid) {
    String sql = "SELECT COALESCE(SUM(quantityclaimed), 0) FROM claimitem WHERE itemid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, itemid);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) return rs.getInt(1);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return 0;
  }

  public Map<String, Integer> getClaimsForItem(String itemid) {
    String sql = "SELECT userid, quantityclaimed FROM claimitem WHERE itemid = ?";
    Map<String, Integer> claims = new HashMap<>();
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, itemid);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) claims.put(rs.getString("userid"), rs.getInt("quantityclaimed"));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return claims;
  }
}
