package viewModel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.LocalUser;
import model.Party;
import model.PartyModel;
import model.User;

public class LoginViewModel
{
  private PartyModel model;
  private final StringProperty username = new SimpleStringProperty("");
  private final StringProperty password = new SimpleStringProperty("");
  private final StringProperty error = new SimpleStringProperty("");

  public LoginViewModel(PartyModel model){
    this.model = model;

    // Register as listener for all 3 event types.
    // From this point on, whenever the model fires these events,
    // our propertyChange() method below is called automatically.


  }

  public boolean login() {
    User user = model.login(username.get(), password.get());
    if (user != null) {
      LocalUser.setUser(user);
      return true;
    }
    error.set("Invalid username or password");
    return false;
  }

  public StringProperty usernameProperty() { return username; }
  public StringProperty passwordProperty() { return password; }
  public StringProperty errorProperty() { return error; }
}