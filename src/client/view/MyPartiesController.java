package client.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import shared.model.LocalUser;
import shared.model.Party;
import client.viewModel.MyPartiesViewModel;

public class MyPartiesController
{

  private Region root;
  private MyPartiesViewModel viewModel;
  private ViewHandler viewHandler;

  @FXML private ListView<Party> partyList;
  @FXML private Button furtherButton;
  @FXML private Label selectedLabel;
  @FXML private Label userLabel;
  @FXML private Label errorLabel;

  public void init(ViewHandler viewHandler, MyPartiesViewModel viewModel, Region root)
  {
    this.root = root;
    this.viewModel = viewModel;
    this.viewHandler = viewHandler;

    //bindings to viewModel
    errorLabel.textProperty().bind(viewModel.errorProperty());

    partyList.setItems(viewModel.getMyParties());
    viewModel.updateParties();
    partyList.getSelectionModel().selectedItemProperty().addListener(
        (obs, oldVal, newVal) -> viewModel.selectedPartyProperty().set(newVal));
    userLabel.setText(LocalUser.getUser().getUsername());
    
    partyList.setCellFactory(lv -> new ListCell<Party>() {
      @Override
      protected void updateItem(Party party, boolean empty) {
        super.updateItem(party, empty);
        if (empty || party == null) {
          setText(null);
          setStyle("");
        } else {
          String role = viewModel.getRoleForParty(party);
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
    viewHandler.openView("my parties");
  }

  @FXML public void onFurther()
  {
    if (viewModel.getSelectedParty() == null)
    {
      selectedLabel.setText("please select a party first");
      return;
    }
    viewHandler.openView("party");
  }

  @FXML public void onMyParties()  { viewHandler.openView("my parties"); }
  @FXML public void onDiscover()   { viewHandler.openView("discover"); }
  @FXML public void onFriends()    { viewHandler.openView("friends"); }
  @FXML public void onCreateParty(){ viewHandler.openView("create party"); }
  @FXML public void onLogout()     { viewHandler.openView("login"); }

  @FXML public void onCreateParty() {
    viewHandler.openView("create party");
  }
  
  public Region getRoot() { return root; }

  public void reset()
  {
    viewModel.updateParties();
  }
}