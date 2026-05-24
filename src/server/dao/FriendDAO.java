package server.dao;

import server.database.DataBaseConnection;
import shared.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FriendDAO {

  private static final Logger log = Logger.getLogger(FriendDAO.class.getName());

  public void addFriend(String userId, String friendId) {
    String sql = "INSERT INTO party_planner.friends (userid, friendid) VALUES (?, ?) ON CONFLICT DO NOTHING";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userId);
      ps.setString(2, friendId);
      ps.executeUpdate();
    } catch (SQLException e) {
      log.severe("addFriend failed for userId=" + userId + ", friendId=" + friendId + ": " + e.getMessage());
    }
  }

  public void removeFriend(String userId, String friendId) {
    String sql = "DELETE FROM party_planner.friends WHERE userid = ? AND friendid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userId);
      ps.setString(2, friendId);
      ps.executeUpdate();
    } catch (SQLException e) {
      log.severe("removeFriend failed for userId=" + userId + ", friendId=" + friendId + ": " + e.getMessage());
    }
  }

  public List<User> getFriends(String userId) {
    String sql = """
        SELECT u.* FROM party_planner."user" u
        JOIN party_planner.friends f ON u.userid = f.friendid
        WHERE f.userid = ?
    """;
    List<User> friends = new ArrayList<>();
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userId);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        friends.add(new User(
            rs.getString("userid"),
            rs.getString("username"),
            rs.getString("hashpass"),
            rs.getString("mail")
        ));
      }
    } catch (SQLException e) {
      log.severe("getFriends failed for userId=" + userId + ": " + e.getMessage());
    }
    return friends;
  }

  public List<User> getNonFriends(String userId) {
    String sql = """
        SELECT u.* FROM party_planner."user" u
        WHERE u.userid != ?
        AND u.userid NOT IN (
            SELECT friendid FROM party_planner.friends WHERE userid = ?
        )
    """;
    List<User> nonFriends = new ArrayList<>();
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userId);
      ps.setString(2, userId);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        nonFriends.add(new User(
            rs.getString("userid"),
            rs.getString("username"),
            rs.getString("hashpass"),
            rs.getString("mail")
        ));
      }
    } catch (SQLException e) {
      log.severe("getNonFriends failed for userId=" + userId + ": " + e.getMessage());
    }
    return nonFriends;
  }

  public boolean isFollowing(String userId, String friendId) {
    String sql = "SELECT 1 FROM party_planner.friends WHERE userid = ? AND friendid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userId);
      ps.setString(2, friendId);
      return ps.executeQuery().next();
    } catch (SQLException e) {
      log.severe("isFollowing failed for userId=" + userId + ", friendId=" + friendId + ": " + e.getMessage());
      return false;
    }
  }
}