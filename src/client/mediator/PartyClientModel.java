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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class PartyClientModel implements PartyModel {
  private final PartyClient client;
  private final Gson gson;
  private final PropertyChangeSupport support = new PropertyChangeSupport(this);
  private final LinkedBlockingQueue<JsonObject> responseQueue = new LinkedBlockingQueue<>();
  private final ReentrantLock requestLock = new ReentrantLock();

  public PartyClientModel(String host, int port) {
    this.client = new PartyClient(host, port);
    this.gson = new Gson();

    Thread listenerThread = new Thread(() -> {
      while (true) {
        String raw = client.receive();
        if (raw != null) {
          try {
            JsonObject json = JsonParser.parseString(raw).getAsJsonObject();
            String msgType = json.has("type") ? json.get("type").getAsString() : "";

            if ("error".equals(msgType)) {
              support.firePropertyChange("error", null,
                  json.has("message") ? json.get("message").getAsString() : "unknown error");
            }
            if (json.has("action")) {
              support.firePropertyChange(json.get("action").getAsString(), null, json);
            }
            if (!"broadcast".equals(msgType)) {
              responseQueue.put(json);
            }
          } catch (Exception e) {
            System.out.println("Listener error: " + e.getMessage());
          }
        }
      }
    });
    listenerThread.setDaemon(true);
    listenerThread.start();
  }

  private JsonObject sendAndReceive(Runnable sendAction) {
    requestLock.lock();
    try {
      sendAction.run();
      return responseQueue.take();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Interrupted waiting for response");
    } finally {
      requestLock.unlock();
    }
  }

  private String getData(JsonObject json) {
    if (!json.has("data") || json.get("data").isJsonNull()) return null;
    var data = json.get("data");
    if (data.isJsonPrimitive()) return data.getAsString();
    return data.toString();
  }

  private boolean isError(JsonObject json) {
    return json.has("type") && "error".equals(json.get("type").getAsString());
  }

  // auth
  @Override public User login(String username, String password) {
    JsonObject json = sendAndReceive(() -> client.requestLogin(username, password));
    if (isError(json)) return null;
    return gson.fromJson(json.get("data"), User.class);
  }

  @Override public User createAccount(String username, String password, String confirmPassword, String mail) {
    JsonObject json = sendAndReceive(() -> client.requestCreateAccount(username, password, confirmPassword, mail));
    if (isError(json)) return null;
    return gson.fromJson(json.get("data"), User.class);
  }

  // users
  @Override public List<User> getAllUsers() {
    JsonObject json = sendAndReceive(client::requestGetAllUsers);
    if (isError(json)) return List.of();
    Type type = new TypeToken<List<User>>(){}.getType();
    return gson.fromJson(json.get("data"), type);
  }

  @Override public List<User> getFriends(User user) {
    JsonObject json = sendAndReceive(() -> client.requestGetFriends(user.getId()));
    if (isError(json)) return List.of();
    Type type = new TypeToken<List<User>>(){}.getType();
    return gson.fromJson(json.get("data"), type);
  }

  @Override public List<User> getNonFriends(User user) {
    JsonObject json = sendAndReceive(() -> client.requestGetNonFriends(user.getId()));
    if (isError(json)) return List.of();
    Type type = new TypeToken<List<User>>(){}.getType();
    return gson.fromJson(json.get("data"), type);
  }

  @Override public void addFriend(User user, User friend) {
    sendAndReceive(() -> client.requestAddFriend(user.getId(), friend.getId()));
  }

  @Override public void removeFriend(User user, User friend) {
    sendAndReceive(() -> client.requestRemoveFriend(user.getId(), friend.getId()));
  }

  // parties
  @Override public ArrayList<Party> getMyParties(User user) {
    JsonObject json = sendAndReceive(() -> client.requestGetMyParties(user.getId()));
    if (isError(json)) return new ArrayList<>();
    Type type = new TypeToken<ArrayList<Party>>(){}.getType();
    return gson.fromJson(json.get("data"), type);
  }

  @Override public List<Party> getInvitedParties(User user) {
    JsonObject json = sendAndReceive(() -> client.requestGetInvitedParties(user.getId()));
    if (isError(json)) return List.of();
    Type type = new TypeToken<List<Party>>(){}.getType();
    return gson.fromJson(json.get("data"), type);
  }

  @Override public Party createParty(String name, String description, String location, String organizerId, LocalDate date) {
    JsonObject json = sendAndReceive(() -> client.requestCreateParty(name, description, location, organizerId, date));
    if (isError(json)) return null;
    return gson.fromJson(json.get("data"), Party.class);
  }

  @Override public Party createParty(String name, String description, String location, String organizerId) {
    return null;
  }

  @Override public Party getParty(int id) { return null; }

  @Override public void updateParty(Party party, String name, String description, String location) {
    sendAndReceive(() -> client.requestUpdateParty(party.getId(), name, description, location));
  }

  @Override public void updatePartyDate(Party party, String date) {
    sendAndReceive(() -> client.requestUpdatePartyDate(party.getId(), date));
  }

  @Override public void deleteParty(Party party) {
    sendAndReceive(() -> client.requestDeleteParty(party.getId()));
  }

  @Override public void manageParty(Party party, String title, String description, String location) {}

  // party membership
  @Override public void joinParty(User user, Party party) {
    sendAndReceive(() -> client.requestJoinParty(user.getId(), party.getId()));
  }

  @Override public void leaveParty(User user, Party party) {
    sendAndReceive(() -> client.requestLeaveParty(user.getId(), party.getId()));
  }

  @Override public void acceptInvite(User user, Party party) {
    sendAndReceive(() -> client.requestAcceptInvite(user.getId(), party.getId()));
  }

  @Override public void declineInvite(User user, Party party) {
    sendAndReceive(() -> client.requestDeclineInvite(user.getId(), party.getId()));
  }

  @Override public String getStatus(User user, Party party) {
    JsonObject json = sendAndReceive(() -> client.requestGetStatus(user.getId(), party.getId()));
    if (isError(json)) return null;
    return getData(json);
  }

  @Override public String getRole(User user, Party party) {
    JsonObject json = sendAndReceive(() -> client.requestGetRole(user.getId(), party.getId()));
    if (isError(json)) return null;
    return getData(json);
  }

  // participants
  @Override public List<Participant> getParticipants(Party party) {
    JsonObject json = sendAndReceive(() -> client.requestGetParticipants(party.getId()));
    if (isError(json)) return List.of();
    Type type = new TypeToken<List<Participant>>(){}.getType();
    return gson.fromJson(json.get("data"), type);
  }

  @Override public List<Participant> addParticipant(Party party, Participant participant) {
    JsonObject json = sendAndReceive(() -> client.requestAddParticipant(party.getId(), participant.getUser().getId()));
    if (isError(json)) return List.of();
    Type type = new TypeToken<List<Participant>>(){}.getType();
    return gson.fromJson(json.get("data"), type);
  }

  @Override public List<Participant> removeParticipant(Party party, Participant participant) {
    JsonObject json = sendAndReceive(() -> client.requestRemoveParticipant(party.getId(), participant.getUser().getId()));
    if (isError(json)) return List.of();
    Type type = new TypeToken<List<Participant>>(){}.getType();
    return gson.fromJson(json.get("data"), type);
  }

  // items
  @Override public List<Item> getItems(Party party) {
    JsonObject json = sendAndReceive(() -> client.requestGetItems(party.getId()));
    if (isError(json)) return List.of();
    Type type = new TypeToken<List<Item>>(){}.getType();
    return gson.fromJson(json.get("data"), type);
  }

  @Override public List<Item> addItem(Party party, String name) {
    JsonObject json = sendAndReceive(() -> client.requestAddItem(party.getId(), name));
    if (isError(json)) return List.of();
    Type type = new TypeToken<List<Item>>(){}.getType();
    return gson.fromJson(json.get("data"), type);
  }

  @Override public List<Item> removeItem(Item item) {
    JsonObject json = sendAndReceive(() -> client.requestRemoveItem(item.getId()));
    if (isError(json)) return List.of();
    Type type = new TypeToken<List<Item>>(){}.getType();
    return gson.fromJson(json.get("data"), type);
  }

  @Override public List<Item> claimItem(String itemId, String userId) {
    JsonObject json = sendAndReceive(() -> client.requestClaimItem(itemId, userId));
    if (isError(json)) return List.of();
    Type type = new TypeToken<List<Item>>(){}.getType();
    return gson.fromJson(json.get("data"), type);
  }

  @Override public List<Item> unclaimItem(String itemId) {
    JsonObject json = sendAndReceive(() -> client.requestUnclaimItem(itemId));
    if (isError(json)) return List.of();
    Type type = new TypeToken<List<Item>>(){}.getType();
    return gson.fromJson(json.get("data"), type);
  }

  // options
  @Override public List<Option> getOptions(Party party) {
    JsonObject json = sendAndReceive(() -> client.requestGetOptions(party.getId()));
    if (isError(json)) return List.of();
    Type type = new TypeToken<List<Option>>(){}.getType();
    return gson.fromJson(json.get("data"), type);
  }

  @Override public List<Option> addOption(Party party, String proposal) {
    JsonObject json = sendAndReceive(() -> client.requestAddOption(party.getId(), proposal));
    if (isError(json)) return List.of();
    Type type = new TypeToken<List<Option>>(){}.getType();
    return gson.fromJson(json.get("data"), type);
  }

  @Override public List<Option> removeOption(Option option) {
    JsonObject json = sendAndReceive(() -> client.requestRemoveOption(option.getOptionid()));
    if (isError(json)) return List.of();
    Type type = new TypeToken<List<Option>>(){}.getType();
    return gson.fromJson(json.get("data"), type);
  }

  @Override public List<Option> voteForOption(String optionId, String userId) {
    JsonObject json = sendAndReceive(() -> client.requestVoteForOption(optionId, userId));
    if (isError(json)) return List.of();
    Type type = new TypeToken<List<Option>>(){}.getType();
    return gson.fromJson(json.get("data"), type);
  }

  @Override public List<Option> removeVote(String optionId, String userId) {
    JsonObject json = sendAndReceive(() -> client.requestRemoveVote(optionId, userId));
    if (isError(json)) return List.of();
    Type type = new TypeToken<List<Option>>(){}.getType();
    return gson.fromJson(json.get("data"), type);
  }

  @Override public boolean hasVotedInParty(String userId, String partyId) {
    JsonObject json = sendAndReceive(() -> client.requestHasVoted(userId, partyId));
    if (isError(json)) return false;
    return Boolean.parseBoolean(getData(json));
  }

  @Override public boolean hasVotedForOption(String userId, String optionId) {
    JsonObject json = sendAndReceive(() -> client.requestHasVotedForOption(userId, optionId));
    if (isError(json)) return false;
    return Boolean.parseBoolean(getData(json));
  }

  @Override public String getTopVotedOption(String partyId) {
    JsonObject json = sendAndReceive(() -> client.requestGetTopVotedOption(partyId));
    if (isError(json)) return "no votes yet";
    String data = getData(json);
    return data != null && !data.isEmpty() ? data : "no votes yet";
  }

  // messages
  @Override public Message sendMessage(String partyId, String userId, String content) {
    JsonObject json = sendAndReceive(() -> client.requestSendMessage(partyId, userId, content));
    if (isError(json)) return null;
    return gson.fromJson(json.get("data"), Message.class);
  }

  @Override public List<Message> getMessages(String partyId) {
    JsonObject json = sendAndReceive(() -> client.requestGetMessages(partyId));
    if (isError(json)) return List.of();
    Type type = new TypeToken<List<Message>>(){}.getType();
    return gson.fromJson(json.get("data"), type);
  }

  @Override public void addListener(String propertyName, PropertyChangeListener listener) {
    support.addPropertyChangeListener(propertyName, listener);
  }

  @Override public void removeListener(String propertyName, PropertyChangeListener listener) {
    support.removePropertyChangeListener(propertyName, listener);
  }
}