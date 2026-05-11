package client.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import shared.model.LocalUser;
import client.viewModel.DiscoverViewModel;
import shared.model.Party;
import client.viewModel.MyPartiesViewModel;

public class MyPartiesController
{

  private Region root;
  private MyPartiesViewModel viewmodel;
  private ViewHandler viewhandler;

  @FXML private ListView<Party> partyList;
  @FXML private Button furtherButton;
  @FXML private Label selectedLabel;
  @FXML private Label userLabel;
  @FXML private Label errorLabel;




  public void init(ViewHandler viewhandler, MyPartiesViewModel viewmodel, Region root){
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;

    //bindings to viewmodel
    errorLabel.textProperty().bind(viewmodel.errorProperty());

    partyList.setItems(viewmodel.getMyParties());
    viewmodel.updateParties();
    partyList.getSelectionModel().selectedItemProperty().addListener(
        (obs, oldVal, newVal) -> viewmodel.selectedPartyProperty().set(newVal));
    userLabel.setText(LocalUser.getUser().getUsername());

//    partyList.getSelectionModel().selectedItemProperty().addListener(
//        (obs, oldVal, newVal) -> {
//          if (newVal != null) {
//            selectedLabel.setText(newVal.toString());
//          }
//          else {
//            selectedLabel.setText("no selected party");
//          }
//        }
//    );
    partyList.setCellFactory(lv -> new ListCell<Party>() {
      @Override
      protected void updateItem(Party party, boolean empty) {
        super.updateItem(party, empty);
        if (empty || party == null) {
          setText(null);
          setStyle("");
        } else {
          String role = viewmodel.getRoleForParty(party);
          setText(party.getName() + " (" + role + ")");
          if ("organizer".equals(role)) {
            setStyle("-fx-font-weight: bold;"); //-fx-text-fill: #1E4A45;
          } else {
            setStyle("-fx-text-fill: inherit;");
          }
        }
      }
    });

  }

  @FXML public void onMyParties() {
    viewhandler.openView("my parties");
  }

  @FXML public void onDiscover() {
    viewhandler.openView("discover");
  }

  @FXML public void onFriends() {
    viewhandler.openView("friends");
  }

  @FXML public void onFurther() {
    if (viewmodel.getSelectedParty() == null) {
      selectedLabel.setText("please select a party first");
      return;
    }
    viewhandler.openView("party");
  }

  @FXML public void onCreateParty() {
    viewhandler.openView("create party");
  }

  @FXML public void onLogout() {
    viewhandler.openView("login");
  }

  public Region getRoot()
  {
    return root;
  }

  public void reset() {
    viewmodel.updateParties();
  }

}
