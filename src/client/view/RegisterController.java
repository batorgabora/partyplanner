package client.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import client.viewModel.RegisterViewModel;

public class RegisterController {

  private Region root;
  private RegisterViewModel viewmodel;
  private ViewHandler viewhandler;

  @FXML private TextField usernameField;
  @FXML private TextField emailField;
  @FXML private PasswordField passwordField;
  @FXML private PasswordField confirmpasswordField;
  @FXML private Button registerButton;
  @FXML private Button backtologinButton;
  @FXML private Label messageLabel;

  public void init(ViewHandler viewhandler, RegisterViewModel viewmodel, Region root) {
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;

    // bind fields
    usernameField.textProperty().bindBidirectional(viewmodel.usernameProperty());
    emailField.textProperty().bindBidirectional(viewmodel.mailProperty());
    passwordField.textProperty().bindBidirectional(viewmodel.passwordProperty());
    confirmpasswordField.textProperty().bindBidirectional(viewmodel.confirmPasswordProperty());

    // bind error label
    messageLabel.textProperty().bind(viewmodel.errorProperty());
  }

  @FXML public void onRegister() {
    if (viewmodel.createAccount()) {
      viewhandler.openView("login");
    }
  }

  @FXML public void onLogout() {
    viewhandler.openView("login");
  }

  public Region getRoot() { return root; }

  public void reset() {
    viewmodel.usernameProperty().set("");
    viewmodel.mailProperty().set("");
    viewmodel.passwordProperty().set("");
    viewmodel.confirmPasswordProperty().set("");
    viewmodel.errorProperty().set("");
  }
}