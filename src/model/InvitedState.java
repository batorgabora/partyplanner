package model;

public class InvitedState implements ParticipantState {

  @Override
  public void accept(Participant participant) {
    participant.setState(new JoinedState());
  }

  @Override
  public void decline(Participant participant) {
    participant.setState(new LeftState());
  }

  @Override
  public void leave(Participant participant) {
    // cannot leave if not joined
  }

  @Override
  public String getState() {
    return "invited";
  }
}