package shared.model.service;

import shared.model.Option;
import shared.model.Party;
import java.util.List;

public interface OptionService extends ObservableService {
  List<Option> getOptions(Party party);
  List<Option> addOption(Party party, String proposal);
  List<Option> removeOption(Option option);
  List<Option> voteForOption(String optionId, String userId);
  List<Option> removeVote(String optionId, String userId);
  boolean hasVotedInParty(String userId, String partyId);
  boolean hasVotedForOption(String userId, String optionId);
  String getTopVotedOption(String partyId);
}