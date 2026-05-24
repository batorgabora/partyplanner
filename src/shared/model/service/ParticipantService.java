package shared.model.service;

import shared.model.Participant;
import shared.model.Party;
import java.util.List;

/**
 * Defines operations for viewing and editing party participants.
 *  * @author Loke Hansen
 *  * @author Victor Tonu
 *  * @author Marko Stokic
 *  * @author Mike Lorenzen
 *  * @author Bator Gabora
 */
public interface ParticipantService extends ObservableService {
  /**
   * Returns all visible participants of a party.
   * @param party the party whose participants should be retrieved
   * @return the list of participants for the party
   */
  List<Participant> getParticipants(Party party);

  /**
   * Adds a participant to a party and returns the updated participant list.
   * @param party the party to add the participant to
   * @param participant the participant to add
   * @return the updated list of participants for the party
   */
  List<Participant> addParticipant(Party party, Participant participant);

  /**
   * Removes a participant from a party and returns the updated participant
   * list.
   * @param party the party to remove the participant from
   * @param participant the participant to remove
   * @return the updated list of participants for the party
   */
  List<Participant> removeParticipant(Party party, Participant participant);
}
