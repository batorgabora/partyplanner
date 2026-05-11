package client.viewModel;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import shared.model.*;
import shared.model.User;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class EditPartyViewModel implements PropertyChangeListener
{
  private PartyModel model;
  private ObjectProperty<Party> selectedParty;
  private final StringProperty error = new SimpleStringProperty("");

  public EditPartyViewModel(PartyModel model, ObjectProperty<Party> selectedParty){
    this.model = model;
    this.selectedParty = selectedParty;
    model.addListener("party", this);
    model.addListener("error", this);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if ("error".equals(evt.getPropertyName())) {
      Platform.runLater(() -> error.set((String) evt.getNewValue()));
    }
  }

  public StringProperty errorProperty() { return error; }
  public void setError(String message)  { error.set(message); }


  public ObservableList<Item> getItems() {
    if (selectedParty.get() == null) return FXCollections.emptyObservableList();
    return FXCollections.observableArrayList(model.getItems(selectedParty.get()));
  }

  public ObservableList<Participant> getMembers() {
    return FXCollections.observableArrayList(
        model.getParticipants(selectedParty.get())
    );
  }

  public String getRole() {
    return model.getRole(LocalUser.getUser(), selectedParty.get());
  }

  public ObservableList<Option> getOptions() {
    if (selectedParty.get() == null) return FXCollections.emptyObservableList();
    return FXCollections.observableArrayList(model.getOptions(selectedParty.get()));
  }

  public Party getSelectedParty() {
    return selectedParty.get();
  }

  public String getRoleForCurrentUser(String partyId) {
    if (selectedParty.get() == null) return null;
    return model.getRole(LocalUser.getUser(), selectedParty.get());
  }
  public ObservableList<User> getAllUsers()
  {
    return FXCollections.observableArrayList(model.getAllUsers());
  }

  public void addParticipant(User user)
  {
    if (user == null || selectedParty.get() == null)
    {
      return;
    }

    model.addParticipant(selectedParty.get(), new Participant(selectedParty.get(), user));
  }

  public void removeParticipant(Participant participant)
  {
    if (participant == null || selectedParty.get() == null)
    {
      return;
    }

    model.removeParticipant(selectedParty.get(), participant);
  }

  public boolean isAlreadyParticipant(User user)
  {
    if (user == null || selectedParty.get() == null)
    {
      return false;
    }

    for (Participant participant : model.getParticipants(selectedParty.get()))
    {
      if (participant.getUser().getId().equals(user.getId()))
      {
        return true;
      }
    }

    return false;
  }

  public void updateName(String name) {
    model.updateParty(selectedParty.get(), name, selectedParty.get().getDescription(), selectedParty.get().getLocation());
  }

  public void updateDescription(String description) {
    model.updateParty(selectedParty.get(), selectedParty.get().getName(), description, selectedParty.get().getLocation());
  }

  public void updateLocation(String location) {
    model.updateParty(selectedParty.get(), selectedParty.get().getName(), selectedParty.get().getDescription(), location);
  }

  public void updateDate(String date) {
    model.updatePartyDate(selectedParty.get(), date);
  }

  public void addItem(String name) {
    model.addItem(selectedParty.get(), name);
  }

  public void removeItem(Item item) {
    model.removeItem(item);
  }

  public void addOption(String proposal) {
    model.addOption(selectedParty.get(), proposal);
  }

  public void removeOption(Option option) {
    model.removeOption(option);
  }
}
