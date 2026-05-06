package model;

import java.util.ArrayList;
import java.util.List;

public class Participant extends User {
  private int id;
  private Party party;

  public Participant(int id, String username, String password, Party party) {
    super(username, password);
    this.id = id;
    this.party = party;
  }

  // Getters
  public int getId() { return id; }
  public Party getParty() { return party; }

  // Setters
  public void setId(int id) { this.id = id; }
  public void setParty(Party party) { this.party = party; }
}