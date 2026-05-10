package mediator;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
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
          JsonObject json = JsonParser.parseString(response).getAsJsonObject();
          String action = json.get("action").getAsString();
          support.firePropertyChange(action, null, json);
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
      String location, String organizerId)
  {
    client.requestCreateParty(name, description, location, organizerId);
    String response = client.receive();
    JsonObject json = JsonParser.parseString(response).getAsJsonObject();
    return gson.fromJson(json.get("data"), Party.class);
  }
}
