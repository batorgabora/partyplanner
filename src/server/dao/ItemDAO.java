package server.dao;

import server.database.DataBaseConnection;
import shared.model.Item;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

  public Item getById(String itemid) {
    String sql = "SELECT * FROM item WHERE itemid = ?";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, itemid);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) return mapRow(rs);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return null;
  }

  public List<Item> getByParty(String partyid) {
    System.out.println("Fetching items for partyid: " + partyid);
    String sql = "SELECT * FROM item WHERE partyid = ?";
    List<Item> items = new ArrayList<>();
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, partyid);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        Item i = mapRow(rs);
        System.out.println("Loaded item: " + i.getName());
        items.add(i);
      }
      System.out.println("Total items: " + items.size());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return items;
  }

  public void create(String itemid, String name, int quantity, String partyid) {
    String sql = "INSERT INTO item (itemid, name, quantity, partyid) VALUES (?, ?, ?, ?)";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, itemid);
      ps.setString(2, name);
      ps.setInt(3, quantity);
      ps.setString(4, partyid);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void update(String itemid, String name, int quantity) {
    String sql = "UPDATE item SET name = ?, quantity = ? WHERE itemid = ?";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, name);
      ps.setInt(2, quantity);
      ps.setString(3, itemid);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void delete(String itemid) {
    String deleteClaims = "DELETE FROM claimitem WHERE itemid = ?";
    String deleteItem = "DELETE FROM item WHERE itemid = ?";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps1 = conn.prepareStatement(deleteClaims);
        PreparedStatement ps2 = conn.prepareStatement(deleteItem)) {
      ps1.setString(1, itemid);
      ps1.executeUpdate();
      ps2.setString(1, itemid);
      ps2.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private Item mapRow(ResultSet rs) throws SQLException {
    return new Item(rs.getString("itemid"), rs.getString("name"));
  }
}
