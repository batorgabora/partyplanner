package viewModel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class PartyViewModel implements PropertyChangeListener
{
  private PartyModel model;
  //for avoiding wiring them together --> shared selectedVinyl
  private ObjectProperty<Party> selectedParty;
  private final StringProperty errorProperty;
  private ObservableList<Participant> members;
  private ObservableList<Item> items;

  public PartyViewModel(PartyModel model,  ObjectProperty<Party> selectedParty){
    this.model = model;

    this.selectedParty = selectedParty;

    // Register as listener for all 3 event types.
    // From this point on, whenever the model fires these events,
    // our propertyChange() method below is called automatically.
    errorProperty = new SimpleStringProperty("");
    model.addListener("something", this);
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

  public Party getSelectedParty() {
    return selectedParty.get();
  }

  @Override public void propertyChange(PropertyChangeEvent evt)
  {

  }
}