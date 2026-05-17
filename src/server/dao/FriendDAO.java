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
    // one INSERT statement, executed twice with swapped parameters.
    // first insert: A -> B, second insert: B -> A.
    // this gives us the two-row approach so both users see each other as friends.
    // ON CONFLICT DO NOTHING means if they're already friends it silently skips.
    String sql = "INSERT INTO friends (userid, friendid) VALUES (?, ?) ON CONFLICT DO NOTHING";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userId);   // A -> B
      ps.setString(2, friendId);
      ps.executeUpdate();
      ps.setString(1, friendId); // B -> A (same statement, swapped args)
      ps.setString(2, userId);
      ps.executeUpdate();
    } catch (SQLException e) {
      log.severe("addFriend failed for userId=" + userId + ", friendId=" + friendId + ": " + e.getMessage());
    }
  }

  public void removeFriend(String userId, String friendId) {
    // deletes both rows in one query using OR.
    // without this both directions would remain and the friendship would still show up.
    String sql = "DELETE FROM friends WHERE (userid = ? AND friendid = ?) OR (userid = ? AND friendid = ?)";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userId);   // A -> B
      ps.setString(2, friendId);
      ps.setString(3, friendId); // B -> A
      ps.setString(4, userId);
      ps.executeUpdate();
    } catch (SQLException e) {
      log.severe("removeFriend failed for userId=" + userId + ", friendId=" + friendId + ": " + e.getMessage());
    }
  }

  public List<User> getFriends(String userId) {
    // joins "user" with friends on friendid so we get the full user data
    // of everyone who is a friend of the given user.
    // because of the two-row approach, WHERE f.userid = ? is enough —
    // no need to check the other direction.
    String sql = """
            SELECT u.* FROM "user" u
            JOIN friends f ON u.userid = f.friendid
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
    // gets everyone who is NOT already a friend and is not the user themselves.
    // used to populate the "add friend" list in the UI.
    // subquery fetches all current friendids for the user,
    // then the outer query excludes them.
    String sql = """
            SELECT u.* FROM "user" u
            WHERE u.userid != ?
            AND u.userid NOT IN (
                SELECT friendid FROM friends WHERE userid = ?
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

  public boolean areFriends(String userId, String friendId) {
    // only needs to check one direction because of the two-row approach —
    // if A -> B exists, B -> A also exists by definition.
    String sql = "SELECT 1 FROM friends WHERE userid = ? AND friendid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, userId);
      ps.setString(2, friendId);
      return ps.executeQuery().next();
    } catch (SQLException e) {
      log.severe("areFriends failed for userId=" + userId + ", friendId=" + friendId + ": " + e.getMessage());
      return false;
    }
  }
}