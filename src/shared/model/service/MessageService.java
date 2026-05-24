package shared.model.service;

import shared.model.Message;
import java.util.List;

/**
 * Defines operations for party chat messages.
 *  * @author Loke Hansen
 *  * @author Victor Tonu
 *  * @author Marko Stokic
 *  * @author Mike Lorenzen
 *  * @author Bator Gabora
 */
public interface MessageService extends ObservableService {
  /**
   * Sends a new message to a party chat.
   * @param partyId the id of the party receiving the message
   * @param userId the id of the user sending the message
   * @param content the message text
   * @return the created message, or null if sending fails
   */
  Message sendMessage(String partyId, String userId, String content);

  /**
   * Returns the message history for a party chat.
   * @param partyId the id of the party whose messages should be retrieved
   * @return a list of messages for the party
   */
  List<Message> getMessages(String partyId);
}
