package shared.model.service;

import shared.model.Option;
import shared.model.Party;
import java.util.List;

/**
 * Defines operations for suggestion options and voting within a party.
 *  * @author Loke Hansen
 *  * @author Victor Tonu
 *  * @author Marko Stokic
 *  * @author Mike Lorenzen
 *  * @author Bator Gabora
 */
public interface OptionService extends ObservableService {
  /**
   * Returns all options currently registered for a party.
   * @param party the party whose options should be retrieved
   * @return the list of options for the party
   */
  List<Option> getOptions(Party party);

  /**
   * Adds a new option to a party and returns the updated option list.
   * @param party the party the option belongs to
   * @param proposal the proposal text to add
   * @return the updated list of options for the party
   */
  List<Option> addOption(Party party, String proposal);

  /**
   * Removes an option and returns the updated option list for its party.
   * @param option the option to remove
   * @return the updated list of options for the related party
   */
  List<Option> removeOption(Option option);

  /**
   * Registers a vote for the specified option from the specified user.
   * @param optionId the id of the option being voted for
   * @param userId the id of the user casting the vote
   * @return the updated list of options for the related party
   */
  List<Option> voteForOption(String optionId, String userId);

  /**
   * Removes a user's vote from the specified option.
   * @param optionId the id of the option whose vote should be removed
   * @param userId the id of the user removing the vote
   * @return the updated list of options for the related party
   */
  List<Option> removeVote(String optionId, String userId);

  /**
   * Checks whether a user has already voted in the party.
   * @param userId the id of the user
   * @param partyId the id of the party
   * @return true if the user has voted in the party, otherwise
   * false
   */
  boolean hasVotedInParty(String userId, String partyId);

  /**
   * Checks whether a user has voted for a specific option.
   * @param userId the id of the user
   * @param optionId the id of the option
   * @return true if the user has voted for the option, otherwise
   * false
   */
  boolean hasVotedForOption(String userId, String optionId);

  /**
   * Returns the currently mostly voted option for a party.
   * @param partyId the id of the party
   * @return the mostly voted option description, or an implementation-defined
   * fallback if no winner is available
   */
  String getTopVotedOption(String partyId);
}
