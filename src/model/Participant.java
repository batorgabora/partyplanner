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

  public void setState(ParticipantState state) {
    this.state = state;
  }
  public void accept() { state.accept(this); }
  public void decline() { state.decline(this); }
  public void leave() { state.leave(this); }
  public String getState() { return state.getState(); }


}