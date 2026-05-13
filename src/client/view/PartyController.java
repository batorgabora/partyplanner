package client.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import shared.model.*;
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
  @FXML private ListView<Option> timeList;
  @FXML private ListView<Participant> memberList;
  @FXML private Button editButton;
  @FXML private Button acceptButton;
  @FXML private Button declineButton; //these thee change based on if party has been accepted or not
  @FXML private Button leaveButton;
  @FXML private Label userLabel;
  @FXML private ImageView loadingIndicator;

  @FXML private Label infoLabel;
  @FXML private Button voteButton;
  @FXML private Button removeVoteButton;

  private Party selected;



  public void init(ViewHandler viewhandler, PartyViewModel viewmodel, Region root) {
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;

    descriptionLabel.setVisible(false);
    descriptionLabel.setManaged(false);

    //bindings to viewmodel
    loadParty();
  }

  public void loadParty() {
    selected = viewmodel.getSelectedParty();
    if (selected == null) return;

    userLabel.setText(LocalUser.getUser().getUsername());
    nameLabel.setText(selected.getName());

    // hide everything while loading
    descriptionLabel.setVisible(false);
    locationLabel.setVisible(false);
    dateLabel.setVisible(false);
    roleLabel.setVisible(false);
    editButton.setVisible(false);
    acceptButton.setVisible(false);
    declineButton.setVisible(false);
    leaveButton.setVisible(false);
    voteButton.setVisible(false);
    removeVoteButton.setVisible(false);
    infoLabel.setVisible(false);
    itemList.setVisible(false);
    memberList.setVisible(false);
    timeList.setVisible(false);
    loadingIndicator.setVisible(true);

    new Thread(() -> {
      var items    = viewmodel.getItems();
      var members  = viewmodel.getMembers();
      var options  = viewmodel.getOptions();
      var role     = viewmodel.getRoleForCurrentUser(selected.getId());
      var status   = viewmodel.getStatusForCurrentUser(selected.getId());
      var hasVoted = viewmodel.hasVotedInParty(selected.getId());
      var topVoted = viewmodel.getTopVotedOption(selected.getId());

      Platform.runLater(() -> {
        dateLabel.setText(topVoted != null && !topVoted.equals("no votes yet") ? topVoted : selected.getDate());
        locationLabel.setText(selected.getLocation());

        itemList.setItems(items);
        memberList.setItems(members);
        timeList.setItems(options);

        timeList.setCellFactory(lv -> new javafx.scene.control.ListCell<Option>() {
          @Override
          protected void updateItem(Option option, boolean empty) {
            super.updateItem(option, empty);
            setText(empty || option == null ? null : option.getProposal() + " (" + option.getVoteCount() + " votes)");
          }
        });

        roleLabel.setText(role != null ? role : "participant");

        boolean isOrganizer = "organizer".equals(role);
        boolean isInvited   = status == null;
        boolean isAccepted  = "accepted".equals(status);

        // show everything back
        dateLabel.setVisible(true);
        locationLabel.setVisible(true);
        roleLabel.setVisible(true);
        itemList.setVisible(true);
        memberList.setVisible(true);
        timeList.setVisible(true);
        voteButton.setVisible(true);
        removeVoteButton.setVisible(true);
        infoLabel.setVisible(true);

        editButton.setVisible("organizer".equals(role));
        acceptButton.setVisible(!isOrganizer && isInvited);
        declineButton.setVisible(!isOrganizer && isInvited);
        leaveButton.setVisible(!isOrganizer && isAccepted);

        if (hasVoted) {
          infoLabel.setText("you have already voted");
          infoLabel.setStyle("-fx-text-fill: green;");
          voteButton.setDisable(true);
          removeVoteButton.setDisable(false);
        } else {
          infoLabel.setText("");
          voteButton.setDisable(false);
          removeVoteButton.setDisable(true);
        }

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

  @FXML public void onVote() {
    shared.model.Option selected = timeList.getSelectionModel().getSelectedItem();
    if (selected == null) {
      infoLabel.setText("select an option first");
      infoLabel.setStyle("-fx-text-fill: red;");
      return;
    }
    if (viewmodel.hasVotedInParty(this.selected.getId())) {
      infoLabel.setText("you already voted, remove your vote first");
      infoLabel.setStyle("-fx-text-fill: red;");
      return;
    }
    viewmodel.voteForOption(selected.getOptionid());
    infoLabel.setText("vote cast!");
    infoLabel.setStyle("-fx-text-fill: green;");
    loadParty();
  }

  @FXML public void onRemoveVote() {
    Option selected = timeList.getSelectionModel().getSelectedItem();
    if (selected == null) {
      infoLabel.setText("select an option first");
      infoLabel.setStyle("-fx-text-fill: red;");
      return;
    }
    viewmodel.removeVote(selected.getOptionid());
    infoLabel.setText("vote removed");
    infoLabel.setStyle("-fx-text-fill: orange;");
    loadParty();
  }

  public Region getRoot()
  {
    return root;
  }

  public void reset(){
    loadParty();
  }

}
