package shared.model.service;

import shared.model.Party;
import shared.model.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines operations for creating, updating, and managing parties and party
 * membership state.
 *  * @author Loke Hansen
 *  * @author Victor Tonu
 *  * @author Marko Stokic
 *  * @author Mike Lorenzen
 *  * @author Bator Gabora
 */
public interface PartyService extends ObservableService {
  /**
   * Returns the parties that currently belong to the given user.
   * @param user the user whose parties should be retrieved
   * @return a list of parties the user is part of
   */
  ArrayList<Party> getMyParties(User user);

  /**
   * Returns parties the user has been invited to but has not yet joined as an
   * accepted participant.
   * @param user the invited user
   * @return a list of invited parties
   */
  List<Party> getInvitedParties(User user);

  /**
   * Returns a party by its identifier.
   * @param id the party identifier
   * @return the matching party, or null if no party is available for
   * the id
   */
  Party getParty(int id);

  /**
   * Creates a new party and assigns the specified user as its organizer.
   * @param name the name of the party
   * @param description the description of the party
   * @param location the location of the party
   * @param organizerId the id of the user creating the party
   * @param date the scheduled date of the party
   * @return the created party, or null if creation fails
   */
  Party createParty(String name, String description, String location, String organizerId, LocalDate date);

  /**
   * Creates a new party without explicitly providing a date.
   * @param name the name of the party
   * @param description the description of the party
   * @param location the location of the party
   * @param organizerId the id of the user creating the party
   * @return the created party, or null if creation fails
   */
  Party createParty(String name, String description, String location, String organizerId);

  /**
   * Updates the basic editable details of an existing party.
   * @param party the party to update
   * @param name the new party name
   * @param description the new party description
   * @param location the new party location
   */
  void updateParty(Party party, String name, String description, String location);

  /**
   * Updates the stored date of an existing party.
   * @param party the party to update
   * @param date the new date value to store
   */
  void updatePartyDate(Party party, String date);

  /**
   * Deletes the given party.
   * @param party the party to delete
   */
  void deleteParty(Party party);

  /**
   * Applies temporary or local edits to the visible details of a party.
   * @param party the party being managed
   * @param title the title to display for the party
   * @param description the description to display for the party
   * @param location the location to display for the party
   */
  void manageParty(Party party, String title, String description, String location);

  /**
   * Adds the given user to the specified party.
   * @param user the user joining the party
   * @param party the party to join
   */
  void joinParty(User user, Party party);

  /**
   * Removes the given user from the specified party.
   * @param user the user leaving the party
   * @param party the party to leave
   */
  void leaveParty(User user, Party party);

  /**
   * Marks an invitation as accepted for the given user and party.
   * @param user the invited user
   * @param party the party whose invitation is being accepted
   */
  void acceptInvite(User user, Party party);

  /**
   * Marks an invitation as declined for the given user and party.
   * @param user the invited user
   * @param party the party whose invitation is being declined
   */
  void declineInvite(User user, Party party);

  /**
   * Returns the stored invitation or membership status for a user in a party.
   * @param user the user whose status should be checked
   * @param party the party to check
   * @return the status value for the user in the party, or null if no
   * status is available
   */
  String getStatus(User user, Party party);

  /**
   * Returns the stored role for a user in a party.
   * @param user the user whose role should be checked
   * @param party the party to check
   * @return the role value for the user in the party, or null if no
   * role is available
   */
  String getRole(User user, Party party);
}
