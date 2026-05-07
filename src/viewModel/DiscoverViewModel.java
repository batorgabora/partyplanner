package viewModel;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.LocalUser;
import model.Party;
import model.PartyModel;

public class DiscoverViewModel
{
  private PartyModel model;

  //for avoiding wiring them together --> shared selectedVinyl
  private ObjectProperty<Party> selectedParty;
  private ObservableList<Party> parties;



  public DiscoverViewModel(PartyModel model, ObjectProperty<Party> selectedParty){
    //for wiring them together --> common selected
    this.selectedParty = selectedParty;

    parties = FXCollections.observableArrayList(model.getParties(LocalUser.getUsername()));


    this.model = model;

    // Register as listener for all 3 event types.
    // From this point on, whenever the model fires these events,
    // our propertyChange() method below is called automatically.
  }
}