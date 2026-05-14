package shared.model;

public class Message {
  private String messageId;
  private String partyId;
  private String userId;
  private String content;
  private String sentAt;

  public Message(String messageId, String partyId, String userId, String content, String sentAt) {
    this.messageId = messageId;
    this.partyId   = partyId;
    this.userId    = userId;
    this.content   = content;
    this.sentAt    = sentAt;
  }

  public String getMessageId() { return messageId; }
  public String getPartyId()   { return partyId;   }
  public String getUserId()    { return userId;     }
  public String getContent()   { return content;    }
  public String getSentAt()    { return sentAt;     }
}