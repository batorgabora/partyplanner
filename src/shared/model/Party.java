package shared.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a party with its identifying information, organizer, scheduled
 * date, participants, and item list.
 *  * @author Loke Hansen
 *  * @author Victor Tonu
 *  * @author Marko Stokic
 *  * @author Mike Lorenzen
 *  * @author Bator Gabora
 */
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

  /**
   * Creates a party with the provided persisted information.
   * @param id the unique id of the party
   * @param name the party name
   * @param description the party description
   * @param location the party location
   * @param date the date of the party, stored as a string if not null
   * @param organizer the user who organizes the party
   */
  public Party(String id, String name, String description, String location, LocalDate date, User organizer) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.location = location;
    this.date = date != null ? date.toString() : null;
    this.organizer = organizer;
    this.participants = new ArrayList<>();
    this.itemList = new ItemList();
  }

  /**
   * Returns the unique id of the party.
   * @return the party id
   */
  public String getId() { return id; }

  /**
   * Returns the name of the party.
   * @return the party name
   */
  public String getName() { return name; }

  /**
   * Returns the description of the party.
   * @return the party description
   */
  public String getDescription() { return description; }

  /**
   * Returns the location of the party.
   * @return the party location
   */
  public String getLocation() { return location; }

  /**
   * Returns the organizer of the party.
   * @return the organizing user
   */
  public User getOrganizer() { return organizer; }

  /**
   * Returns the stored date value for the party.
   * @return the date string, or null if no date is set
   */
  public String getDate() { return date; }

  /**
   * Updates the name of the party.
   * @param name the new party name
   */
  public void setName(String name) { this.name = name; }

  /**
   * Updates the description of the party.
   * @param description the new party description
   */
  public void setDescription(String description) { this.description = description; }

  /**
   * Updates the location of the party.
   * @param location the new party location
   */
  public void setLocation(String location) { this.location = location; }

  /**
   * Updates the stored date value of the party.
   * @param date the new date string
   */
  public void setDate(String date) { this.date = date; }

  @Override public String toString()
  {
    return  name + " at " + location + "  -  " + description;
  }
}
