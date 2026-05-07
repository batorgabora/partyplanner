package model;

import java.util.ArrayList;
import java.util.List;

public class ModelManager implements  PartyModel
{
  private List<User> users;
  private List<Party> parties;

  public ModelManager() {
    this.users = new ArrayList<>();
    this.parties = new ArrayList<>();
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
    if (user == null)
    {
      return new ArrayList<>(parties);
      // returns all of the current partys in the system if there is no user requesting it
    }

    return new ArrayList<>(user.getPartyList());
    // returns the specific users partys
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
  private void addDemoData()
  {
    User user1 = new User("anna", "1234");
    User user2 = new User("mikkel", "1234");
    User user3 = new User("sara", "1234");

    users.add(user1);
    users.add(user2);
    users.add(user3);

    addFriend(user1, user2);

    Organizer organizer1 = new Organizer("", null);
    Party party1 = new Party("Beach Party", "Bring snacks and towels.", "Amager Beach", organizer1);
    organizer1.setParty(party1);

    Organizer organizer2 = new Organizer("", null);
    Party party2 = new Party("Game Night", "Board games and pizza.", "Campus Lounge", organizer2);
    organizer2.setParty(party2);

    parties.add(party1);
    parties.add(party2);

    joinParty(user1, party1);
    joinParty(user2, party1);
    joinParty(user3, party2);
  }
}
