package model;

public interface ParticipantState
{
  void accept(Participant participant);
  void decline(Participant participant);
  void leave(Participant participant);

  String getState();
}