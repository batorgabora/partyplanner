package shared.model.service;

import shared.model.Participant;
import shared.model.Party;
import java.util.List;

public interface ParticipantService extends ObservableService {
  List<Participant> getParticipants(Party party);
  List<Participant> addParticipant(Party party, Participant participant);
  List<Participant> removeParticipant(Party party, Participant participant);
}