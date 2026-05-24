package server.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import server.dao.*;
import shared.model.*;
import shared.util.PasswordUtil;

public class ModelManager implements PartyModel {

  private final PropertyChangeSupport support = new PropertyChangeSupport(this);

  public ModelManager() {}

  // auth
  @Override public User login(String username, String password) {
    User user = new UserDAO().getByUsername(username);
    if (user == null) return null;
    return PasswordUtil.verify(password, user.getPassword()) ? user : null;
  }

  @Override public User createAccount(String username, String password, String confirmPassword, String mail) {
    if (username == null || username.trim().isEmpty()) { fireError("Username is required."); return null; }
    if (password == null || password.isEmpty())        { fireError("Password is required."); return null; }
    if (mail == null || mail.trim().isEmpty())          { fireError("Email is required."); return null; }
    if (!password.equals(confirmPassword))              { fireError("Passwords do not match."); return null; }

    UserDAO userDAO = new UserDAO();
    if (userDAO.getByUsername(username) != null) { fireError("Username already taken."); return null; }
    String userId = userDAO.create(UUID.randomUUID().toString(), username, mail, PasswordUtil.hash(password));
    if (userId == null) { fireError("Failed to create account."); return null; }
    return new User(userId, username, PasswordUtil.hash(password), mail);
  }

  // users
  @Override public List<User> getAllUsers() {
    return new UserDAO().getAll();
  }

  @Override public List<User> getFriends(User user) {
    return new FriendDAO().getFriends(user.getId());
  }

  @Override public List<User> getNonFriends(User user) {
    return new FriendDAO().getNonFriends(user.getId());
  }

  @Override public void addFriend(User user, User friend) {
    new FriendDAO().addFriend(user.getId(), friend.getId());
  }

  @Override public void removeFriend(User user, User friend) {
    new FriendDAO().removeFriend(user.getId(), friend.getId());
  }

  // parties
  @Override public ArrayList<Party> getMyParties(User user) {
    if (user == null) return new ArrayList<>();
    return new PartyDAO().getAcceptedByUser(user.getId());
  }

  @Override public ArrayList<Party> getInvitedParties(User user) {
    if (user == null) return new ArrayList<>();
    return new PartyDAO().getInvitedByUser(user.getId());
  }

  @Override public Party getParty(int id) { return null; }

  @Override public Party createParty(String name, String description, String location, String organizerId, LocalDate date) {
    String partyId = UUID.randomUUID().toString();
    new PartyDAO().create(partyId, name, description, location, date != null ? date : LocalDate.now());
    new PartyUsersDAO().add(organizerId, partyId, "organizer");
    Party party = new PartyDAO().getById(partyId);
    if (party == null) fireError("Failed to create party.");
    return party;
  }

  @Override public Party createParty(String name, String description, String location, String organizerId) {
    return null;
  }

  @Override public void updateParty(Party party, String name, String description, String location) {
    party.setName(name);
    party.setDescription(description);
    party.setLocation(location);
    new PartyDAO().update(party.getId(), name, description, location);
  }

  @Override public void updatePartyDate(Party party, String date) {
    new PartyDAO().updateDate(party.getId(), date);
  }

  @Override public void deleteParty(Party party) {
    new PartyDAO().delete(party.getId());
  }

  @Override public void manageParty(Party party, String title, String description, String location) {
    if (party == null) return;
    party.setName(title);
    party.setDescription(description);
    party.setLocation(location);
  }

  // party membership
  @Override public void joinParty(User user, Party party) {
    new PartyUsersDAO().add(user.getId(), party.getId(), "participant");
  }

  @Override public void leaveParty(User user, Party party) {
    new PartyUsersDAO().remove(user.getId(), party.getId());
  }

  @Override public void acceptInvite(User user, Party party) {
    new PartyUsersDAO().updateStatus(user.getId(), party.getId(), "accepted");
  }

  @Override public void declineInvite(User user, Party party) {
    new PartyUsersDAO().updateStatus(user.getId(), party.getId(), "declined");
  }

  @Override public String getStatus(User user, Party party) {
    return new PartyUsersDAO().getStatus(user.getId(), party.getId());
  }

  @Override public String getRole(User user, Party party) {
    return new PartyUsersDAO().getRole(user.getId(), party.getId());
  }

