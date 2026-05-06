package viewModel;

import javafx.beans.property.ObjectProperty;
import model.Party;
import model.PartyModel;

public class LoginViewModel
{
  private PartyModel model;

  public LoginViewModel(PartyModel model){
    this.model = model;

    // Register as listener for all 3 event types.
    // From this point on, whenever the model fires these events,
    // our propertyChange() method below is called automatically.
  }
}