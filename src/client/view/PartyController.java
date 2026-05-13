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

  @FXML private Button voteButton;
  @FXML private Label topVoteLabel;

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

    var topOption = viewmodel.getTopVotedOption(selected.getId());

    userLabel.setText(LocalUser.getUser().getUsername());
    nameLabel.setText(selected.getName());
    descriptionLabel.setText(selected.getDescription());
    locationLabel.setText(selected.getLocation());
    dateLabel.setText(topOption);

    loadingIndicator.setVisible(true);

    itemList.setItems(viewmodel.getItems());
    memberList.setItems(viewmodel.getMembers());
    timeList.setItems(viewmodel.getOptions());

    String role   = viewmodel.getRoleForCurrentUser(selected.getId());
    String status = viewmodel.getStatusForCurrentUser(selected.getId());

    roleLabel.setText(role != null ? role : "participant");
    editButton.setVisible("organizer".equals(role));

    boolean isInvited  = status == null;
    boolean isAccepted = "accepted".equals(status);
    acceptButton.setVisible(isInvited);
    declineButton.setVisible(isInvited);
    leaveButton.setVisible(isAccepted && !"organizer".equals(role));

    loadingIndicator.setVisible(false);
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

  @FXML public void onVote() {
    Object selected = timeList.getSelectionModel().getSelectedItem();
    if (selected == null) {
      topVoteLabel.setText("select an option first");
      return;
    }
    viewmodel.voteForOption(selected.toString());
    loadParty(); // refresh to show updated vote counts
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
