package view;

import dao.PartyUsersDAO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;

import model.*;
import viewModel.EditPartyViewModel;
import viewModel.PartyViewModel;

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

  private Party selected;



  public void init(ViewHandler viewhandler, EditPartyViewModel viewmodel, Region root){
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;

    selected = viewmodel.getSelectedParty();

    //bindings to viewmodel
    loadParty();
  }

  public void loadParty() {


    nameField.setText(selected.getName());
    descriptionField.setText(selected.getDescription());
    itemList.setItems(viewmodel.getItems());
    memberList.setItems(viewmodel.getMembers());
    roleLabel.setText(viewmodel.getRoleForCurrentUser(viewmodel.getSelectedParty().getId()));
    dateField.setText(selected.getDate());
    locationField.setText(selected.getDate());
    timeList.setItems(viewmodel.getOptions());

    userDropdown.setItems(viewmodel.getAllUsers());
    statusLabel.setText("");
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