package viewModel;

import javafx.beans.property.ObjectProperty;
import model.Party;
import model.PartyModel;

public class MyPartiesViewModel
{
  private PartyModel model;



  public MyPartiesViewModel(PartyModel model){
    //for wiring them together --> common selected

    this.model = model;

    // Register as listener for all 3 event types.
    // From this point on, whenever the model fires these events,
    // our propertyChange() method below is called automatically.
  }
}