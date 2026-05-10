package client.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import shared.model.LocalUser;
import client.viewModel.DiscoverViewModel;
import shared.model.Party;
import client.viewModel.MyPartiesViewModel;

public class MyPartiesController
{

  private Region root;
  private MyPartiesViewModel viewmodel;
  private ViewHandler viewhandler;

  @FXML private ListView partyList;
  @FXML private Button furtherButton;
  @FXML private Label selectedLabel;
  @FXML private Label userLabel;




  public void init(ViewHandler viewhandler, MyPartiesViewModel viewmodel, Region root){
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;

    //bindings to viewmodel
    userLabel.setText(LocalUser.getUser().getUsername());

  }

  @FXML public void onMyParties() {
    viewhandler.openView("my parties");
  }

  @FXML public void onDiscover() {
    viewhandler.openView("discover");
  }

  @FXML public void onFriends() {
    viewhandler.openView("friends");
  }

  @FXML public void onFurther() {
    viewhandler.openView("party");
  }

  @FXML public void onCreateParty() {
    viewhandler.openView("create party");
  }

  @FXML public void onLogout() {
    viewhandler.openView("login");
  }

  public Region getRoot()
  {
    return root;
  }

  public void reset(){}

}
