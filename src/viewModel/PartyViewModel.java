package viewModel;

import javafx.beans.property.ObjectProperty;
import model.Party;
import model.PartyModel;

public class PartyViewModel
{
  private PartyModel model;
  //for avoiding wiring them together --> shared selectedVinyl
  private ObjectProperty<Party> selectedParty;

  public PartyViewModel(PartyModel model,  ObjectProperty<Party> selectedParty){
    this.model = model;

    this.selectedParty = selectedParty;

    // Register as listener for all 3 event types.
    // From this point on, whenever the model fires these events,
    // our propertyChange() method below is called automatically.
  }
}