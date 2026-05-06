package model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Participant extends User {
  private String id;
  private Party party;

  public Participant(String id, String username, String password, Party party) {
    super(username, password);
    this.id = UUID.randomUUID().toString();
    this.party = party;
  }

  // Getters
  public String getId() { return id; }
  public Party getParty() { return party; }

  // setters
  public void setParty(Party party) { this.party = party; }
}