  // participants
  @Override public List<Participant> getParticipants(Party party) {
    if (party == null) return new ArrayList<>();
    return new PartyUsersDAO().getParticipantsByParty(party.getId());
  }

  @Override public List<Participant> addParticipant(Party party, Participant participant) {
    if (party == null || participant == null) return getParticipants(party);
    PartyUsersDAO dao = new PartyUsersDAO();
    if (!dao.isMember(participant.getUser().getId(), party.getId())) {
      dao.add(participant.getUser().getId(), party.getId(), "participant");
    }
    return getParticipants(party);
  }

  @Override public List<Participant> removeParticipant(Party party, Participant participant) {
    if (party == null || participant == null) return getParticipants(party);
    if (party.getOrganizer() != null &&
        party.getOrganizer().getId().equals(participant.getUser().getId())) {
      return getParticipants(party);
    }
    new PartyUsersDAO().remove(participant.getUser().getId(), party.getId());
    return getParticipants(party);
  }

  // items
  @Override public List<Item> getItems(Party party) {
    return new ItemDAO().getByParty(party.getId());
  }

  @Override public List<Item> addItem(Party party, String name) {
    String id = "item-" + UUID.randomUUID().toString().substring(0, 8);
    new ItemDAO().create(id, name, 1, party.getId());
    return getItems(party);
  }

  @Override public List<Item> removeItem(Item item) {
    Party party = new PartyDAO().getById(item.getPartyId());
    new ItemDAO().delete(item.getId());
    return party != null ? getItems(party) : List.of();
  }

  @Override public List<Item> claimItem(String itemId, String userId) {
    new ItemDAO().claimItem(itemId, userId);
    Item item = new ItemDAO().getById(itemId);
    Party party = item != null ? new PartyDAO().getById(item.getPartyId()) : null;
    return party != null ? getItems(party) : List.of();
  }

  @Override public List<Item> unclaimItem(String itemId) {
    new ItemDAO().unclaimItem(itemId);
    Item item = new ItemDAO().getById(itemId);
    Party party = item != null ? new PartyDAO().getById(item.getPartyId()) : null;
    return party != null ? getItems(party) : List.of();
  }

  // options
  @Override public List<Option> getOptions(Party party) {
    return new OptionDAO().getByParty(party.getId());
  }

  @Override public List<Option> addOption(Party party, String proposal) {
    String id = "opt-" + UUID.randomUUID().toString().substring(0, 8);
    new OptionDAO().create(id, proposal, party.getId());
    return getOptions(party);
  }

  @Override public List<Option> removeOption(Option option) {
    Party party = new PartyDAO().getById(option.getPartyid());
    new OptionDAO().delete(option.getOptionid());
    return party != null ? getOptions(party) : List.of();
  }

  @Override public List<Option> voteForOption(String optionId, String userId) {
    new OptionDAO().vote(optionId, userId);
    Option option = new OptionDAO().getById(optionId);
    Party party = option != null ? new PartyDAO().getById(option.getPartyid()) : null;
    return party != null ? getOptions(party) : List.of();
  }

  @Override public List<Option> removeVote(String optionId, String userId) {
    new OptionDAO().removeVote(optionId, userId);
    Option option = new OptionDAO().getById(optionId);
    Party party = option != null ? new PartyDAO().getById(option.getPartyid()) : null;
    return party != null ? getOptions(party) : List.of();
  }

  @Override public boolean hasVotedInParty(String userId, String partyId) {
    return new OptionDAO().hasVoted(userId, partyId);
  }

  @Override public boolean hasVotedForOption(String userId, String optionId) {
    return new OptionDAO().hasVotedForOption(userId, optionId);
  }

  @Override public String getTopVotedOption(String partyId) {
    return new OptionDAO().getTopVoted(partyId);
  }

  // messages
  @Override public Message sendMessage(String partyId, String userId, String content) {
    return new MessageDAO().create(UUID.randomUUID().toString(), partyId, userId, content);
  }

  @Override public List<Message> getMessages(String partyId) {
    return new MessageDAO().getByParty(partyId);
  }

  private void fireError(String message) {
    support.firePropertyChange("error", null, message);
  }

  @Override public void addListener(String propertyName, PropertyChangeListener listener) {
    support.addPropertyChangeListener(propertyName, listener);
  }

  @Override public void removeListener(String propertyName, PropertyChangeListener listener) {
    support.removePropertyChangeListener(propertyName, listener);
  }
}