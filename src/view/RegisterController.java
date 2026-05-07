package view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import viewModel.LoginViewModel;
import viewModel.RegisterViewModel;

public class RegisterController
{

  private Region root;
  private RegisterViewModel viewmodel;
  private ViewHandler viewhandler;

  private TextField usernameField;
  private TextField emailField;
  private TextField passwordField;
  private TextField confirmpasswordField;
  private Button registerButton;
  private Button backtologinButton;

  public void init(ViewHandler viewhandler, RegisterViewModel viewmodel, Region root){
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;

    //bindings to viewmodel
  }

  @FXML public void onRegister() {
//    registration logic
    viewhandler.openView("login");
  }
  @FXML public void onLogin() {
    //    back to login
    viewhandler.openView("login");
  }

  public Region getRoot()
  {
    return root;
  }

  public void reset(){}

}