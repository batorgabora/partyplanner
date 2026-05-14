package client.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
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
  @FXML private Button declineButton;
  @FXML private Button leaveButton;
  @FXML private Label userLabel;
  @FXML private ImageView loadingIndicator;
  @FXML private Label infoLabel;
  @FXML private Button voteButton;
  @FXML private Button removeVoteButton;

  private Party selected;

  public void init(ViewHandler viewhandler, PartyViewModel viewmodel, Region root)
  {
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;

    descriptionLabel.setVisible(false);
    descriptionLabel.setManaged(false);

    // set once — no need to re-apply on every load
    timeList.setCellFactory(lv -> new ListCell<Option>() {
      @Override
      protected void updateItem(Option option, boolean empty) {
        super.updateItem(option, empty);
        setText(empty || option == null ? null : option.getProposal() + " (" + option.getVoteCount() + " votes)");
      }
    });

    loadParty();
  }

  public void loadParty()
  {
    selected = viewmodel.getSelectedParty();
    if (selected == null) return;

    userLabel.setText(LocalUser.getUser().getUsername());
    nameLabel.setText(selected.getName());

    setContentVisible(false); // hide everything, show spinner

    new Thread(() -> {
      // all server calls happen off the UI thread
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
        roleLabel.setText(role != null ? role : "participant");

        itemList.setItems(items);
        memberList.setItems(members);
        timeList.setItems(options);

        boolean isOrganizer = "organizer".equals(role);
        boolean isInvited   = status == null;
        boolean isAccepted  = "accepted".equals(status);

        editButton.setVisible(isOrganizer);
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

        setContentVisible(true); // show everything, hide spinner
      });
    }).start();
  }

  // toggles all content vs the loading spinner
  private void setContentVisible(boolean visible)
  {
    loadingIndicator.setVisible(!visible);
    dateLabel.setVisible(visible);
    locationLabel.setVisible(visible);
    roleLabel.setVisible(visible);
    itemList.setVisible(visible);
    memberList.setVisible(visible);
    timeList.setVisible(visible);
    voteButton.setVisible(visible);
    removeVoteButton.setVisible(visible);
    infoLabel.setVisible(visible);

    // while loading hide all buttons; role logic sets them individually when visible=true
    if (!visible) {
      editButton.setVisible(false);
      acceptButton.setVisible(false);
      declineButton.setVisible(false);
      leaveButton.setVisible(false);
    }
  }

  @FXML public void onVote()
  {
    Option selected = timeList.getSelectionModel().getSelectedItem();
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

  @FXML public void onRemoveVote()
  {
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

  @FXML public void onDiscover()   { viewhandler.openView("discover"); }
  @FXML public void onFriends()    { viewhandler.openView("friends"); }
  @FXML public void onMyParties()  { viewhandler.openView("my parties"); }
  @FXML public void onLogOut()     { viewhandler.openView("login"); }
  @FXML public void addFriend()    { viewhandler.openView("friends"); }
  @FXML public void onEditParty()  { viewhandler.openView("edit party"); }
  @FXML public void onChat() {}

  @FXML public void onAccept()
  {
    viewmodel.acceptInvitation();
    viewhandler.openView("my parties");
  }

  @FXML public void onDecline()
  {
    viewmodel.declineInvitation();
    viewhandler.openView("discover");
  }

  @FXML public void onLeave()
  {
    viewmodel.leaveParty();
    viewhandler.openView("my parties");
  }

  public Region getRoot() { return root; }
  public void reset()     { loadParty(); }
}