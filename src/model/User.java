package model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import dao.UserDAO;

public class User {
  private String username;
  private String password;
  private String mail;
  private String id;
  private ArrayList<User> friendList;
  private ArrayList<Party> partyList;

  public User(String id, String username, String password, String mail) {
    this.username = username;
    this.password = password;
    this.mail = mail;
    this.id = id;
    this.friendList = new ArrayList<>();
    this.partyList = new ArrayList<>();
  }

  public User(String username, String password, String mail) {
    this.username = username;
    this.password = password;
    this.mail = mail;
    this.id = UUID.randomUUID().toString();
    this.friendList = new ArrayList<>();
    this.partyList = new ArrayList<>();
  }


  public String getUsername() { return username; }
  public String getPassword() { return password; }
  public String getMail() { return mail; }
  public ArrayList<User> getFriendList() { return friendList; }
  public ArrayList<Party> getPartyList() { return partyList; }
  public String getId()
  {
    return id;
  }


  public void setUsername(String username) { this.username = username; }
  public void setPassword(String password) { this.password = password; }
  public void setMail(String mail){this.mail = mail;}


  public void addFriend(User user) { friendList.add(user); }
  public void removeFriend(User user) { friendList.remove(user); }
  public void joinParty(Party party) { partyList.add(party); }
  public void leaveParty(Party party) { partyList.remove(party); }

  public String toString(){
    return username;
  }
}