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

public class ModelManager implements PartyModel
{
  private List<User> users;
  private List<Party> parties;
  private final PropertyChangeSupport support = new PropertyChangeSupport(this);

  public ModelManager() {
    this.users = new ArrayList<>();
    this.parties = new ArrayList<>();
  }

  @Override public User login(String username, String password) {
    UserDAO userDAO = new UserDAO();
    User user = userDAO.getByUsername(username);
    if (user == null) return null;
    if (PasswordUtil.verify(password, user.getPassword())) {
      return user;
    }
    return null;
  }

  @Override public synchronized void addFriend(User user, User friend)
  {
    if (!user.getFriendList().contains(friend))
    {
      user.addFriend(friend);
      friend.addFriend(user);
    }
  }

  @Override public synchronized void removeFriend(User user, User friend)
  {
    user.removeFriend(friend);
    friend.removeFriend(user);
  }

  @Override public synchronized ArrayList<Party> getParties(User user)
  {
    if (user == null) return new ArrayList<>();
    PartyDAO partyDAO = new PartyDAO();
    return partyDAO.getByUser(user.getId());
  }


  @Override public synchronized List<Item> getItems(Party party) {
    return new ItemDAO().getByParty(party.getId());
  }


  @Override public synchronized String getRole(User user, Party party) {
    return new PartyUsersDAO().getRole(user.getId(), party.getId());
  }


  @Override public synchronized List<Participant> getParticipants(Party party) {
    if (party == null) return new ArrayList<>();
    return new PartyUsersDAO().getParticipantsByParty(party.getId());
  }

  @Override public synchronized List<Option> getOptions(Party party) {
    return new OptionDAO().getByParty(party.getId());
  }

  @Override public synchronized Party createParty(String name, String description,
      String location, String organizerId)
  {
    String partyId = UUID.randomUUID().toString();
    PartyDAO partyDAO = new PartyDAO();
    partyDAO.create(partyId, name, description, location, LocalDate.now());
    Party party = partyDAO.getById(partyId);
    if (party == null) fireError("Failed to create party. Please try again.");
    return party;
  }

  @Override public synchronized Party getParty(int id)
  {
    if (id < 0 || id >= parties.size())
    {
      return null;
    }
    return parties.get(id);
  }

  @Override public synchronized void joinParty(User user, Party party)
  {
    Participant participant = new Participant(party, user);
    party.addParticipant(participant);
    user.joinParty(party);
  }


  @Override public synchronized void leaveParty(User user, Party party)
  {
      ArrayList<Participant> participants = party.getParticipants();
      for (int i = 0; i < participants.size(); i++)
      {
        if (participants.get(i).getUser().equals(user))
        {
          participants.remove(i);
          user.leaveParty(party);
          break;
        }
      }
  }

  @Override public synchronized void deleteParty(Party party)
  {

  }


  @Override public synchronized void manageParty(Party party, String title,
      String description, String location)
  {
    if (party == null)
    {
      return;
    }
    party.setName(title);
    party.setDescription(description);
    party.setLocation(location);
  }

  @Override public synchronized void addParticipant(Party party, Participant participant)
  {
    if (party == null || participant == null)
    {
      return;
    }

    PartyUsersDAO partyUsersDAO = new PartyUsersDAO();
    String userId = participant.getUser().getId();
    String partyId = party.getId();

    if (!partyUsersDAO.isMember(userId, partyId))
    {
      partyUsersDAO.add(userId, partyId, "participant");
    }
  }

  @Override public synchronized void removeParticipant(Party party, Participant participant)
  {
    if (party == null || participant == null)
    {
      return;
    }

    if (party.getOrganizer() != null &&
        party.getOrganizer().getId().equals(participant.getUser().getId()))
    {
      return;
    }

    new PartyUsersDAO().remove(participant.getUser().getId(), party.getId());

    if (participant.getParty() == party)
    {
      participant.setParty(null);
    }
  }


  // ModelManager
  @Override public void updateParty(Party party, String name, String description, String location) {
    party.setName(name);
    party.setDescription(description);
    party.setLocation(location);
    new PartyDAO().update(party.getId(), name, description, location);
  }

  @Override public void updatePartyDate(Party party, String date) {
    new PartyDAO().updateDate(party.getId(), date);
  }

  @Override public void addItem(Party party, String name) {
    String id = "item-" + UUID.randomUUID().toString().substring(0, 8);
    new ItemDAO().create(id, name, 1, party.getId());
  }

  @Override public void removeItem(Item item) {
    new ItemDAO().delete(item.getId());
  }

  @Override public void addOption(Party party, String proposal) {
    String id = "opt-" + UUID.randomUUID().toString().substring(0, 8);
    new OptionDAO().create(id, proposal, party.getId());
  }

  @Override public void removeOption(Option option) {
    new OptionDAO().delete(option.getOptionid());
  }




  private void fireError(String message) {
    support.firePropertyChange("error", null, message);
  }

  @Override public void addListener(String propertyName, PropertyChangeListener listener)
  {
    support.addPropertyChangeListener(propertyName, listener);
  }

  @Override public void removeListener(String propertyName,
      PropertyChangeListener listener)
  {
    support.removePropertyChangeListener(propertyName, listener);
  }

  @Override
  public synchronized User createAccount(String username, String password, String confirmPassword, String mail) {
    if (username == null || username.trim().isEmpty()) { fireError("Username is required.");               return null; }
    if (password == null || password.isEmpty())        { fireError("Password is required.");               return null; }
    if (mail == null || mail.trim().isEmpty())          { fireError("Email is required.");                 return null; }
    if (confirmPassword == null || confirmPassword.isEmpty()) { fireError("Please confirm your password."); return null; }
    if (!password.equals(confirmPassword))              { fireError("Passwords do not match.");            return null; }

    UserDAO userDAO = new UserDAO();
    if (userDAO.getByUsername(username) != null) { fireError("Username is already taken."); return null; }

    String userId = userDAO.create(UUID.randomUUID().toString(), username, mail, PasswordUtil.hash(password));
    if (userId == null) { fireError("Failed to create account. Please try again."); return null; }
    return new User(userId, username, PasswordUtil.hash(password), mail);
  }
  @Override public List<User> getAllUsers()
  {
    return new UserDAO().getAll();
  }



}
