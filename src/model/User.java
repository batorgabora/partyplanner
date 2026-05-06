package model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {
  private String username;
  private String password;
  private String id;
  private List<User> friendList;
  private List<Party> partyList;

  public User(String username, String password) {
    this.username = username;
    this.password = password;
    this.id = UUID.randomUUID().toString();
    this.friendList = new ArrayList<>();
    this.partyList = new ArrayList<>();
  }

  // Getters
  public String getUsername() { return username; }
  public String getPassword() { return password; }
  public List<User> getFriendList() { return friendList; }
  public List<Party> getPartyList() { return partyList; }

  // Setters
  public void setUsername(String username) { this.username = username; }
  public void setPassword(String password) { this.password = password; }

  // Methods
  public void addFriend(User user) { friendList.add(user); }
  public void removeFriend(User user) { friendList.remove(user); }
  public void joinParty(Party party) { partyList.add(party); }
  public void leaveParty(Party party) { partyList.remove(party); }
}