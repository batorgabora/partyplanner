package client.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import shared.model.*;
import client.viewModel.PartyViewModel;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
  @FXML private ScrollPane chatScrollPane;
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

    chatInput.textProperty().bindBidirectional(viewmodel.messageInputProperty());

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
    loadMessages();
  }

  public void loadParty()
  {
    userLabel.setText(LocalUser.getUser().getUsername());

    selected = viewmodel.getSelectedParty();
    if (selected == null) return;

    nameLabel.setText(selected.getName());

    setContentVisible(false);

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
      infoLabel.setStyle("-fx-text-fill: red;");
      return;
    }
    if (viewmodel.hasVotedInParty(selected.getId())) {
      infoLabel.setText("you already voted, remove your vote first");
      infoLabel.setStyle("-fx-text-fill: red;");
      return;
    }
    viewmodel.voteForOption(option.getOptionid());
    infoLabel.setText("vote cast!");
    infoLabel.setStyle("-fx-text-fill: green;");
    loadParty();
  }

  @FXML public void onRemoveVote()
  {
    Option option = timeList.getSelectionModel().getSelectedItem();
    if (option == null) {
      infoLabel.setText("select an option first");
      infoLabel.setStyle("-fx-text-fill: red;");
      return;
    }
    viewmodel.removeVote(option.getOptionid());
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

  @FXML public void onSendMessage() {
    if (viewmodel.messageInputProperty().get().trim().isEmpty()) return;
    new Thread(() -> {
      viewmodel.sendMessage();
      List<Message> messages = viewmodel.getMessages();
      Platform.runLater(() -> {
        viewmodel.messageInputProperty().set("");
        renderMessages(messages);
      });
    }).start();
  }

  private void loadMessages() {
    new Thread(() -> {
      List<Message> messages = viewmodel.getMessages();
      Platform.runLater(() -> renderMessages(messages));
    }).start();
  }
  
  private void renderMessages(List<Message> messages) {
    chatMessages.getChildren().clear();
    String myId = LocalUser.getUser().getId();
    for (Message msg : messages) {
      boolean isOwn  = myId.equals(msg.getUserId());
      String  sender = isOwn ? "You" : resolveUsername(msg.getUserId());
      String  time   = extractTime(msg.getSentAt());

      Label senderLabel = new Label(sender);
      senderLabel.getStyleClass().add("chat-sender");
      Label timeLabel = new Label(time);
      timeLabel.getStyleClass().add("chat-time");

      HBox header = new HBox(6);
      if (isOwn) {
        header.setAlignment(Pos.CENTER_RIGHT);
        header.getChildren().addAll(timeLabel, senderLabel);
      } else {
        header.getChildren().addAll(senderLabel, timeLabel);
      }

      Label bubble = new Label(msg.getContent());
      bubble.setWrapText(true);
      bubble.setPrefWidth(190);
      bubble.getStyleClass().add(isOwn ? "chat-bubble-own" : "chat-bubble-other");

      VBox msgBox = new VBox(2);
      msgBox.getStyleClass().add(isOwn ? "chat-message-own" : "chat-message-other");
      if (isOwn) msgBox.setAlignment(Pos.CENTER_RIGHT);
      msgBox.getChildren().addAll(header, bubble);

      chatMessages.getChildren().add(msgBox);
    }
    Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
  }

  private String resolveUsername(String userId) {
    for (Participant p : memberList.getItems()) {
      if (p.getUser().getId().equals(userId)) return p.getUser().getUsername();
    }
    return userId.length() > 8 ? userId.substring(0, 8) : userId;
  }

  private String extractTime(String sentAt) {
    if (sentAt != null && sentAt.length() >= 16) return sentAt.substring(11, 16);
    return sentAt != null ? sentAt : "";
  }
  
  private boolean chatOpen = false;
  private ScheduledExecutorService messagePollExecutor;
  private ScheduledFuture<?> messagePollTask;

  private void startMessagePolling() {
    if (messagePollExecutor == null || messagePollExecutor.isShutdown()) {
      messagePollExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "message-poll");
        t.setDaemon(true);
        return t;
      });
    }
    messagePollTask = messagePollExecutor.scheduleAtFixedRate(() -> {
      List<Message> messages = viewmodel.getMessages();
      Platform.runLater(() -> renderMessages(messages));
    }, 3, 3, TimeUnit.SECONDS);
  }

  private void stopMessagePolling() {
    if (messagePollTask != null) messagePollTask.cancel(false);
    if (messagePollExecutor != null) messagePollExecutor.shutdownNow();
    messagePollExecutor = null;
  }

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
      loadMessages();
      startMessagePolling();
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
      stopMessagePolling();
    }
  }

  @FXML public void onAccept()
  {
    viewmodel.acceptInvitation();
    viewhandler.openView("party");
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
  public void reset()     { stopMessagePolling(); loadParty();}
}
