package viewModel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.LocalUser;
import model.Party;
import model.PartyModel;
import model.User;
import util.PasswordUtil;

public class LoginViewModel
{
  private PartyModel model;
  private final StringProperty username = new SimpleStringProperty("");
  private final StringProperty password = new SimpleStringProperty("");
  private final StringProperty error = new SimpleStringProperty("");

  public LoginViewModel(PartyModel model){
    this.model = model;


  }

  public boolean login() {
    System.out.printf(username + " " + password);
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