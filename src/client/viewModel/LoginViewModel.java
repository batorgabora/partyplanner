package client.viewModel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import shared.model.LocalUser;
import shared.model.Party;
import shared.model.PartyModel;
import shared.model.User;
import shared.util.PasswordUtil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LoginViewModel implements PropertyChangeListener
{
  private PartyModel model;
  private final StringProperty username = new SimpleStringProperty("");
  private final StringProperty password = new SimpleStringProperty("");
  private final StringProperty error = new SimpleStringProperty("");

  public LoginViewModel(PartyModel model){
    this.model = model;


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

  @Override public void propertyChange(PropertyChangeEvent evt)
  {

  }
}
