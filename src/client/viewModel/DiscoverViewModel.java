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

public class DiscoverViewModel implements PropertyChangeListener
{
  private PartyModel model;

  //for avoiding wiring them together --> shared selectedVinyl
  private ObjectProperty<Party> selectedParty;
  private ObservableList<Party> parties;
  private final StringProperty errorProperty;// joined
  private ObservableList<Party> invites;  // invited


  public DiscoverViewModel(PartyModel model, ObjectProperty<Party> selectedParty){
    //for wiring them together --> common selected
    this.selectedParty = selectedParty;
    this.model = model;
    errorProperty = new SimpleStringProperty("");
    model.addListener("getAll", this);
    parties = FXCollections.observableArrayList();  // initialize empty
  }


  public ObjectProperty<Party> selectedPartyProperty() {
    return selectedParty;
  }
  public Party getSelectedParty() {
    return selectedParty.get();
  }

  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    Platform.runLater(() -> {
      parties.setAll(model.getParties(LocalUser.getUser()));
    });
  }



  public ObservableList<Party> getParties() {
    return parties;
  }

  public void updateParties() {
    parties.setAll(model.getParties(LocalUser.getUser()));
  }

}
