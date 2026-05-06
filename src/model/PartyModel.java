package model;

import java.util.List;

public interface PartyModel {

  // User operations
  User login(String username, String password);
  void addFriend(User user, User friend);
  void removeFriend(User user, User friend);

  // Party operations
  List<Party> getParties(User user);
  Party getParty(int id);
  void joinParty(User user, Party party);
  void leaveParty(User user, Party party);

  // Organizer operations
 //sub till organizer class is done Party createParty(String title, String description, String location, Organizer organizer);
  void deleteParty(Party party);
  void manageParty(Party party, String title, String description, String location);
  void addParticipant(Party party, Participant participant);
  void removeParticipant(Party party, Participant participant);
}