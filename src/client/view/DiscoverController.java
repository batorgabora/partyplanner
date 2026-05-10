package client.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import shared.model.LocalUser;
import client.viewModel.DiscoverViewModel;
import shared.model.Party;

public class DiscoverController
{

  private Region root;
  private DiscoverViewModel viewmodel;
  private ViewHandler viewhandler;

  @FXML private ListView<Party> partyList;
  @FXML private Label selectedLabel;
  @FXML private Button onFurtherButton; // joined parties
  // invited parties
  @FXML private Label userLabel;


  public void init(ViewHandler viewhandler, DiscoverViewModel viewmodel, Region root){
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;

    //bindings to viewmodel
    partyList.setItems(viewmodel.getParties());
    viewmodel.updateParties();
    partyList.getSelectionModel().selectedItemProperty().addListener(
        (obs, oldVal, newVal) -> viewmodel.selectedPartyProperty().set(newVal));
    userLabel.setText(LocalUser.getUser().getUsername());
    partyList.getSelectionModel().selectedItemProperty().addListener(
        (obs, oldVal, newVal) -> {
          if (newVal != null) {
            selectedLabel.setText(newVal.toString());
          }
          else {
            selectedLabel.setText("no selected vinyl");
          }
        }
    );
  }

  @FXML public void onFurther() {
    if (viewmodel.getSelectedParty() == null) {
      selectedLabel.setText("please select a party first");
      return;
    }
    viewhandler.openView("party");
  }







  @FXML private void onMyParties()
  {
    viewhandler.openView("my parties");
  }

  @FXML private void onDiscover()
  {
    viewhandler.openView("discover");
  }

  @FXML private void onFriends()
  {
    viewhandler.openView("friends");
  }

  @FXML private void onLogout()
  {
    viewhandler.openView("login");
  }

  public Region getRoot()
  {
    return root;
  }

  public void reset(){
    viewmodel.updateParties();
  }
}
