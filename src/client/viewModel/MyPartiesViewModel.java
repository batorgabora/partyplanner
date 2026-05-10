package client.viewModel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import shared.model.Party;
import shared.model.PartyModel;

public class MyPartiesViewModel
{
  private PartyModel model;
  private ObjectProperty<Party> selectedParty;
  private final StringProperty error = new SimpleStringProperty("");



  public MyPartiesViewModel(PartyModel model, ObjectProperty<Party> selectedParty){
    //for wiring them together --> common selected
    this.model = model;
    this.selectedParty = selectedParty;

    // Register as listener for all 3 event types.
    // From this point on, whenever the model fires these events,
    // our propertyChange() method below is called automatically.
  }

  public StringProperty errorProperty() { return error; }
}
