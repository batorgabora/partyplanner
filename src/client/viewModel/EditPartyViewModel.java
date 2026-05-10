package client.viewModel;

import javafx.beans.property.ObjectProperty;
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



  public EditPartyViewModel(PartyModel model, ObjectProperty<Party> selectedParty){
    //for wiring them together --> common selected
    this.model = model;
    this.selectedParty = selectedParty;
    model.addListener("party", this);
    // Register as listener for all 3 event types.
    // From this point on, whenever the model fires these events,
    // our propertyChange() method below is called automatically.
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    // refresh data
  }


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
