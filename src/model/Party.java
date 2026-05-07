package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Party {
  private String id;
  private String name;
  private String description;
  private String location;
  private Organizer organizer;
  private List<Participant> participants;
  private ItemList itemList;

  @Override public boolean equals(Object o)
  {
    if (o == null || getClass() != o.getClass())
      return false;
    Party party = (Party) o;
    return Objects.equals(id, party.id) && Objects.equals(name, party.name)
        && Objects.equals(description, party.description) && Objects.equals(
        location, party.location) && Objects.equals(organizer, party.organizer)
        && Objects.equals(participants, party.participants) && Objects.equals(
        itemList, party.itemList);
  }

  @Override public int hashCode()
  {
    return Objects.hash(id, name, description, location, organizer,
        participants, itemList);
  }

  public Party(String name, String description, String location, Organizer organizer) {
    this.id = UUID.randomUUID().toString();
    this.name = name;
    this.description = description;
    this.location = location;
    this.organizer = organizer;
    this.participants = new ArrayList<>();
    this.itemList = new ItemList();

  }

  // Getters
  public String getId() { return id; }
  public String getName() { return name; }
  public String getDescription() { return description; }
  public String getLocation() { return location; }
  public Organizer getOrganizer() { return organizer; }
  public List<Participant> getParticipants() { return participants; }
  public ItemList getItemList() { return itemList; }

  // Setters
  public void setName(String name) { this.name = name; }
  public void setDescription(String description) { this.description = description; }
  public void setLocation(String location) { this.location = location; }

  // Methods
  public void addParticipant(Participant participant) { participants.add(participant); }
  public void removeParticipant(Participant participant) { participants.remove(participant); }

  @Override public String toString()
  {
    return "Party{" + "id='" + id + '\'' + ", name='" + name + '\''
        + ", description='" + description + '\'' + ", location='" + location
        + '\'' + ", organizer=" + organizer;
  }
}