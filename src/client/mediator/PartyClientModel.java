package client.mediator;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import shared.model.*;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PartyClientModel implements PartyModel
{
  private final PartyClient client;
  private final Gson gson;
  private final PropertyChangeSupport support = new PropertyChangeSupport(this);
  public PartyClientModel(String host, int port) {
    this.client = new PartyClient(host, port);
    this.gson = new Gson();

    Thread listenerThread = new Thread(() -> {
      while (true) {
        String response = client.receive();
        if (response != null) {
          try {
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            if (!json.has("action")) continue;
            String action = json.get("action").getAsString();
            support.firePropertyChange(action, null, json);
          } catch (Exception e) {
            System.out.println("Listener error: " + e.getMessage());
          }
        }
      }
    });
    listenerThread.setDaemon(true);
    listenerThread.start();
  }

  @Override public User login(String username, String password)
  {
    return null;
  }

  @Override public void addFriend(User user, User friend)
  {

  }

  @Override public void removeFriend(User user, User friend)
  {

  }

  @Override public List<Party> getInvitedParties(User user)
  {
    return List.of();
  }

  @Override public Party getParty(int id)
  {
    return null;
  }

  @Override public void joinParty(User user, Party party)
  {

  }

  @Override public void leaveParty(User user, Party party)
  {

  }

  @Override public List<User> getAllUsers()
  {
    return List.of();
  }

  @Override public void deleteParty(Party party)
  {
    client.requestDeleteParty(party.getId());
  }

  @Override public void manageParty(Party party, String title,
      String description, String location)
  {

  }

  @Override public void addParticipant(Party party, Participant participant)
  {

  }

  @Override public void removeParticipant(Party party, Participant participant)
  {

  }

  @Override public void updatePartyDate(Party party, String date)
  {

  }

  @Override public void addItem(Party party, String name)
  {

  }

  @Override public void removeItem(Item item)
  {

  }

  @Override public void addOption(Party party, String proposal)
  {

  }

  @Override public void removeOption(Option option)
  {

  }

  @Override public boolean hasVotedInParty(String userId, String partyId)
  {
    return false;
  }

  @Override public void addListener(String propertyName, PropertyChangeListener listener)
  {
    support.addPropertyChangeListener(propertyName, listener);
  }

  @Override public void removeListener(String propertyName, PropertyChangeListener listener)
  {
    support.removePropertyChangeListener(propertyName, listener);

  }

  @Override public User createAccount(String username, String password,
      String confirmPassword, String mail)
  {
    return null;
  }

  @Override public ArrayList<Party> getMyParties(User user)
  {
    return null;
  }

  @Override public void acceptInvite(User user, Party party)
  {

  }

  @Override public void declineInvite(User user, Party party)
  {

  }

  @Override public void voteForOption(String optionId, String userId) {
    client.requestVoteForOption(optionId, userId);
    client.receive(); // wait for server ack
  }

  @Override public void removeVote(String optionId, String userId) {
    client.requestRemoveVote(optionId, userId);
    client.receive();
  }

  @Override public String getTopVotedOption(String partyId) {
    client.requestGetTopVotedOption(partyId);
    String response = client.receive();
    JsonObject json = JsonParser.parseString(response).getAsJsonObject();
    return json.has("data") && !json.get("data").isJsonNull()
        ? json.get("data").getAsString()
        : "no votes yet";
  }

  @Override public String getStatus(User user, Party party)
  {
    return "";
  }

  @Override public List<Item> getItems(Party party)
  {
    return List.of();
  }

  @Override public List<Participant> getParticipants(Party party)
  {
    return List.of();
  }

  @Override public String getRole(User user, Party party)
  {
    return "";
  }

  @Override public List<Option> getOptions(Party party)
  {
    return List.of();
  }


  @Override public Party createParty(String name, String description,
      String location, String organizerId, LocalDate date)
  {
    client.requestCreateParty(name, description, location, organizerId, date);
    String response = client.receive();
    JsonObject json = JsonParser.parseString(response).getAsJsonObject();
    return gson.fromJson(json.get("data"), Party.class);
  }

  @Override public Party createParty(String name, String description,
      String location, String organizerId)
  {
    return null;
  }

  @Override public void updateParty(Party party, String name,
      String description, String location)
  {

  }
}
