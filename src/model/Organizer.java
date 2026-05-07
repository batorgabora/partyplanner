package model;

import java.util.UUID;

public class Organizer
{
  private String id;
  private Party party;

  public Organizer(String id, Party party)
  {
    this.id = UUID.randomUUID().toString();
    this.party = party;
  }
  // Getters
  public String getId() { return id; }
  public Party getParty() { return party; }

  // setters
  public void setParty(Party party) { this.party = party; }
}
