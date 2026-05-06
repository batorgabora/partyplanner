package model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Party {
  private String id;
  private String name;
  private String description;
  private String location;
  private Organizer organizer;
  private List<Participant> participants;

  public Party(String name, String description, String location, Organizer organizer) {
    this.id = UUID.randomUUID().toString();
    this.name = name;
    this.description = description;
    this.location = location;
    this.organizer = organizer;
    this.participants = new ArrayList<>();
  }

  // Getters
  public String getId() { return id; }
  public String getName() { return name; }
  public String getDescription() { return description; }
  public String getLocation() { return location; }
  public Organizer getOrganizer() { return organizer; }
  public List<Participant> getParticipants() { return participants; }

  // Setters
  public void setName(String name) { this.name = name; }
  public void setDescription(String description) { this.description = description; }
  public void setLocation(String location) { this.location = location; }

  // Methods
  public void addParticipant(Participant participant) { participants.add(participant); }
  public void removeParticipant(Participant participant) { participants.remove(participant); }
}