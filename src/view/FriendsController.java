package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import model.*;
import viewModel.FriendsViewModel;
import viewModel.PartyViewModel;

public class FriendsController
{

  private Region root;
  private FriendsViewModel viewmodel;
  private ViewHandler viewhandler;


  @FXML private ListView<User> friendsList;
  @FXML private Button chatButton;
  @FXML private Button removefriendButton;
  @FXML private Label userLabel;


  public void init(ViewHandler viewhandler, FriendsViewModel viewmodel, Region root) {
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;
    friendsList.setItems(viewmodel.getFriends());
    userLabel.setText(LocalUser.getUser().getUsername());
  }

  public void reset() {
    viewmodel.loadFriends(); // called every time the view is shown
  }

  @FXML public void onDiscover() {
    viewhandler.openView("discover");
  }
  @FXML public void onFriends() {
    viewhandler.openView("friends");
  }
  @FXML public void onMyParties() {
    viewhandler.openView("my parties");
  }
  @FXML public void onLogOut() {viewhandler.openView("login");}
  @FXML public void addFriend() {
    //addd friend logic
    viewhandler.openView("friends");
  }

  public Region getRoot()
  {
    return root;
  }

}