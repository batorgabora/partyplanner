package shared.model;

import java.util.UUID;

public class Item
{
  private String id;
  private String name;
  private boolean claimed;
  private String claimedBy; // changed from Participant to String

  public Item(String name) {
    this.id = UUID.randomUUID().toString();
    this.name = name;
  }
  private String partyId;

  public Item(String id, String name, String partyId) {
    this.id = id;
    this.name = name;
    this.partyId = partyId;
  }

  public String getPartyId() { return partyId; }

  public String getId()        { return id; }
  public String getName()      { return name; }
  public boolean isClaimed()   { return claimed; }
  public String getClaimedBy() { return claimedBy; } // changed return type

  public void claim(String username) {
    this.claimed = true;
    this.claimedBy = username;
  }

  public void unclaim() {
    this.claimed = false;
    this.claimedBy = null;
  }

  @Override public String toString() { return name; }
}