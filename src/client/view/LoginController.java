package client.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import shared.model.LocalUser;
import shared.model.User;
import client.viewModel.LoginViewModel;

public class LoginController
{

  private Region root;
  private LoginViewModel viewmodel;
  private ViewHandler viewhandler;

  @FXML private TextField usernameField;
  @FXML private TextField passwordField;
  @FXML private Button signInButton;
  @FXML private Button registerButton;
  @FXML private Label errorLabel;

  public void init(ViewHandler viewhandler, LoginViewModel viewmodel, Region root){
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;

    //bindings to viewmodel
    usernameField.textProperty().bindBidirectional(viewmodel.usernameProperty());
    passwordField.textProperty().bindBidirectional(viewmodel.passwordProperty());
    errorLabel.textProperty().bind(viewmodel.errorProperty());

    passwordField.setOnKeyPressed(event -> {
      if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
        onForward();
      }
    });

    usernameField.setOnKeyPressed(event -> {
      if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
        onForward();
      }
    });
  }

  @FXML
  public void onForward() {
    if (viewmodel.login()) {
      viewhandler.openView("discover");
    }
  }

  @FXML public void onRegister() {
    viewhandler.openView("register");
  }

  public Region getRoot()
  {
    return root;
  }

  public void reset(){}

  @FXML private void onOpenCreateAccount()
  {
    viewhandler.openView("create account");
  }

  @FXML private ProgressIndicator loadingIndicator;


}
