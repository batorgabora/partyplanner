package shared.model.service;

import shared.model.Message;
import java.util.List;

public interface MessageService extends ObservableService {
  Message sendMessage(String partyId, String userId, String content);
  List<Message> getMessages(String partyId);
}