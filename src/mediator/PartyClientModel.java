package mediator;

public class PartyClientModel
{
  private final PartyClient client;
  private final Gson gson;

  public PartyClientModel(String host, int port) {
    this.client = new PartyClient(host, port);
    this.gson = new Gson();
  }
  @Override public User login(String username, String password)
  {
    return null;
  }

  @Override public void addFriend(User user, User friend)
  {

  }

  @Override public void removeFriend(User user, User friend)
  {

  }

  @Override public List<Party> getParties(User user)
  {
    return List.of();
  }

  @Override public Party getParty(int id)
  {
    return null;
  }

  @Override public void joinParty(User user, Party party)
  {

  }

  @Override public void leaveParty(User user, Party party)
  {

  }

  @Override public void deleteParty(Party party)
  {

  }

  @Override public void manageParty(Party party, String title,
      String description, String location)
  {

  }

  @Override public void addParticipant(Party party, Participant participant)
  {

  }

  @Override public void removeParticipant(Party party, Participant participant)
  {

  }
}
