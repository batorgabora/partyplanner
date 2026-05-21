package shared.model;

import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface PartyModel {

 User login(String username, String password);
 User createAccount(String username, String password, String confirmPassword, String mail);

 List<User> getAllUsers();
 List<User> getFriends(User user);
 List<User> getNonFriends(User user);
 void addFriend(User user, User friend);
 void removeFriend(User user, User friend);

 ArrayList<Party> getMyParties(User user);
 List<Party> getInvitedParties(User user);
 Party getParty(int id);
 Party createParty(String name, String description, String location, String organizerId, LocalDate date);
 Party createParty(String name, String description, String location, String organizerId);
 void updateParty(Party party, String name, String description, String location);
 void updatePartyDate(Party party, String date);
 void deleteParty(Party party);
 void manageParty(Party party, String title, String description, String location);

 void joinParty(User user, Party party);
 void leaveParty(User user, Party party);
 void acceptInvite(User user, Party party);
 void declineInvite(User user, Party party);
 String getStatus(User user, Party party);
 String getRole(User user, Party party);

 List<Participant> getParticipants(Party party);
 List<Participant> addParticipant(Party party, Participant participant);
 List<Participant> removeParticipant(Party party, Participant participant);

 List<Item> getItems(Party party);
 List<Item> addItem(Party party, String name);
 List<Item> removeItem(Item item);
 List<Item> claimItem(String itemId, String userId);
 List<Item> unclaimItem(String itemId);

 List<Option> getOptions(Party party);
 List<Option> addOption(Party party, String proposal);
 List<Option> removeOption(Option option);
 List<Option> voteForOption(String optionId, String userId);
 List<Option> removeVote(String optionId, String userId);
 boolean hasVotedInParty(String userId, String partyId);
 boolean hasVotedForOption(String userId, String optionId);
 String getTopVotedOption(String partyId);

 Message sendMessage(String partyId, String userId, String content);
 List<Message> getMessages(String partyId);

 void addListener(String propertyName, PropertyChangeListener listener);
 void removeListener(String propertyName, PropertyChangeListener listener);
}