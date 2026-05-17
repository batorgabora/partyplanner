package shared.model;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public interface PartyModel {


  User login(String username, String password);
  List<User> getFriends(User user);
  List<User> getNonFriends(User user);
  void addFriend(User user, User friend);
  void removeFriend(User user, User friend);


  List<Party> getInvitedParties(User user);
  Party getParty(int id);
  void joinParty(User user, Party party);
  void leaveParty(User user, Party party);
  List<User> getAllUsers();


 //sub till organizer class is done Party createParty(String title, String description, String location, Organizer organizer);
  void deleteParty(Party party);
  void manageParty(Party party, String title, String description, String location);
  void addParticipant(Party party, Participant participant);
  void removeParticipant(Party party, Participant participant);
  void removeVote(String optionId, String userId);
  void updatePartyDate(Party party, String date);
  void addItem(Party party, String name);
  void removeItem(Item item);
  void addOption(Party party, String proposal);
  void removeOption(Option option);
  boolean hasVotedInParty(String userId, String partyId);
  void addListener(String propertyName, PropertyChangeListener listener);
  void removeListener(String propertyName, PropertyChangeListener listener);
  User createAccount(String username, String password, String confirmPassword, String mail);
  ArrayList<Party> getMyParties(User user);
  void acceptInvite(User user, Party party);
  void declineInvite(User user, Party party);
  String getStatus(User user, Party party);
  public List<Item> getItems(Party party);
  public List<Participant> getParticipants(Party party);
  public String getRole(User user, Party party);
  public List<Option> getOptions(Party party);

  Party createParty(String name, String description, String location, String organizerId, java.time.LocalDate date);

  Party createParty(String name, String description, String location,
      String organizerId);
  void updateParty(Party party, String name, String description, String location);
  void voteForOption(String optionId, String userId);
  boolean hasVotedForOption(String userId, String optionId);
  String getTopVotedOption(String partyId);
  void claimItem(String itemId, String userId);
  void unclaimItem(String itemId);
  Message sendMessage(String partyId, String userId, String content);
  List<Message> getMessages(String partyId);
}
