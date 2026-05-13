package client.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

import shared.model.Item;
import shared.model.LocalUser;
import shared.model.Participant;
import shared.model.Party;
import client.viewModel.PartyViewModel;

public class PartyController
{

  private Region root;
  private PartyViewModel viewmodel;
  private ViewHandler viewhandler;

  @FXML private Label roleLabel;
  @FXML private Label nameLabel;
  @FXML private Label descriptionLabel;
  @FXML private Label dateLabel;
  @FXML private Label locationLabel;
  @FXML private ListView<Item> itemList;
  @FXML private ListView timeList;
  @FXML private ListView<Participant> memberList;
  @FXML private Button editButton;
  @FXML private Button acceptButton;
  @FXML private Button declineButton; //these thee change based on if party has been accepted or not
  @FXML private Button leaveButton;
  @FXML private Button chatButton;
  @FXML private Button addfriendButton;
  @FXML private VBox chatWindow;
  @FXML private VBox chatMessages;
  @FXML private TextField chatInput;
  @FXML private Label userLabel;
  @FXML private ImageView loadingIndicator;

  private Party selected;


  public void init(ViewHandler viewhandler, PartyViewModel viewmodel, Region root){
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;
    leaveButton.setVisible(false);
    chatButton.setVisible(false);
    chatWindow.setVisible(false);

    //bindings to viewmodel
    loadParty();
  }

  public void loadParty() {
    selected = viewmodel.getSelectedParty();
    if (selected == null) return;

    // instant — no network, just local data
    userLabel.setText(LocalUser.getUser().getUsername());
    nameLabel.setText(selected.getName());
    descriptionLabel.setText(selected.getDescription());
    locationLabel.setText(selected.getLocation());
    dateLabel.setText(selected.getDate());

    // hide lists, show cat
    itemList.setVisible(false);
    memberList.setVisible(false);
    timeList.setVisible(false);
    loadingIndicator.setVisible(true);

    new Thread(() -> {
      // slow network calls off UI thread
      var items   = viewmodel.getItems();
      var members = viewmodel.getMembers();
      var options = viewmodel.getOptions();
      var role    = viewmodel.getRoleForCurrentUser(selected.getId());
      var status  = viewmodel.getStatusForCurrentUser(selected.getId());

      Platform.runLater(() -> {
        itemList.setItems(items);
        memberList.setItems(members);
        timeList.setItems(options);

        boolean isOrganizer = "organizer".equals(role);
        roleLabel.setText(role != null ? role : "participant");
        editButton.setVisible(isOrganizer);

        boolean isAccepted = "accepted".equals(status);
        boolean isInvited = !isOrganizer && status == null;
        acceptButton.setVisible(isInvited);
        declineButton.setVisible(isInvited);
        leaveButton.setVisible(isAccepted && !isOrganizer);
        chatButton.setVisible(isAccepted || isOrganizer);

        itemList.setVisible(true);
        memberList.setVisible(true);
        timeList.setVisible(true);
        loadingIndicator.setVisible(false);
      });
    }).start();
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
  @FXML public void addFriend() { viewhandler.openView("friends");}
  @FXML public void onEditParty() {viewhandler.openView("edit party");}

  @FXML public void onAccept() {
    viewmodel.acceptInvitation();
    viewhandler.openView("my parties");
  }

  @FXML public void onDecline() {
    viewmodel.declineInvitation();
    viewhandler.openView("discover");
  }

  @FXML public void onLeave() {
    viewmodel.leaveParty();
    viewhandler.openView("my parties");
  }
  private boolean chatOpen = false;
  @FXML public void onChat() {
    chatOpen = !chatOpen;
    if (chatOpen) {
      dateLabel.setLayoutX(509);
      dateLabel.setLayoutY(2);
      locationLabel.setLayoutX(509);
      locationLabel.setLayoutY(28);
      memberList.setPrefHeight(202);
      editButton.setLayoutX(544);
      editButton.setLayoutY(397);
      chatButton.setLayoutX(543);
      chatButton.setLayoutY(439);
      leaveButton.setLayoutX(542);
      leaveButton.setLayoutY(482);
      addfriendButton.setLayoutX(555);
      chatWindow.setVisible(true);
      addfriendButton.setLayoutX(535);
      memberList.setPrefWidth(175);
    }
    else {
      dateLabel.setLayoutX(685);
      dateLabel.setLayoutY(1);
      locationLabel.setLayoutX(684);
      locationLabel.setLayoutY(22);
      memberList.setPrefHeight(331);
      editButton.setLayoutX(785);
      editButton.setLayoutY(342);
      chatButton.setLayoutX(755);
      chatButton.setLayoutY(439);
      leaveButton.setLayoutX(752);
      leaveButton.setLayoutY(482);
      chatWindow.setVisible(false);
      addfriendButton.setLayoutX(596);
      memberList.setPrefWidth(216);
    }
  }

  public Region getRoot()
  {
    return root;
  }

  public void reset(){
    loadParty();
  }

}