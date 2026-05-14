package shared.util;

public enum Action {
  GET_ALL("getAll"),
  GET_MY_PARTIES("getMyParties"),
  GET_INVITED_PARTIES("getInvitedParties"),
  LOGIN("login"),
  CREATE_ACCOUNT("createAccount"),
  GET_ALL_USERS("getAllUsers"),
  ADD_FRIEND("addFriend"),
  JOIN_PARTY("joinParty"),
  LEAVE_PARTY("leaveParty"),
  ACCEPT_INVITE("acceptInvite"),
  DECLINE_INVITE("declineInvite"),
  GET_STATUS("getStatus"),
  GET_ROLE("getRole"),
  CREATE_PARTY("createParty"),
  UPDATE_PARTY("updateParty"),
  UPDATE_PARTY_DATE("updatePartyDate"),
  DELETE_PARTY("deleteParty"),
  GET_ITEMS("getItems"),
  ADD_ITEM("addItem"),
  REMOVE_ITEM("removeItem"),
  GET_OPTIONS("getOptions"),
  ADD_OPTION("addOption"),
  REMOVE_OPTION("removeOption"),
  VOTE_FOR_OPTION("voteForOption"),
  REMOVE_VOTE("removeVote"),
  GET_TOP_VOTED_OPTION("getTopVotedOption"),
  HAS_VOTED("hasVoted"),
  HAS_VOTED_FOR_OPTION("hasVotedForOption"),
  GET_PARTICIPANTS("getParticipants"),
  ADD_PARTICIPANT("addParticipant"),
  REMOVE_PARTICIPANT("removeParticipant"),
  CLAIM_ITEM("claimItem"),
  UNCLAIM_ITEM("unclaimItem");

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