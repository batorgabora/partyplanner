package model;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public interface PartyModel {


  User login(String username, String password);
  void addFriend(User user, User friend);
  void removeFriend(User user, User friend);


  List<Party> getParties(User user);
  Party getParty(int id);
  void joinParty(User user, Party party);
  void leaveParty(User user, Party party);
  List<User> getAllUsers();


 //sub till organizer class is done Party createParty(String title, String description, String location, Organizer organizer);
  void deleteParty(Party party);
  void manageParty(Party party, String title, String description, String location);
  void addParticipant(Party party, Participant participant);
  void removeParticipant(Party party, Participant participant);
  void addListener(String propertyName, PropertyChangeListener listener);
  void removeListener(String propertyName, PropertyChangeListener listener);
  User createAccount(String username, String password, String confirmPassword, String mail);
  public List<Item> getItems(Party party);
  public List<Participant> getParticipants(Party party);
  public String getRole(User user, Party party);
  public List<Option> getOptions(Party party);

}