package model;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ModelManager implements  PartyModel
{
  private List<User> users;
  private List<Party> parties;
  private final PropertyChangeSupport support = new PropertyChangeSupport(this);

  public ModelManager() {
    this.users = new ArrayList<>();
    this.parties = new ArrayList<>();
    //addDemoData();
  }

  @Override public User login(String username, String password)
  {
    for (int i = 0; i < users.size(); i++)
    {
      User user = users.get(i);
      if (user.getUsername().equals(username) && user.getPassword().equals(password))
      {
        return user;
      }
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

  @Override public List<Party> getParties(User user)
  {
    if (user == null) return new ArrayList<>();
    PartyDAO partyDAO = new PartyDAO();
    return partyDAO.getByUser(user.getId());
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
    List<Participant> participants = party.getParticipants();
    for (int i = 0; i < participants.size(); i++) {
      if (participants.get(i).getUser().equals(user)) {
        participants.remove(i);
        break;
      }
    }
    user.leaveParty(party);
  }

  @Override public void deleteParty(Party party)
  {
    parties.remove(party);
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

  @Override public User createAccount(String username, String password, String confirmPassword)
  {
    if (username == null || username.trim().isEmpty())
    {
      return null;
    }

    if (password == null || password.isEmpty())
    {
      return null;
    }

    if (confirmPassword == null || confirmPassword.isEmpty())
    {
      return null;
    }

    if (!password.equals(confirmPassword))
    {
      return null;
    }

    for (int i = 0; i < users.size(); i++)
    {
      User user = users.get(i);
      if (user.getUsername().equals(username))
      {
        return null;
      }
    }

    String id = UUID.randomUUID().toString();
    User newUser = new User(id, username, password);
    users.add(newUser);
    return newUser;
  }

  @Override public ArrayList<Party> getInvites(User user)
  {
    if (user == null) return new ArrayList<>();
    // TODO: implement DB fetch for pending invites
    return new ArrayList<>();
  }

  @Override public void declineInvite(User user, Party party)
  {

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
