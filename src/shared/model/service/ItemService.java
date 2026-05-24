package shared.model.service;

import shared.model.Item;
import shared.model.Party;
import java.util.List;

/**
 * Defines operations for managing party item lists and item claiming.
 *  * @author Loke Hansen
 *  * @author Victor Tonu
 *  * @author Marko Stokic
 *  * @author Mike Lorenzen
 *  * @author Bator Gabora
 */
public interface ItemService extends ObservableService {
  /**
   * Returns all items associated with a party.
   * @param party the party whose items should be retrieved
   * @return the list of items for the party
   */
  List<Item> getItems(Party party);

  /**
   * Adds a new item to a party and returns the updated item list.
   * @param party the party the item belongs to
   * @param name the display name of the item
   * @return the updated list of items for the party
   */
  List<Item> addItem(Party party, String name);

  /**
   * Removes an item and returns the updated item list for its party.
   * @param item the item to remove
   * @return the updated list of items for the related party
   */
  List<Item> removeItem(Item item);

  /**
   * Marks an item as claimed by a user and returns the updated item list.
   * @param itemId the id of the item to claim
   * @param userId the id of the user claiming the item
   * @return the updated list of items for the related party
   */
  List<Item> claimItem(String itemId, String userId);

  /**
   * Removes the claim from an item and returns the updated item list.
   * @param itemId the id of the item to unclaim
   * @return the updated list of items for the related party
   */
  List<Item> unclaimItem(String itemId);
}
