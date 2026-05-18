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

  public PartyClientModel(String host, int port) {
    this.client = new PartyClient(host, port);
    this.gson = new Gson();

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

  // A lock that only lets ONE thread at a time run the send+receive pair.
  // "Reentrant" means if the same thread already holds the lock, it can
  // acquire it again without deadlocking (we don't use that here, but it's
  // the standard Lock implementation in Java).
  private final ReentrantLock requestLock = new ReentrantLock();

  private JsonObject sendAndReceive(Runnable sendAction) {
    // Block here until no other thread is inside this method.
    // If Thread A is sending a request and waiting for a response,
    // Thread B will wait at this line instead of sending its own
    // request and stealing Thread A's response from the queue.
    requestLock.lock();
    try {
      // Now we're the only thread in here — safe to send and receive.
      sendAction.run();       // send the request
      return responseQueue.take(); // wait for and return the response
      // Since no other thread can send between these two lines,
      // the response we take() is guaranteed to be ours.
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Interrupted waiting for response");
    } finally {
      // Always release the lock — even if an exception is thrown —
      // so other threads don't wait forever.
      requestLock.unlock();
    }
  }

  private boolean isError(JsonObject json) {
    // json.has("type") — checks the key exists at all
    // json.get("type").getAsString() — reads it as a plain String
    // Returns true only if type is exactly "error"
    return json.has("type") && "error".equals(json.get("type").getAsString());
  }

  private String getData(JsonObject json) {
    // Used for plain string responses (role, status, vote count, etc.)
    // where "data" is just "organizer" or "true" — not a JSON object/array.

    // Guard: if "data" key is missing or explicitly null, return null
    if (!json.has("data") || json.get("data").isJsonNull()) return null;

    var data = json.get("data"); // JsonElement — could be string, object, array

    // isJsonPrimitive() means it's a string, number, or boolean —
    // getAsString() safely reads it as a plain Java String
    if (data.isJsonPrimitive()) return data.getAsString();

    // Fallback: if it's somehow an object or array, toString() gives
    // the raw JSON text — better than crashing
    return data.toString();
  }

  @Override public ArrayList<Party> getMyParties(User user) {

    // sendAndReceive takes a Runnable — a piece of code to run.
    // It acquires the lock, runs the send, waits for the response,
    // then releases the lock. The lambda () -> ... is the "send" part.
    JsonObject json = sendAndReceive(() -> client.requestGetMyParties(user.getId()));

    // isError checks if the response has "type":"error" in it.
    // The server sends this when something goes wrong (user not found, DB error, etc.)
    // If so, return an empty list rather than crashing.
    if (isError(json)) return new ArrayList<>();

    // Gson needs to know the exact type to deserialize into at runtime.
    // Java erases generic types at runtime (type erasure), so
    // List<Party>.class doesn't exist — you have to use TypeToken to
    // capture it. This is Gson's workaround for that limitation.
    Type type = new TypeToken<ArrayList<Party>>(){}.getType();

    // json.get("data") returns the "data" field from the response JsonObject.
    // The full response looks like:
    // {
    //   "type": "response",
    //   "action": "getMyParties",
    //   "data": [ { "id": "...", "name": "...", ... }, { ... } ]
    // }
    // gson.fromJson() walks the JSON array and maps each object
    // to a Party instance using reflection (matching field names).
    return gson.fromJson(json.get("data"), type);
  }

  //same process later on

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

  @Override public List<Item> getItems(Party party) {
    JsonObject json = sendAndReceive(() -> client.requestGetItems(party.getId()));
    if (isError(json)) return List.of();
    Type type = new TypeToken<List<Item>>(){}.getType();
    return gson.fromJson(json.get("data"), type);
  }

  @Override public void addItem(Party party, String name) {
    sendAndReceive(() -> client.requestAddItem(party.getId(), name));
  }

  @Override public void removeItem(Item item) {
    sendAndReceive(() -> client.requestRemoveItem(item.getId()));
  }

  @Override public void claimItem(String itemId, String userId) {
    sendAndReceive(() -> client.requestClaimItem(itemId, userId));
  }

  @Override public void unclaimItem(String itemId) {
    sendAndReceive(() -> client.requestUnclaimItem(itemId));
  }

  @Override public List<Option> getOptions(Party party) {
    JsonObject json = sendAndReceive(() -> client.requestGetOptions(party.getId()));
    if (isError(json)) return List.of();
    Type type = new TypeToken<List<Option>>(){}.getType();
    return gson.fromJson(json.get("data"), type);
  }

  @Override public void addOption(Party party, String proposal) {
    sendAndReceive(() -> client.requestAddOption(party.getId(), proposal));
  }

  @Override public void removeOption(Option option) {
    sendAndReceive(() -> client.requestRemoveOption(option.getOptionid()));
  }

  @Override public void voteForOption(String optionId, String userId) {
    sendAndReceive(() -> client.requestVoteForOption(optionId, userId));
  }

  @Override public void removeVote(String optionId, String userId) {
    sendAndReceive(() -> client.requestRemoveVote(optionId, userId));
  }

  @Override public String getTopVotedOption(String partyId) {
    JsonObject json = sendAndReceive(() -> client.requestGetTopVotedOption(partyId));
    if (isError(json)) return "no votes yet";
    String data = getData(json);
    return data != null && !data.isEmpty() ? data : "no votes yet";
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

  @Override public List<Participant> getParticipants(Party party) {
    JsonObject json = sendAndReceive(() -> client.requestGetParticipants(party.getId()));
    if (isError(json)) return List.of();
    Type type = new TypeToken<List<Participant>>(){}.getType();
    return gson.fromJson(json.get("data"), type);
  }

  @Override public void addParticipant(Party party, Participant participant) {
    sendAndReceive(() -> client.requestAddParticipant(party.getId(), participant.getUser().getId()));
  }

  @Override public void removeParticipant(Party party, Participant participant) {
    sendAndReceive(() -> client.requestRemoveParticipant(party.getId(), participant.getUser().getId()));
  }

  @Override public void manageParty(Party party, String title, String description, String location) {}

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