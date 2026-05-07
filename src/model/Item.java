package model;

import java.util.UUID;

public class Item
{
  private String id;
  private String name;
  private boolean claimed;
  private Participant claimedBy;

  public Item(String name) {
    this.id = UUID.randomUUID().toString();
    this.name = name;
    this.claimed = false;
    this.claimedBy = null;
  }
  public Item(String id, String name) {
    this.id = id;
    this.name = name;
    this.claimed = false;
    this.claimedBy = null;
  }

  // Getters
  public String getId() { return id; }
  public String getName() { return name; }
  public boolean isClaimed() { return claimed; }
  public Participant getClaimedBy() { return claimedBy; }
  // Methods
  public void claim(Participant participant) {
    this.claimed = true;
    this.claimedBy = participant;
  }

  public void unclaim() {
    this.claimed = false;
    this.claimedBy = null;
  }

  @Override public String toString()
  {
    return name;
  }
}