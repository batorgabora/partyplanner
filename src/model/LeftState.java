package model;

public class LeftState implements ParticipantState {

  @Override
  public void accept(Participant participant) {
    // cannot rejoin after leaving
  }

  @Override
  public void decline(Participant participant) {
    // cannot decline after leaving
  }

  @Override
  public void leave(Participant participant) {
    // already left, do nothing
  }

  @Override
  public String getState() {
    return "left";
  }
}