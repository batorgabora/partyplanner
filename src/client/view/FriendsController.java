package client.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import shared.model.*;
import client.viewModel.FriendsViewModel;

public class FriendsController {

  private Region root;
  private FriendsViewModel viewmodel;
  private ViewHandler viewhandler;

  @FXML private ListView<User> friendsList;
  @FXML private ComboBox<User> nonFriendsDrop;
  @FXML private Button addfriendButton;
  @FXML private Button removefriendButton;
  @FXML private Label userLabel;
  @FXML private Label statusLabel;
  @FXML private Label infoLabel;
  @FXML private ImageView loadingIndicator;

  public void init(ViewHandler viewhandler, FriendsViewModel viewmodel, Region root) {
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;

    userLabel.setText(LocalUser.getUser().getUsername());
    statusLabel.textProperty().bind(viewmodel.errorProperty());

    // bind lists once
    friendsList.setItems(viewmodel.friendsProperty());
    nonFriendsDrop.setItems(viewmodel.nonFriendsProperty());

    friendsList.setCellFactory(lv -> new ListCell<User>() {
      @Override protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);
        setText(empty || user == null ? null : user.getUsername());
      }
    });

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
    userLabel.setText(LocalUser.getUser().getUsername());
    loadingIndicator.setVisible(true);
    friendsList.setVisible(false);
    nonFriendsDrop.setVisible(false);
    addfriendButton.setVisible(false);
    removefriendButton.setVisible(false);
    infoLabel.setVisible(false);

    new Thread(() -> {
      viewmodel.loadData(); // updates observable lists directly
      Platform.runLater(() -> {
        friendsList.setVisible(true);
        nonFriendsDrop.setVisible(true);
        addfriendButton.setVisible(true);
        removefriendButton.setVisible(true);
        infoLabel.setVisible(true);
        loadingIndicator.setVisible(false);
      });
    }).start();
  }

  @FXML public void onAddFriend() {
    User selected = nonFriendsDrop.getSelectionModel().getSelectedItem();
    if (selected == null) return;
    new Thread(() -> {
      viewmodel.addFriend(selected);
      viewmodel.loadData(); // refresh both lists after change
    }).start();
  }

  @FXML public void onRemoveFriend() {
    User selected = friendsList.getSelectionModel().getSelectedItem();
    if (selected == null) return;
    new Thread(() -> {
      viewmodel.removeFriend(selected);
      viewmodel.loadData(); // refresh both lists after change
    }).start();
  }

  @FXML public void onDiscover()  { viewhandler.openView("discover"); }
  @FXML public void onFriends()   { viewhandler.openView("friends"); }
  @FXML public void onMyParties() { viewhandler.openView("my parties"); }
  @FXML public void onLogout()    { viewhandler.openView("login"); }
  @FXML public void addFriend()   { onAddFriend(); }

  public Region getRoot() { return root; }
  public void reset()     { loadData(); }
}