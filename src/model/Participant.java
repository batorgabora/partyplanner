package model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Participant {

  private String id;
  private Party party;
  private User user;

  public Participant(Party party, User user) {
    this.id = UUID.randomUUID().toString();
    this.party = party;
    this.user = user;
  }

  // Getters
  public String getId() { return id; }
  public Party getParty() { return party; }

  public User getUser()
  {
    return user;
  }

  // setters
  public void setParty(Party party) { this.party = party; }
}