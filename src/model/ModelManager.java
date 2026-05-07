package model;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
    System.out.println(user.getPassword() + " mail:" + user.getMail());
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



  //  public void addDemoData()
//  {
//    User user1 = new User("abcc","anna", "1234","stfu@gmail.com");
//    User user2 = new User("abcd", "mikkel", "1234","stfu@gmail.com");
//    User user3 = new User("abce", "sara", "1234","stfu@gmail.com");
//
//    users.add(user1);
//    users.add(user2);
//    users.add(user3);
//
//    addFriend(user1, user2);
//
//    Organizer organizer1 = new Organizer("", null);
//    Party party1 = new Party("Beach Party", "Bring snacks and towels.", "Amager Beach", organizer1);
//    organizer1.setParty(party1);
//    party1.getItemList().addItem(new Item("Chips"));
//    party1.getItemList().addItem(new Item("Soda"));
//    party1.getItemList().addItem(new Item("Blankets"));
//
//    Organizer organizer2 = new Organizer("", null);
//    Party party2 = new Party("Game Night", "Board games and pizza.", "Campus Lounge", organizer2);
//    organizer2.setParty(party2);
//    party2.getItemList().addItem(new Item("Pizza"));
//    party2.getItemList().addItem(new Item("Cards"));
//    party2.getItemList().addItem(new Item("Soft drinks"));
//
//    parties.add(party1);
//    parties.add(party2);
//
//    joinParty(user1, party1);
//    joinParty(user2, party1);
//    joinParty(user3, party2);
//  }


}
