package shared.model.service;

import shared.model.Item;
import shared.model.Party;
import java.util.List;

public interface ItemService extends ObservableService {
  List<Item> getItems(Party party);
  List<Item> addItem(Party party, String name);
  List<Item> removeItem(Item item);
  List<Item> claimItem(String itemId, String userId);
  List<Item> unclaimItem(String itemId);
}