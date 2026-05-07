package model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Participant {

  private String id;
  private Party party;

  public Participant(Party party) {
    this.id = UUID.randomUUID().toString();
    this.party = party;
  }

  // Getters
  public String getId() { return id; }
  public Party getParty() { return party; }

  // setters
  public void setParty(Party party) { this.party = party; }
}