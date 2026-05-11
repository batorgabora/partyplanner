package client.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

  private Party selected;



  public void init(ViewHandler viewhandler, EditPartyViewModel viewmodel, Region root){
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;

    selected = viewmodel.getSelectedParty();
    if (selected == null) return;

    loadParty();
  }

  public void loadParty() {
    if (selected == null) return;
    nameField.setText(selected.getName());
    descriptionField.setText(selected.getDescription());
    itemList.setItems(viewmodel.getItems());
    memberList.setItems(viewmodel.getMembers());
    roleLabel.setText(viewmodel.getRoleForCurrentUser(viewmodel.getSelectedParty().getId()));
    dateField.setText(selected.getDate());
    locationField.setText(selected.getLocation());
    timeList.setItems(viewmodel.getOptions());

    userLabel.setText(LocalUser.getUser().getUsername());

    userDropdown.setItems(viewmodel.getAllUsers());
    statusLabel.setText("");

    nameField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
      if (!isNowFocused) {
        // lost focus - save it
        viewmodel.updateName(nameField.getText());
      }
    });

    descriptionField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
      if (!isNowFocused) {
        // lost focus - save it
        viewmodel.updateDescription(descriptionField.getText());
      }
    });

    locationField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
      if (!isNowFocused) {
        // lost focus - save it
        viewmodel.updateLocation(locationField.getText());
      }
    });

    dateField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
      if (!isNowFocused) {
        // lost focus - save it
        viewmodel.updateDate(dateField.getText());
      }
    });
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
  @FXML public void onLogOut() {viewhandler.openView("login");}
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
    if (selected == null) { statusLabel.setText("Select an item first"); return; }
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
    if (selected == null) { statusLabel.setText("Select an option first"); return; }
    viewmodel.removeOption(selected);
    timeList.setItems(viewmodel.getOptions());
  }

  @FXML public void onAddParticipant()
  {
    User selectedUser = userDropdown.getSelectionModel().getSelectedItem();

    if (selectedUser == null)
    {
      statusLabel.setText("Select a user first");
      return;
    }

    if (viewmodel.isAlreadyParticipant(selectedUser))
    {
      statusLabel.setText("User is already in the party");
      return;
    }

    viewmodel.addParticipant(selectedUser);
    memberList.setItems(viewmodel.getMembers());
    statusLabel.setText(selectedUser.getUsername() + " added to the party");
  }

  @FXML public void onRemoveParticipant()
  {
    Participant selectedParticipant = memberList.getSelectionModel().getSelectedItem();

    if (selectedParticipant == null)
    {
      statusLabel.setText("Select a participant first");
      return;
    }

    if (selected.getOrganizer() != null &&
        selected.getOrganizer().getId().equals(selectedParticipant.getUser().getId()))
    {
      statusLabel.setText("Organizer cannot be removed");
      return;
    }

    viewmodel.removeParticipant(selectedParticipant);
    memberList.setItems(viewmodel.getMembers());
    statusLabel.setText(selectedParticipant.getUser().getUsername() + " removed from the party");
  }


  public Region getRoot()
  {
    return root;
  }

  public void reset(){
    loadParty();
  }

}
