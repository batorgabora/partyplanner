package client.viewModel;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import shared.model.User;
import shared.model.service.UserService;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class RegisterViewModel implements PropertyChangeListener {

  private final UserService model;
  private final StringProperty username        = new SimpleStringProperty("");
  private final StringProperty password        = new SimpleStringProperty("");
  private final StringProperty confirmPassword = new SimpleStringProperty("");
  private final StringProperty mail            = new SimpleStringProperty("");
  private final StringProperty error           = new SimpleStringProperty("");

  public RegisterViewModel(UserService model) {
    this.model = model;
    model.addListener("error", this);
  }

  @Override public void propertyChange(PropertyChangeEvent evt) {
    if ("error".equals(evt.getPropertyName())) {
      Platform.runLater(() -> error.set((String) evt.getNewValue()));
    }
  }

  public boolean createAccount() {
    if (username.get().isBlank()) { error.set("username is required"); return false; }
    if (password.get().isBlank()) { error.set("password is required"); return false; }
    if (!password.get().equals(confirmPassword.get())) { error.set("passwords do not match"); return false; }
    if (mail.get().isBlank()) { error.set("email is required"); return false; }

    User user = model.createAccount(username.get(), password.get(), confirmPassword.get(), mail.get());
    if (user != null) return true;
    error.set("could not create account");
    return false;
  }

  public StringProperty usernameProperty()        { return username; }
  public StringProperty passwordProperty()        { return password; }
  public StringProperty confirmPasswordProperty() { return confirmPassword; }
  public StringProperty mailProperty()            { return mail; }
  public StringProperty errorProperty()           { return error; }
}