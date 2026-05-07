package dao;

import database.DataBaseConnection;
import model.Item;
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
    String sql = "SELECT * FROM item WHERE partyid = ?";
    List<Item> items = new ArrayList<>();
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, partyid);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) items.add(mapRow(rs));
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
    String sql = "DELETE FROM item WHERE itemid = ?";
    try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, itemid);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private Item mapRow(ResultSet rs) throws SQLException {
    return null;//new Item(rs.getString("itemid"), rs.getString("name"));
  }
}