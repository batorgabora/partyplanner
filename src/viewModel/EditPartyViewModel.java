package viewModel;

import dao.PartyUsersDAO;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;
import model.User;

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
    return FXCollections.observableArrayList(
        model.getItems(selectedParty.get())
    );
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
    return FXCollections.observableArrayList(model.getOptions(selectedParty.get()));
  }

  public Party getSelectedParty() {
    return selectedParty.get();
  }

  public String getRoleForCurrentUser(String partyId) {
    return new PartyUsersDAO().getRole(LocalUser.getUser().getId(), partyId);
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


}