package shared.model;

import shared.model.service.*;

/**
 * Combines all shared model service interfaces into a single access point for
 * client and server model implementations.
 *  * @author Loke Hansen
 *  * @author Victor Tonu
 *  * @author Marko Stokic
 *  * @author Mike Lorenzen
 *  * @author Bator Gabora
 */
public interface PartyModel extends UserService, FriendService, PartyService, ItemService, OptionService, MessageService, ParticipantService, PartyViewService, EditPartyViewService {}
