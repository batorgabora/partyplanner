package shared.util;

public enum Action {
  GET_ALL("getAll"),
  LOGIN("login"),
  JOIN_PARTY("joinParty"),
  LEAVE_PARTY("leaveParty"),
  CREATE_PARTY("createParty"),
  DELETE_PARTY("deleteParty"),
  ADD_PARTICIPANT("addParticipant"),
  REMOVE_PARTICIPANT("removeParticipant"),
  ADD_FRIEND("addFriend"),
  VOTE_FOR_OPTION("voteForOption"),
  REMOVE_VOTE("removeVote"),
  GET_TOP_VOTED_OPTION("getTopVotedOption");

  private final String value;

  Action(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static Action fromValue(String value) {
    for (Action action : values()) {
      if (action.value.equals(value)) {
        return action;
      }
    }
    throw new IllegalArgumentException("Unknown action: " + value);
  }
}