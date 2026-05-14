package client.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

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
  @FXML private Button chatButton;
  @FXML private Button addfriendButton;
  @FXML private VBox chatWindow;
  @FXML private VBox chatMessages;
  @FXML private TextField chatInput;
  @FXML private Label userLabel;
  @FXML private ImageView loadingIndicator;
  @FXML private Label infoLabel;
  @FXML private Button voteButton;
  @FXML private Button removeVoteButton;
  @FXML private Button claimButton;

  private Party selected;

  public void init(ViewHandler viewhandler, PartyViewModel viewmodel, Region root)
  {
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;

    leaveButton.setVisible(false);
    chatButton.setVisible(false);
    chatWindow.setVisible(false);
    descriptionLabel.setVisible(false);
    descriptionLabel.setManaged(false);

    timeList.setCellFactory(lv -> new ListCell<Option>() {
      @Override
      protected void updateItem(Option option, boolean empty) {
        super.updateItem(option, empty);
        setText(empty || option == null ? null : option.getProposal() + " (" + option.getVoteCount() + " votes)");
      }
    });

    itemList.setCellFactory(lv -> new ListCell<Item>() {
      @Override
      protected void updateItem(Item item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) { setText(null); return; }
        setText(item.isClaimed()
            ? item.getName() + " — claimed by " + item.getClaimedBy()
            : item.getName() + " — unclaimed");
      }
    });

    itemList.getSelectionModel().selectedItemProperty().addListener(
        (obs, oldVal, newVal) -> updateClaimButton(newVal));

    loadParty();
  }

  public void loadParty()
  {
    selected = viewmodel.getSelectedParty();
    if (selected == null) return;

    userLabel.setText(LocalUser.getUser().getUsername());
    nameLabel.setText(selected.getName());

    setContentVisible(false);

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
        chatButton.setVisible(isOrganizer || isAccepted);

        if (hasVoted) {
          infoLabel.setText("you have already voted");
          voteButton.setDisable(true);
          removeVoteButton.setDisable(false);
        } else {
          infoLabel.setText("");
          voteButton.setDisable(false);
          removeVoteButton.setDisable(true);
        }

        updateClaimButton(null);
        setContentVisible(true);
      });
    }).start();
  }

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
    claimButton.setVisible(visible);
    if (!visible) {
      editButton.setVisible(false);
      acceptButton.setVisible(false);
      declineButton.setVisible(false);
      leaveButton.setVisible(false);
      claimButton.setVisible(false);
    }
  }

  private void updateClaimButton(Item item)
  {
    if (item == null) {
      claimButton.setText("claim");
      claimButton.setDisable(true);
      return;
    }
    String currentUser     = LocalUser.getUser().getUsername();
    boolean claimedByMe    = item.isClaimed() && item.getClaimedBy().equals(currentUser);
    boolean claimedByOther = item.isClaimed() && !claimedByMe;

    claimButton.setText(claimedByMe ? "unclaim" : "claim");
    claimButton.setDisable(claimedByOther);
  }

  @FXML public void onClaim()
  {
    Item item = itemList.getSelectionModel().getSelectedItem();
    if (item == null) {
      infoLabel.setText("select an item first");
      return;
    }

    String currentUser  = LocalUser.getUser().getUsername();
    boolean claimedByMe = item.isClaimed() && item.getClaimedBy().equals(currentUser);

    if (claimedByMe) item.unclaim();
    else             item.claim(currentUser);

    itemList.refresh();
    updateClaimButton(item);

    new Thread(() -> {
      if (claimedByMe) viewmodel.unclaimItem(item.getId());
      else             viewmodel.claimItem(item.getId());
    }).start();
  }

  @FXML public void onVote()
  {
    Option option = timeList.getSelectionModel().getSelectedItem();
    if (option == null) {
      infoLabel.setText("select an option first");
      return;
    }
    if (viewmodel.hasVotedInParty(selected.getId())) {
      infoLabel.setText("you already voted, remove your vote first");
      return;
    }
    viewmodel.voteForOption(option.getOptionid());
    infoLabel.setText("vote cast!");
    loadParty();
  }

  @FXML public void onRemoveVote()
  {
    Option option = timeList.getSelectionModel().getSelectedItem();
    if (option == null) {
      infoLabel.setText("select an option first");
      return;
    }
    viewmodel.removeVote(option.getOptionid());
    infoLabel.setText("vote removed");
    loadParty();
  }

  private boolean chatOpen = false;
  @FXML public void onChat()
  {
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
    } else {
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

  @FXML public void onDiscover()   { viewhandler.openView("discover"); }
  @FXML public void onFriends()    { viewhandler.openView("friends"); }
  @FXML public void onMyParties()  { viewhandler.openView("my parties"); }
  @FXML public void onLogOut()     { viewhandler.openView("login"); }
  @FXML public void addFriend()    { viewhandler.openView("friends"); }
  @FXML public void onEditParty()  { viewhandler.openView("edit party"); }

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