package client.view;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import shared.model.*;
import client.viewModel.EditPartyViewModel;
import client.viewModel.PartyViewModel;

import java.util.ArrayList;

public class EditPartyController
{

  private Region root;
  private EditPartyViewModel viewmodel;
  private ViewHandler viewhandler;

  @FXML private Label roleLabel;
  @FXML private TextField nameField;
  @FXML private TextArea descriptionField;
  @FXML private TextField dateField;
  @FXML private TextField locationField;
  @FXML private ListView<Item> itemList;
  @FXML private ListView timeList;
  @FXML private ListView<Participant> memberList;
  @FXML private ComboBox<User> userDropdown;
  @FXML private Label statusLabel;
  @FXML private TextField itemField;
  @FXML private TextField optionField;
  @FXML private Label userLabel;
  @FXML private Button addparticipantButton;
  @FXML private Button removeparticipantButton;
  @FXML private Label label1;
  @FXML private Label label2;
  @FXML private Label label3;
  @FXML private Button minusOption;
  @FXML private Button plusOption;
  @FXML private Button itemMinus;
  @FXML private Button itemPlus;
  @FXML private Button saveButton;
  @FXML private Button deleteButton;

  @FXML private ImageView loadingIndicator;

  private Party selected;



  public void init(ViewHandler viewhandler, EditPartyViewModel viewmodel, Region root){
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;

    selected = viewmodel.getSelectedParty();
    if (selected == null) return;

    userLabel.setText(LocalUser.getUser().getUsername());

    loadParty();
  }

  public void loadParty() {
    userLabel.setText(LocalUser.getUser().getUsername());
    if (selected == null) return;

    nameField.setText(selected.getName());
    descriptionField.setText(selected.getDescription());
    dateField.setText(selected.getDate());
    locationField.setText(selected.getLocation());
    statusLabel.textProperty().bind(viewmodel.errorProperty());

    setContentVisible(false);

    new Thread(() -> {
      var items   = viewmodel.getItems();
      var members = viewmodel.getMembers();
      var options = viewmodel.getOptions();
      var friends = viewmodel.getFriends();
      var role    = viewmodel.getRoleForCurrentUser(selected.getId());

      Platform.runLater(() -> {
        itemList.setItems(items);
        memberList.setItems(members);
        timeList.setItems(options);
        userDropdown.setItems(friends);
        roleLabel.setText(role);

        nameField.focusedProperty().addListener((obs, was, isNow) -> {
          if (!isNow) viewmodel.updateName(nameField.getText());
        });
        descriptionField.focusedProperty().addListener((obs, was, isNow) -> {
          if (!isNow) viewmodel.updateDescription(descriptionField.getText());
        });
        locationField.focusedProperty().addListener((obs, was, isNow) -> {
          if (!isNow) viewmodel.updateLocation(locationField.getText());
        });
        dateField.focusedProperty().addListener((obs, was, isNow) -> {
          if (!isNow) viewmodel.updateDate(dateField.getText());
        });

        setContentVisible(true);
      });
    }).start();
  }

  private void setContentVisible(boolean visible) {
    loadingIndicator.setVisible(!visible);
    itemList.setVisible(visible);
    memberList.setVisible(visible);
    timeList.setVisible(visible);
    userDropdown.setVisible(visible);
    roleLabel.setVisible(visible);
    nameField.setVisible(visible);
    descriptionField.setVisible(visible);
    dateField.setVisible(visible);
    locationField.setVisible(visible);
    addparticipantButton.setVisible(visible);
    removeparticipantButton.setVisible(visible);
    label1.setVisible(visible);
    label2.setVisible(visible);
    label3.setVisible(visible);
    statusLabel.setVisible(visible);
    itemMinus.setVisible(visible);
    itemPlus.setVisible(visible);
    minusOption.setVisible(visible);
    plusOption.setVisible(visible);
    itemField.setVisible(visible);
    optionField.setVisible(visible);
    saveButton.setVisible(visible);
    deleteButton.setVisible(visible);
  }

  @FXML public void onDelete() {
    viewmodel.deleteParty();
    viewhandler.openView("discover");
  }

  @FXML public void onBack() {
    if (viewmodel.getSelectedParty() == null) {
      return;
    }
    viewhandler.openView("party");
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
  @FXML public void onLogout() {viewhandler.openView("login");}
  @FXML public void addFriend() {viewhandler.openView("friends");}

  @FXML public void onAddItem() {
    String name = itemField.getText().trim();
    if (name.isEmpty()) return;
    viewmodel.addItem(name);
    itemList.setItems(viewmodel.getItems());
    itemField.clear();
  }

  @FXML public void onRemoveItem() {
    Item selected = itemList.getSelectionModel().getSelectedItem();
    if (selected == null) { viewmodel.setError("Select an item first"); return; }
    viewmodel.removeItem(selected);
    itemList.setItems(viewmodel.getItems());
  }

  @FXML public void onAddOption() {
    String proposal = optionField.getText().trim();
    if (proposal.isEmpty()) return;
    viewmodel.addOption(proposal);
    timeList.setItems(viewmodel.getOptions());
    optionField.clear();
  }

  @FXML public void onRemoveOption() {
    Option selected = (Option) timeList.getSelectionModel().getSelectedItem();
    if (selected == null) { viewmodel.setError("Select an option first"); return; }
    viewmodel.removeOption(selected);
    timeList.setItems(viewmodel.getOptions());
  }

  @FXML public void onAddParticipant()
  {
    User selectedUser = userDropdown.getSelectionModel().getSelectedItem();

    if (selectedUser == null)
    {
      viewmodel.setError("Select a user first");
      return;
    }

    if (viewmodel.isAlreadyParticipant(selectedUser))
    {
      viewmodel.setError("User is already in the party");
      return;
    }

    viewmodel.addParticipant(selectedUser);
    memberList.setItems(viewmodel.getMembers());
    viewmodel.setError(selectedUser.getUsername() + " added to the party");
  }

  @FXML public void onRemoveParticipant()
  {
    Participant selectedParticipant = memberList.getSelectionModel().getSelectedItem();

    if (selectedParticipant == null)
    {
      viewmodel.setError("Select a participant first");
      return;
    }

    if (selected.getOrganizer() != null &&
        selected.getOrganizer().getId().equals(selectedParticipant.getUser().getId()))
    {
      viewmodel.setError("Organizer cannot be removed");
      return;
    }

    viewmodel.removeParticipant(selectedParticipant);
    memberList.setItems(viewmodel.getMembers());
    viewmodel.setError(selectedParticipant.getUser().getUsername() + " removed from the party");
  }

  public Region getRoot()
  {
    return root;
  }

  public void reset(){
    loadParty();
  }

}
