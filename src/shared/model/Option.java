package shared.model;

public class Option {
  private String optionid;
  private String proposal;
  private String partyid;
  private int voteCount;

  public Option(String optionid, String proposal, String partyid, int voteCount) {
    this.optionid = optionid;
    this.proposal = proposal;
    this.partyid = partyid;
    this.voteCount = voteCount;
  }
  public Option(String optionid, String proposal, String partyid) {
    this.optionid = optionid;
    this.proposal = proposal;
    this.partyid = partyid;
  }

  public int getVoteCount() { return voteCount; }
  public String getOptionid() { return optionid; }
  public String getProposal() { return proposal; }
  public String getPartyid() { return partyid; }

  @Override
  public String toString() { return proposal; }
}
