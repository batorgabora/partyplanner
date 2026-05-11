package client.mediator;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import shared.model.*;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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


  @Override public List<Party> getParties(User user)
  {
    if (user == null) return List.of();
    CompletableFuture<List<Party>> future = new CompletableFuture<>();
    PropertyChangeListener listener = evt -> {
      JsonObject json = (JsonObject) evt.getNewValue();
      Party[] parties = gson.fromJson(json.get("data").getAsString(), Party[].class);
      future.complete(Arrays.asList(parties));
    };
    support.addPropertyChangeListener("getAll", listener);
    client.requestGetParties(user.getId());
    try {
      return future.get(5, TimeUnit.SECONDS);
    } catch (Exception e) {
      return List.of();
    } finally {
      support.removePropertyChangeListener("getAll", listener);
    }
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
    CompletableFuture<Party> future = new CompletableFuture<>();
    PropertyChangeListener listener = evt -> {
      JsonObject json = (JsonObject) evt.getNewValue();
      Party party = gson.fromJson(json.get("data").getAsString(), Party.class);
      future.complete(party);
    };
    support.addPropertyChangeListener("createParty", listener);
    client.requestCreateParty(name, description, location, organizerId, date);
    try {
      return future.get(5, TimeUnit.SECONDS);
    } catch (Exception e) {
      return null;
    } finally {
      support.removePropertyChangeListener("createParty", listener);
    }
  }

  @Override public void updateParty(Party party, String name,
      String description, String location)
  {

  }
}
