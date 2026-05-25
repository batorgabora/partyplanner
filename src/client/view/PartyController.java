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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class PartyController {

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
  @FXML private Button claimButton;
  @FXML private Label label1;
  @FXML private Label label2;
  @FXML private Label label3;

  @FXML private Label errorLabel;

  private Party selected;
  private boolean chatOpen = false;

  public void init(ViewHandler viewhandler, PartyViewModel viewmodel, Region root) {
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;

    leaveButton.setVisible(false);
    chatButton.setVisible(false);
    chatWindow.setVisible(false);
    descriptionLabel.setVisible(false);
    descriptionLabel.setVisible(false);

    errorLabel.textProperty().bind(viewmodel.errorProperty());

    chatInput.textProperty().bindBidirectional(viewmodel.messageInputProperty());

    itemList.setItems(viewmodel.itemsProperty());
    memberList.setItems(viewmodel.membersProperty());
    timeList.setItems(viewmodel.optionsProperty());

    timeList.getSelectionModel().selectedItemProperty().addListener(
        (obs, oldVal, newVal) -> {
          if (newVal != null) {
            new Thread(() -> {
              boolean voted = viewmodel.hasVotedForOption(newVal.getOptionid());
              Platform.runLater(() -> {
                voteButton.setDisable(false);
                voteButton.setText(voted ? "remove vote" : "vote");
              });
            }).start();
          } else {
            voteButton.setText("vote");
            voteButton.setDisable(true);
          }
        });

    timeList.setCellFactory(lv -> new ListCell<Option>() {
      @Override protected void updateItem(Option option, boolean empty) {
        super.updateItem(option, empty);
        setText(empty || option == null ? null : option.getProposal() + " (" + option.getVoteCount() + " votes)");
      }
    });

    itemList.setCellFactory(lv -> new ListCell<Item>() {
      @Override protected void updateItem(Item item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) { setText(null); return; }
        setText(item.isClaimed()
            ? item.getName() + " — claimed by " + item.getClaimedBy()
            : item.getName() + " — unclaimed");
      }
    });

    itemList.getSelectionModel().selectedItemProperty().addListener(
        (obs, oldVal, newVal) -> updateClaimButton(newVal));

    viewmodel.messagesProperty().addListener((javafx.collections.ListChangeListener<Message>) change -> {
      while (change.next()) {
        if (change.wasReplaced()) {
          chatMessages.getChildren().clear();
        }
        if (change.wasAdded()) {
          for (Message msg : change.getAddedSubList()) {
            appendMessage(msg);
          }
        }
      }
      Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
    });

    chatMessages.heightProperty().addListener((obs, oldH, newH) ->
        Platform.runLater(() -> chatScrollPane.setVvalue(1.0)));

    loadParty();

    chatInput.setOnKeyPressed(event -> {
      if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
        onSendMessage();
        event.consume();
      }
    });
  }

  public void loadParty() {
    userLabel.setText(LocalUser.getUser().getUsername());
    selected = viewmodel.getSelectedParty();
    if (selected == null) return;

    nameLabel.setText(selected.getName());
    setContentVisible(false);

    new Thread(() -> {
      viewmodel.loadData();
      var role     = viewmodel.getRoleForCurrentUser(selected.getId());
      var status   = viewmodel.getStatusForCurrentUser(selected.getId());
      var topVoted = viewmodel.getTopVotedOption(selected.getId());

      Platform.runLater(() -> {
        dateLabel.setText(topVoted != null && !topVoted.equals("no votes yet")
            ? topVoted : selected.getDate());
        locationLabel.setText(selected.getLocation());
        descriptionLabel.setText(selected.getDescription());
        roleLabel.setText(role != null ? role : "participant");

        boolean isOrganizer = "organizer".equals(role);
        boolean isAccepted  = "accepted".equals(status);
        boolean isInvited = !isOrganizer && (status == null || status.isEmpty());

        setContentVisible(true);

        setContentVisible(true); // sets everything visible first

        // then override specific buttons based on role/status
        editButton.setVisible(isOrganizer);
        acceptButton.setVisible(isInvited);
        declineButton.setVisible(isInvited);
        leaveButton.setVisible(!isOrganizer && isAccepted);
        chatButton.setVisible(isOrganizer || isAccepted);
        voteButton.setVisible(isOrganizer || isAccepted);
        claimButton.setVisible(isOrganizer || isAccepted);

        updateClaimButton(null);
      });
    }).start();
  }

  private void setContentVisible(boolean visible) {
    loadingIndicator.setVisible(!visible);
    descriptionLabel.setVisible(visible);
    descriptionLabel.setManaged(visible);
    dateLabel.setVisible(visible);
    locationLabel.setVisible(visible);
    roleLabel.setVisible(visible);
    itemList.setVisible(visible);
    memberList.setVisible(visible);
    timeList.setVisible(visible);
    infoLabel.setVisible(visible);
    claimButton.setVisible(visible);
    errorLabel.setVisible(visible);
    label1.setVisible(visible);
    label2.setVisible(visible);
    label3.setVisible(visible);
    addfriendButton.setVisible(visible);
    if (!visible) {
      editButton.setVisible(false);
      acceptButton.setVisible(false);
      declineButton.setVisible(false);
      leaveButton.setVisible(false);
      claimButton.setVisible(false);
      voteButton.setVisible(false);
    }
  }

  private void updateClaimButton(Item item) {
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

  @FXML public void onClaim() {
    Item item = itemList.getSelectionModel().getSelectedItem();
    if (item == null) { infoLabel.setText("select an item first"); return; }

    String currentUser  = LocalUser.getUser().getUsername();
    boolean claimedByMe = item.isClaimed() && item.getClaimedBy().equals(currentUser);

    // optimistic UI update — update locally before server confirms
    if (claimedByMe) item.unclaim();
    else             item.claim(currentUser);

    itemList.refresh();
    updateClaimButton(item);

    new Thread(() -> {
      if (claimedByMe) viewmodel.unclaimItem(item.getId());
      else             viewmodel.claimItem(item.getId());
    }).start();
  }

  @FXML public void onVote() {
    Option option = timeList.getSelectionModel().getSelectedItem();
    if (option == null) {
      infoLabel.setVisible(true);
      infoLabel.setText("select an option first");
      infoLabel.setStyle("-fx-text-fill: #ef6464;");
      return;
    }
    boolean isVoting = voteButton.getText().equals("vote");
    if (isVoting) {
      infoLabel.setVisible(true);
      infoLabel.setText("vote cast!");
      infoLabel.setStyle("-fx-text-fill: #4a934a;");
      voteButton.setText("remove vote");
      new Thread(() -> viewmodel.voteForOption(option.getOptionid())).start();
    } else {
      infoLabel.setVisible(true);
      infoLabel.setText("vote removed");
      infoLabel.setStyle("-fx-text-fill: #dca948;");
      voteButton.setText("vote");
      new Thread(() -> viewmodel.removeVote(option.getOptionid())).start();
    }
  }

  @FXML public void addFriend() {
    Participant selected = memberList.getSelectionModel().getSelectedItem();
    if (selected == null) {
      infoLabel.setText("select a member first");
      infoLabel.setStyle("-fx-text-fill: #ef6464;");
      return;
    }
    viewmodel.addFriend(selected.getUser());
    infoLabel.setText(selected.getUser().getUsername() + " added as friend");
    infoLabel.setStyle("-fx-text-fill: #4a934a;");
  }

  @FXML public void onSendMessage() {
    if (viewmodel.messageInputProperty().get().trim().isEmpty()) return;
    new Thread(() -> {
      viewmodel.sendMessage();
      Platform.runLater(() -> viewmodel.messageInputProperty().set(""));
      loadMessages();
    }).start();
  }

  private void loadMessages() {
    new Thread(() -> viewmodel.loadMessages()).start();
  }

  // builds and appends a single message bubble to the chat window
  // called both on initial load and when a new message arrives via Observer
  private void appendMessage(Message msg) {
    String myId   = LocalUser.getUser().getId();
    boolean isOwn = myId.equals(msg.getUserId());
    String sender = isOwn ? "You" : resolveUsername(msg.getUserId());
    String time   = formatSentAt(msg.getSentAt());

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

  // resolves a userId to a username using the already-loaded member list
  // avoids extra network calls for username lookup
  private String resolveUsername(String userId) {
    if (userId == null) return "unknown";
    for (Participant p : memberList.getItems()) {
      if (p.getUser().getId().equals(userId)) return p.getUser().getUsername();
    }
    return userId.length() > 8 ? userId.substring(0, 8) : userId;
  }

  private String formatSentAt(String sentAt) {
    if (sentAt == null || sentAt.isEmpty()) return "";
    try {
      String normalized = sentAt.length() > 19 ? sentAt.substring(0, 19) : sentAt;
      LocalDateTime dt = LocalDateTime.parse(normalized);
      String timeStr = dt.format(DateTimeFormatter.ofPattern("HH:mm"));
      if (dt.toLocalDate().equals(LocalDate.now())) return timeStr;
      return dt.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"));
    } catch (Exception e) {
      return sentAt.length() >= 16 ? sentAt.substring(11, 16) : sentAt;
    }
  }

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
      addfriendButton.setLayoutX(535);
      chatWindow.setVisible(true);
      memberList.setPrefWidth(175);
      // load existing messages when chat opens
      // new messages after this point arrive via Observer (sendMessage broadcast)
      loadMessages();
    } else {
      dateLabel.setLayoutX(685);
      dateLabel.setLayoutY(1);
      locationLabel.setLayoutX(684);
      locationLabel.setLayoutY(22);
      memberList.setPrefHeight(331);
      editButton.setLayoutX(755);
      editButton.setLayoutY(400);
      chatButton.setLayoutX(755);
      chatButton.setLayoutY(439);
      leaveButton.setLayoutX(752);
      leaveButton.setLayoutY(482);
      chatWindow.setVisible(false);
      addfriendButton.setLayoutX(596);
      memberList.setPrefWidth(216);
    }
  }

  @FXML public void onAccept()   { viewmodel.acceptInvitation(); viewhandler.openView("party"); }
  @FXML public void onDecline()  { viewmodel.declineInvitation(); viewhandler.openView("discover"); }
  @FXML public void onLeave()    { viewmodel.leaveParty(); viewhandler.openView("my parties"); }
  @FXML public void onDiscover() { viewhandler.openView("discover"); }
  @FXML public void onFriends()  { viewhandler.openView("friends"); }
  @FXML public void onMyParties(){ viewhandler.openView("my parties"); }
  @FXML public void onLogOut()   { viewhandler.openView("login"); }
  @FXML public void onEditParty(){ viewhandler.openView("edit party"); }
  @FXML public void onLogout()   { viewhandler.openView("login"); }

  public Region getRoot() { return root; }
  public void reset()     { loadParty(); }
}