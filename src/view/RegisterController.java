package view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import viewModel.RegisterViewModel;

public class RegisterController
{
  private Region root;
  private RegisterViewModel viewmodel;
  private ViewHandler viewhandler;

  @FXML private TextField usernameField;
  @FXML private TextField emailField;
  @FXML private TextField passwordField;
  @FXML private TextField confirmpasswordField;
  @FXML private Button registerButton;
  @FXML private Button backtologinButton;
  @FXML private Label messageLabel;

  public void init(ViewHandler viewhandler, RegisterViewModel viewmodel, Region root){
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;
  }

  @FXML public void onRegister() {
    boolean created = viewmodel.createAccount(
        usernameField.getText(),
        passwordField.getText(),
        confirmpasswordField.getText(),
        emailField.getText());

    System.out.println("created: " + created);
    if (created) {
      viewhandler.openView("login");
    }
    else
    {
      messageLabel.setText("Could not create account.");
    }
  }

  @FXML public void onLogin() {
    viewhandler.openView("login");
  }

  public Region getRoot()
  {
    return root;
  }

  public void reset(){
    usernameField.clear();
    emailField.clear();
    passwordField.clear();
    confirmpasswordField.clear();
    messageLabel.setText("");
  }


}
