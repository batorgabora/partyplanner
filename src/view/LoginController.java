package view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import viewModel.LoginViewModel;

public class LoginController
{

  private Region root;
  private LoginViewModel viewmodel;
  private ViewHandler viewhandler;


  public void init(ViewHandler viewhandler, LoginViewModel viewmodel, Region root){
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;

    //bindings to viewmodel
  }

  @FXML public void onForward() {
    viewhandler.openView("discover");
  }

  public Region getRoot()
  {
    return root;
  }

  public void reset(){}

}