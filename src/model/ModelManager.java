package model;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import dao.*;
import util.PasswordUtil;

public class ModelManager implements  PartyModel
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

  @Override public void addFriend(User user, User friend)
  {
    if (!user.getFriendList().contains(friend))
    {
      user.addFriend(friend);
      friend.addFriend(user);
    }
  }

  @Override public void removeFriend(User user, User friend)
  {
    user.removeFriend(friend);
    friend.removeFriend(user);
  }

  @Override public ArrayList<Party> getParties(User user)
  {
    if (user == null) return new ArrayList<>();
    PartyDAO partyDAO = new PartyDAO();
    return partyDAO.getByUser(user.getId());
  }


  @Override public List<Item> getItems(Party party) {
    return new ItemDAO().getByParty(party.getId());
  }


  @Override public String getRole(User user, Party party) {
    return new PartyUsersDAO().getRole(user.getId(), party.getId());
  }


  @Override public List<Participant> getParticipants(Party party) {
    if (party == null) return new ArrayList<>();
    return new PartyUsersDAO().getParticipantsByParty(party.getId());
  }

  @Override public List<Option> getOptions(Party party) {
    return new OptionDAO().getByParty(party.getId());
  }

  @Override public Party createParty(String name, String description,
      String location, String organizerId)
  {
    String partyId = UUID.randomUUID().toString();
    PartyDAO partyDAO = new PartyDAO();
    partyDAO.create(partyId, name, description, location, LocalDate.now());
    return partyDAO.getById(partyId);
  }

  @Override public Party getParty(int id)
  {
    if (id < 0 || id >= parties.size())
    {
      return null;
    }
    return parties.get(id);
  }

  @Override public void joinParty(User user, Party party)
  {
    Participant participant = new Participant(party, user);
    party.addParticipant(participant);
    user.joinParty(party);
  }


  @Override public void leaveParty(User user, Party party)
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

  @Override public void deleteParty(Party party)
  {

  }


  @Override public void manageParty(Party party, String title,
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

  @Override public void addParticipant(Party party, Participant participant)
  {
    if (party == null || participant == null)
    {
      return;
    }

    if (!party.getParticipants().contains(participant))
    {
      party.addParticipant(participant);
      participant.setParty(party);
    }
  }

  @Override public void removeParticipant(Party party, Participant participant)
  {
    if (party == null || participant == null)
    {
      return;
    }

    party.removeParticipant(participant);

    if (participant.getParty() == party)
    {
      participant.setParty(null);
    }
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
  public User createAccount(String username, String password, String confirmPassword, String mail) {
    if (username == null || username.trim().isEmpty()) { System.out.println("FAIL: username"); return null; }
    if (password == null || password.isEmpty()) { System.out.println("FAIL: password"); return null; }
    if (mail == null || mail.trim().isEmpty()) { System.out.println("FAIL: mail"); return null; }
    if (confirmPassword == null || confirmPassword.isEmpty()) { System.out.println("FAIL: confirmPassword"); return null; }
    if (!password.equals(confirmPassword)) { System.out.println("FAIL: passwords don't match"); return null; }

    UserDAO userDAO = new UserDAO();
    if (userDAO.getByUsername(username) != null) { System.out.println("FAIL: username taken"); return null; }

    System.out.println("Creating user: " + username + " mail: " + mail);
    String userId = userDAO.create(UUID.randomUUID().toString(), username, mail, PasswordUtil.hash(password));
    System.out.println("Created with id: " + userId);
    return new User(userId, username, PasswordUtil.hash(password), mail);
  }

}
