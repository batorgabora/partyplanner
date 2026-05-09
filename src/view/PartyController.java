package view;

import dao.PartyUsersDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;

import model.Item;
import model.LocalUser;
import model.Participant;
import model.Party;
import viewModel.PartyViewModel;

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
  @FXML private Button leaveButton;
  @FXML private Label userLabel;

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
    nameLabel.setText(selected.getName());
    descriptionLabel.setText(selected.getDescription());
    locationLabel.setText(selected.getLocation());
    dateLabel.setText("whenever");
    itemList.setItems(viewmodel.getItems());
    memberList.setItems(viewmodel.getMembers());
    roleLabel.setText(viewmodel.getRole());
    dateLabel.setText(selected.getDate());
    locationLabel.setText(selected.getLocation());
    timeList.setItems(viewmodel.getOptions());

    String role = viewmodel.getRoleForCurrentUser(viewmodel.getSelectedParty().getId());
    roleLabel.setText(role != null ? role : "participant");
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
  @FXML public void addFriend() {
    //addd friend logic
    viewhandler.openView("friends");
  }




  public Region getRoot()
  {
    return root;
  }

  public void reset(){
    loadParty();
  }

}