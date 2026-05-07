package model;

public class JoinedState implements ParticipantState {

  @Override
  public void accept(Participant participant) {
    // already joined, do nothing
  }

  @Override
  public void decline(Participant participant) {
    // already joined, can't decline
  }

  @Override
  public void leave(Participant participant) {
    participant.setState(new LeftState());
  }

  @Override
  public String getState() {
    return "joined";
  }
}