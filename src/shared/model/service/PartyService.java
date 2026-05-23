package shared.model.service;

import shared.model.Party;
import shared.model.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface PartyService extends ObservableService {
  ArrayList<Party> getMyParties(User user);
  List<Party> getInvitedParties(User user);
  Party getParty(int id);
  Party createParty(String name, String description, String location, String organizerId, LocalDate date);
  Party createParty(String name, String description, String location, String organizerId);
  void updateParty(Party party, String name, String description, String location);
  void updatePartyDate(Party party, String date);
  void deleteParty(Party party);
  void manageParty(Party party, String title, String description, String location);
  void joinParty(User user, Party party);
  void leaveParty(User user, Party party);
  void acceptInvite(User user, Party party);
  void declineInvite(User user, Party party);
  String getStatus(User user, Party party);
  String getRole(User user, Party party);
}