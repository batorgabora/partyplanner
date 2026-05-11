package client.viewModel;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import shared.model.LocalUser;
import shared.model.Party;
import shared.model.PartyModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MyPartiesViewModel implements PropertyChangeListener
{
  private PartyModel model;
  private ObjectProperty<Party> selectedParty;
  private final StringProperty error = new SimpleStringProperty("");
  private ObservableList<Party> parties;
  private final StringProperty errorProperty;// joined



  public MyPartiesViewModel(PartyModel model, ObjectProperty<Party> selectedParty){
    //for wiring them together --> common selected
    this.model = model;
    this.selectedParty = selectedParty;

    errorProperty = new SimpleStringProperty("");
    model.addListener("getMy", this);
    parties = FXCollections.observableArrayList();  // initialize empty
    // Register as listener for all 3 event types.
    // From this point on, whenever the model fires these events,
    // our propertyChange() method below is called automatically.
  }

  public ObjectProperty<Party> selectedPartyProperty() {
    return selectedParty;
  }
  public Party getSelectedParty() {
    return selectedParty.get();
  }

  public void updateParties() {
    parties.setAll(model.getMyParties(LocalUser.getUser()));
  }

  public ObservableList<Party> getMyParties() {
    return parties;
  }

  public StringProperty errorProperty() { return error; }

  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    Platform.runLater(() -> {
      parties.setAll(model.getMyParties(LocalUser.getUser()));
    });
  }

  public String getRoleForParty(Party party) {
    return model.getRole(LocalUser.getUser(), party);
  }
}
