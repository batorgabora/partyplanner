package server.dao;

import server.database.DataBaseConnection;
import shared.model.Item;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ItemDAO {

  private static final Logger log = Logger.getLogger(ItemDAO.class.getName());

  public Item getById(String itemid) {
    String sql = "SELECT * FROM party_planner.item WHERE itemid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, itemid);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) return mapRow(rs);
    } catch (SQLException e) {
      log.severe("getById failed for itemid=" + itemid + ": " + e.getMessage());
    }
    return null;
  }

  public List<Item> getByParty(String partyid) {
    String sql = "SELECT i.*, c.userid AS claimer_id, u.username AS claimer_name " +
        "FROM party_planner.item i " +
        "LEFT JOIN party_planner.claimitem c ON i.itemid = c.itemid " +
        "LEFT JOIN party_planner.\"user\" u ON c.userid = u.userid " +
        "WHERE i.partyid = ?";
    List<Item> items = new ArrayList<>();
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, partyid);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) items.add(mapRow(rs));
    } catch (SQLException e) {
      log.severe("getByParty failed for partyid=" + partyid + ": " + e.getMessage());
    }
    return items;
  }

  public void create(String itemid, String name, int quantity, String partyid) {
    String sql = "INSERT INTO party_planner.item (itemid, name, quantity, partyid) VALUES (?, ?, ?, ?)";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, itemid);
      ps.setString(2, name);
      ps.setInt(3, quantity);
      ps.setString(4, partyid);
      ps.executeUpdate();
    } catch (SQLException e) {
      log.severe("create failed for itemid=" + itemid + ": " + e.getMessage());
    }
  }

  public void update(String itemid, String name, int quantity) {
    String sql = "UPDATE party_planner.item SET name = ?, quantity = ? WHERE itemid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, name);
      ps.setInt(2, quantity);
      ps.setString(3, itemid);
      ps.executeUpdate();
    } catch (SQLException e) {
      log.severe("update failed for itemid=" + itemid + ": " + e.getMessage());
    }
  }

  public void delete(String itemid) {
    String deleteClaims = "DELETE FROM party_planner.claimitem WHERE itemid = ?";
    String deleteItem   = "DELETE FROM party_planner.item WHERE itemid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps1 = conn.prepareStatement(deleteClaims);
        PreparedStatement ps2 = conn.prepareStatement(deleteItem)) {
      ps1.setString(1, itemid);
      ps1.executeUpdate();
      ps2.setString(1, itemid);
      ps2.executeUpdate();
    } catch (SQLException e) {
      log.severe("delete failed for itemid=" + itemid + ": " + e.getMessage());
    }
  }

  public void claimItem(String itemid, String userid) {
    String delete = "DELETE FROM party_planner.claimitem WHERE itemid = ?";
    String insert = "INSERT INTO party_planner.claimitem (itemid, userid, quantityclaimed) VALUES (?, ?, 1)";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps1 = conn.prepareStatement(delete);
        PreparedStatement ps2 = conn.prepareStatement(insert)) {
      ps1.setString(1, itemid);
      ps1.executeUpdate();
      ps2.setString(1, itemid);
      ps2.setString(2, userid);
      ps2.executeUpdate();
    } catch (SQLException e) {
      log.severe("claimItem failed for itemid=" + itemid + ": " + e.getMessage());
    }
  }

  public void unclaimItem(String itemid) {
    String sql = "DELETE FROM party_planner.claimitem WHERE itemid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, itemid);
      ps.executeUpdate();
    } catch (SQLException e) {
      log.severe("unclaimItem failed for itemid=" + itemid + ": " + e.getMessage());
    }
  }

  private Item mapRow(ResultSet rs) throws SQLException {
    Item item = new Item(rs.getString("itemid"), rs.getString("name"));
    String claimerName = rs.getString("claimer_name");
    if (claimerName != null) item.claim(claimerName);
    return item;
  }
}