package shared.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemList {
  private String id;
  private ArrayList<Item> items;

  public ItemList() {
    this.id = UUID.randomUUID().toString();
    this.items = new ArrayList<>();
  }

  // Getters
  public String getId() { return id; }
  public ArrayList<Item> getItems() { return items; }

  // Methods
  public void addItem(Item item) { items.add(item); }
  public void removeItem(Item item) { items.remove(item); }
}
