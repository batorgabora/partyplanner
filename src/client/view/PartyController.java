package client.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

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
  @FXML private Label userLabel;
  @FXML private ImageView loadingIndicator;

  private Party selected;



  public void init(ViewHandler viewhandler, PartyViewModel viewmodel, Region root){
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;


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

        roleLabel.setText(role != null ? role : "participant");
        editButton.setVisible("organizer".equals(role));

        boolean isInvited;
        if (status == null) {
          isInvited = true;
        } else {
          isInvited = false;
        }
        boolean isAccepted = "accepted".equals(status);
        acceptButton.setVisible(isInvited);
        declineButton.setVisible(isInvited);
        leaveButton.setVisible(isAccepted && !"organizer".equals(role));

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
  @FXML public void onEditParty() {
    viewhandler.openView("edit party");
  }

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

  public Region getRoot()
  {
    return root;
  }

  public void reset(){
    loadParty();
  }

}
