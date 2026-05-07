package model;

public class Option {
  private int optionid;
  private String proposal;
  private int partyid;

  public Option(int optionid, String proposal, int partyid) {
    this.optionid = optionid;
    this.proposal = proposal;
    this.partyid = partyid;
  }

  public int getOptionid() { return optionid; }
  public String getProposal() { return proposal; }
  public int getPartyid() { return partyid; }

  public void setProposal(String proposal) { this.proposal = proposal; }
}