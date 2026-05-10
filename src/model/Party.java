package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Party {
  private String id;
  private String name;
  private String description;
  private String location;
  private User organizer;
  private String date;
  private ArrayList<Participant> participants;
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

  public Party(String name, String description, String location, User organizer) {
    this.id = UUID.randomUUID().toString();
    this.name = name;
    this.description = description;
    this.location = location;
    this.organizer = organizer;
    this.participants = new ArrayList<>();
    this.itemList = new ItemList();
  }

  public Party(String id, String name, String description, String location, User organizer) {
    this.id = id;  // use DB id instead of generating new UUID
    this.name = name;
    this.description = description;
    this.location = location;
    this.organizer = organizer;
  }

  public Party(String id, String name, String description, String location, LocalDate date, User organizer) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.location = location;
    this.date = date != null ? date.toString() : null;
    this.organizer = organizer;
  }

  public String getId() { return id; }
  public String getName() { return name; }
  public String getDescription() { return description; }
  public String getLocation() { return location; }
  public User getOrganizer() { return organizer; }
  public String getDate() { return date; }
  public ArrayList<Participant> getParticipants() { return participants; }
  public ItemList getItemList() { return itemList; }


  public void setName(String name) { this.name = name; }
  public void setDescription(String description) { this.description = description; }
  public void setLocation(String location) { this.location = location; }


  public void addParticipant(Participant participant) { participants.add(participant); }
  public void removeParticipant(Participant participant) { participants.remove(participant); }

  @Override public String toString()
  {
    return  name + " at " + location + "  -  " + description;
  }
}