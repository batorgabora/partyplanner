package server.dao;

import server.database.DataBaseConnection;
import shared.model.Message;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MessageDAO {

  private static final Logger log = Logger.getLogger(MessageDAO.class.getName());

  public Message create(String messageId, String partyId, String userId, String content) {
    String sql = "INSERT INTO party_planner.messages (messageid, partyid, userid, content, sent_at) VALUES (?, ?, ?, ?, NOW() AT TIME ZONE 'Europe/Copenhagen')";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, messageId);
      ps.setString(2, partyId);
      ps.setString(3, userId);
      ps.setString(4, content);
      ps.executeUpdate();
      System.out.println("[MessageDAO] inserted messageId=" + messageId);
    } catch (SQLException e) {
      System.out.println("[MessageDAO] INSERT failed: " + e.getMessage());
      log.severe("create failed for messageid=" + messageId + ": " + e.getMessage());
      return null;
    }
    return getById(messageId);
  }

  public Message createAt(String messageId, String partyId, String userId, String content, LocalDateTime sentAt) {
    String sql = "INSERT INTO party_planner.messages (messageid, partyid, userid, content, sent_at) VALUES (?, ?, ?, ?, ?)";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, messageId);
      ps.setString(2, partyId);
      ps.setString(3, userId);
      ps.setString(4, content);
      ps.setTimestamp(5, Timestamp.valueOf(sentAt));
      ps.executeUpdate();
    } catch (SQLException e) {
      log.severe("createAt failed for messageid=" + messageId + ": " + e.getMessage());
      return null;
    }
    return getById(messageId);
  }

  public Message getById(String messageId) {
    String sql = "SELECT * FROM party_planner.messages WHERE messageid = ?";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, messageId);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) return mapRow(rs);
    } catch (SQLException e) {
      log.severe("getById failed for messageid=" + messageId + ": " + e.getMessage());
    }
    return null;
  }

  public List<Message> getByParty(String partyId) {
    String sql = "SELECT * FROM party_planner.messages WHERE partyid = ? ORDER BY sent_at ASC";
    List<Message> messages = new ArrayList<>();
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, partyId);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) messages.add(mapRow(rs));
    } catch (SQLException e) {
      log.severe("getByParty failed for partyid=" + partyId + ": " + e.getMessage());
    }
    return messages;
  }

  public void deleteAll() {
    String sql = "DELETE FROM party_planner.messages";
    try (Connection conn = DataBaseConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      int deleted = ps.executeUpdate();
      System.out.println("[MessageDAO] deleted " + deleted + " messages");
    } catch (SQLException e) {
      log.severe("deleteAll failed: " + e.getMessage());
    }
  }

  private Message mapRow(ResultSet rs) throws SQLException {
    Timestamp ts = rs.getTimestamp("sent_at");
    String sentAt = ts != null ? ts.toLocalDateTime().toString() : "";
    return new Message(
        rs.getString("messageid"),
        rs.getString("partyid"),
        rs.getString("userid"),
        rs.getString("content"),
        sentAt
    );
  }
}