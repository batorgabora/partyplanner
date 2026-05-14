package client.mediator;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import shared.model.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PartyClientModel implements PartyModel
{
  private final PartyClient client;
  private final Gson gson;
  private final PropertyChangeSupport support = new PropertyChangeSupport(this);

  // queue that the listener thread fills and sendAndReceive() drains
  private final java.util.concurrent.LinkedBlockingQueue<JsonObject> responseQueue = new java.util.concurrent.LinkedBlockingQueue<>();

  public PartyClientModel(String host, int port) {
    this.client = new PartyClient(host, port);
    this.gson = new Gson();

    // background thread that owns the socket input stream.
    // all incoming messages go through here — both responses and server push events.
    Thread listenerThread = new Thread(() -> {
      while (true) {
        String raw = client.receive();
        if (raw != null) {
          try {
            JsonObject json = JsonParser.parseString(raw).getAsJsonObject();
            if (json.has("action")) {
              support.firePropertyChange(json.get("action").getAsString(), null, json);
            }
            responseQueue.put(json);
          } catch (Exception e) {
            System.out.println("Listener error: " + e.getMessage());
          }
        }
      }
    });
    listenerThread.setDaemon(true);
    listenerThread.start();
  }

  // all methods use this instead of client.receive() directly
  private JsonObject sendAndReceive() {
    try {
      return responseQueue.take();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Interrupted waiting for response");
    }
  }

  private String getData(JsonObject json) {
    return json.has("data") && !json.get("data").isJsonNull() ? json.get("data").getAsString() : null;
  }

  private boolean isError(JsonObject json) {
    return json.has("type") && "error".equals(json.get("type").getAsString());
  }

  @Override public User login(String username, String password) {
    client.requestLogin(username, password);
    JsonObject json = sendAndReceive();
    if (isError(json)) return null;
    return gson.fromJson(json.get("data").getAsString(), User.class);
  }

  @Override public User createAccount(String username, String password, String confirmPassword, String mail) {
    client.requestCreateAccount(username, password, confirmPassword, mail);
    JsonObject json = sendAndReceive();
    if (isError(json)) return null;
    return gson.fromJson(json.get("data").getAsString(), User.class);
  }

  @Override public List<User> getAllUsers() {
    client.requestGetAllUsers();
    JsonObject json = sendAndReceive();
    if (isError(json)) return List.of();
    Type type = new TypeToken<List<User>>(){}.getType();
    return gson.fromJson(json.get("data").getAsString(), type);
  }

  @Override public void addFriend(User user, User friend) {
    client.requestAddFriend(user.getId(), friend.getId());
    sendAndReceive();
  }

  @Override public void removeFriend(User user, User friend) {}

  @Override public ArrayList<Party> getMyParties(User user) {
    client.requestGetMyParties(user.getId());
    JsonObject json = sendAndReceive();
    if (isError(json)) return new ArrayList<>();
    Type type = new TypeToken<ArrayList<Party>>(){}.getType();
    return gson.fromJson(json.get("data").getAsString(), type);
  }

  @Override public List<Party> getInvitedParties(User user) {
    client.requestGetInvitedParties(user.getId());
    JsonObject json = sendAndReceive();
    if (isError(json)) return List.of();
    Type type = new TypeToken<List<Party>>(){}.getType();
    return gson.fromJson(json.get("data").getAsString(), type);
  }

  @Override public Party createParty(String name, String description, String location, String organizerId, LocalDate date) {
    client.requestCreateParty(name, description, location, organizerId, date);
    JsonObject json = sendAndReceive();
    if (isError(json)) return null;
    return gson.fromJson(json.get("data").getAsString(), Party.class);
  }

  @Override public Party createParty(String name, String description, String location, String organizerId) {
    return null;
  }

  @Override public Party getParty(int id) { return null; }

  @Override public void updateParty(Party party, String name, String description, String location) {
    client.requestUpdateParty(party.getId(), name, description, location);
    sendAndReceive();
  }

  @Override public void updatePartyDate(Party party, String date) {
    client.requestUpdatePartyDate(party.getId(), date);
    sendAndReceive();
  }

  @Override public void deleteParty(Party party) {
    client.requestDeleteParty(party.getId());
    sendAndReceive();
  }


  @Override public void joinParty(User user, Party party) {
    client.requestJoinParty(user.getId(), party.getId());
    sendAndReceive();
  }

  @Override public void leaveParty(User user, Party party) {
    client.requestLeaveParty(user.getId(), party.getId());
    sendAndReceive();
  }

  @Override public void acceptInvite(User user, Party party) {
    client.requestAcceptInvite(user.getId(), party.getId());
    sendAndReceive();
  }

  @Override public void declineInvite(User user, Party party) {
    client.requestDeclineInvite(user.getId(), party.getId());
    sendAndReceive();
  }

  @Override public String getStatus(User user, Party party) {
    client.requestGetStatus(user.getId(), party.getId());
    JsonObject json = sendAndReceive();
    if (isError(json)) return null;
    return getData(json);
  }

  @Override public String getRole(User user, Party party) {
    client.requestGetRole(user.getId(), party.getId());
    JsonObject json = sendAndReceive();
    if (isError(json)) return null;
    return getData(json);
  }

  //items
  @Override public List<Item> getItems(Party party) {
    client.requestGetItems(party.getId());
    JsonObject json = sendAndReceive();
    if (isError(json)) return List.of();
    Type type = new TypeToken<List<Item>>(){}.getType();
    return gson.fromJson(json.get("data").getAsString(), type);
  }

  @Override public void addItem(Party party, String name) {
    client.requestAddItem(party.getId(), name);
    sendAndReceive();
  }

  @Override public void removeItem(Item item) {
    client.requestRemoveItem(item.getId());
    sendAndReceive();
  }

  @Override public void claimItem(String itemId, String userId) {
    client.requestClaimItem(itemId, userId);
    sendAndReceive();
  }

  @Override public void unclaimItem(String itemId) {
    client.requestUnclaimItem(itemId);
    sendAndReceive();
  }


  //options
  @Override public List<Option> getOptions(Party party) {
    client.requestGetOptions(party.getId());
    JsonObject json = sendAndReceive();
    if (isError(json)) return List.of();
    Type type = new TypeToken<List<Option>>(){}.getType();
    return gson.fromJson(json.get("data").getAsString(), type);
  }

  @Override public void addOption(Party party, String proposal) {
    client.requestAddOption(party.getId(), proposal);
    sendAndReceive();
  }

  @Override public void removeOption(Option option) {
    client.requestRemoveOption(option.getOptionid());
    sendAndReceive();
  }

  @Override public void voteForOption(String optionId, String userId) {
    client.requestVoteForOption(optionId, userId);
    sendAndReceive();
  }

  @Override public void removeVote(String optionId, String userId) {
    client.requestRemoveVote(optionId, userId);
    sendAndReceive();
  }

  @Override public String getTopVotedOption(String partyId) {
    client.requestGetTopVotedOption(partyId);
    JsonObject json = sendAndReceive();
    if (isError(json)) return "no votes yet";
    String data = getData(json);
    return data != null && !data.isEmpty() ? data : "no votes yet";
  }

  @Override public boolean hasVotedInParty(String userId, String partyId) {
    client.requestHasVoted(userId, partyId);
    JsonObject json = sendAndReceive();
    if (isError(json)) return false;
    return Boolean.parseBoolean(getData(json));
  }

  @Override public boolean hasVotedForOption(String userId, String optionId) {
    client.requestHasVotedForOption(userId, optionId);
    JsonObject json = sendAndReceive();
    if (isError(json)) return false;
    return Boolean.parseBoolean(getData(json));
  }


  //participants
  @Override public List<Participant> getParticipants(Party party) {
    client.requestGetParticipants(party.getId());
    JsonObject json = sendAndReceive();
    if (isError(json)) return List.of();
    Type type = new TypeToken<List<Participant>>(){}.getType();
    return gson.fromJson(json.get("data").getAsString(), type);
  }

  @Override public void addParticipant(Party party, Participant participant) {
    client.requestAddParticipant(party.getId(), participant.getUser().getId());
    sendAndReceive();
  }

  @Override public void removeParticipant(Party party, Participant participant) {
    client.requestRemoveParticipant(party.getId(), participant.getUser().getId());
    sendAndReceive();
  }

  @Override public void manageParty(Party party, String title,
      String description, String location)
  {

  }


  @Override public Message sendMessage(String partyId, String userId, String content) {
    client.requestSendMessage(partyId, userId, content);
    JsonObject json = sendAndReceive();
    if (isError(json)) return null;
    return gson.fromJson(json.get("data").getAsString(), Message.class);
  }

  @Override public List<Message> getMessages(String partyId) {
    client.requestGetMessages(partyId);
    JsonObject json = sendAndReceive();
    if (isError(json)) return List.of();
    Type type = new TypeToken<List<Message>>(){}.getType();
    return gson.fromJson(json.get("data").getAsString(), type);
  }

  @Override public void addListener(String propertyName, PropertyChangeListener listener) {
    support.addPropertyChangeListener(propertyName, listener);
  }

  @Override public void removeListener(String propertyName, PropertyChangeListener listener) {
    support.removePropertyChangeListener(propertyName, listener);
  }
}