package client.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;

import shared.model.*;
import client.viewModel.FriendsViewModel;

public class FriendsController
{
  private Region root;
  private FriendsViewModel viewmodel;
  private ViewHandler viewhandler;

  @FXML private ListView<User> friendsList;
  @FXML private ComboBox<User> nonFriendsDrop;
  @FXML private Button addfriendButton;
  @FXML private Button removefriendButton;
  @FXML private Label userLabel;
  @FXML private Label statusLabel;

  public void init(ViewHandler viewhandler, FriendsViewModel viewmodel, Region root) {
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;

    userLabel.setText(LocalUser.getUser().getUsername());

    // show username in the friends list
    friendsList.setCellFactory(lv -> new ListCell<User>() {
      @Override protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);
        setText(empty || user == null ? null : user.getUsername());
      }
    });

    // show username in the dropdown
    nonFriendsDrop.setCellFactory(lv -> new ListCell<User>() {
      @Override protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);
        setText(empty || user == null ? null : user.getUsername());
      }
    });
    nonFriendsDrop.setButtonCell(new ListCell<User>() {
      @Override protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);
        setText(empty || user == null ? null : user.getUsername());
      }
    });

    loadData();
  }

  private void loadData() {
    friendsList.setItems(viewmodel.getFriends());
    nonFriendsDrop.setItems(viewmodel.getNonFriends());
  }

  public void reset() {
    loadData();
  }

  @FXML public void onAddFriend() {
    User selected = nonFriendsDrop.getSelectionModel().getSelectedItem();
    if (selected == null) return;
    viewmodel.addFriend(selected);
    loadData();
  }

  @FXML public void onRemoveFriend() {
    User selected = friendsList.getSelectionModel().getSelectedItem();
    if (selected == null) return;
    viewmodel.removeFriend(selected);
    loadData();
  }

  @FXML public void onDiscover()  { viewhandler.openView("discover"); }
  @FXML public void onFriends()   { viewhandler.openView("friends"); }
  @FXML public void onMyParties() { viewhandler.openView("my parties"); }
  @FXML public void onLogOut()    { viewhandler.openView("login"); }
  @FXML public void addFriend()   { onAddFriend(); }

  public Region getRoot() { return root; }
}