package model;

public class Option {
  private String optionid;
  private String proposal;
  private String partyid;

  public Option(String optionid, String proposal, String partyid) {
    this.optionid = optionid;
    this.proposal = proposal;
    this.partyid = partyid;
  }

  public String getOptionid() { return optionid; }
  public String getProposal() { return proposal; }
  public String getPartyid() { return partyid; }

  @Override
  public String toString() { return proposal; }
}