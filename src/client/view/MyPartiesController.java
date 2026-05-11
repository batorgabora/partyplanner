package client.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import shared.model.LocalUser;
import shared.model.Party;
import client.viewModel.MyPartiesViewModel;

public class MyPartiesController
{

  private Region root;
  private MyPartiesViewModel viewModel;
  private ViewHandler viewhandler;

  @FXML private ListView<Party> partyList;
  @FXML private Button furtherButton;
  @FXML private Label selectedLabel;
  @FXML private Label userLabel;
  @FXML private Label errorLabel;

  public void init(ViewHandler viewhandler, MyPartiesViewModel viewModel, Region root)
  {
    this.root = root;
    this.viewModel = viewModel;
    this.viewhandler = viewhandler;

    partyList.setItems(viewModel.getParties());
    viewModel.updateParties();

    partyList.getSelectionModel().selectedItemProperty().addListener(
        (obs, oldVal, newVal) -> {
          viewModel.selectedPartyProperty().set(newVal);
          if (newVal != null)
            selectedLabel.setText(newVal.getName());
          else
            selectedLabel.setText("no selected party");
        });

    userLabel.setText(LocalUser.getUser().getUsername());
    errorLabel.textProperty().bind(viewModel.errorProperty());

  }

  @FXML public void onFurther()
  {
    if (viewModel.getSelectedParty() == null)
    {
      selectedLabel.setText("please select a party first");
      return;
    }
    viewhandler.openView("party");
  }

  @FXML public void onMyParties()  { viewhandler.openView("my parties"); }
  @FXML public void onDiscover()   { viewhandler.openView("discover"); }
  @FXML public void onFriends()    { viewhandler.openView("friends"); }
  @FXML public void onCreateParty(){ viewhandler.openView("create party"); }
  @FXML public void onLogout()     { viewhandler.openView("login"); }

  public Region getRoot() { return root; }

  public void reset()
  {
    viewModel.updateParties();
  }
}