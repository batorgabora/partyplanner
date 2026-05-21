package client.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import shared.model.LocalUser;
import client.viewModel.DiscoverViewModel;
import shared.model.Party;

public class DiscoverController {

  private Region root;
  private DiscoverViewModel viewmodel;
  private ViewHandler viewhandler;

  @FXML private ListView<Party> partyList;
  @FXML private Label selectedLabel;
  @FXML private Button onFurtherButton;
  @FXML private Label userLabel;
  @FXML private ImageView loadingIndicator;

  public void init(ViewHandler viewhandler, DiscoverViewModel viewmodel, Region root) {
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;

    userLabel.setText(LocalUser.getUser().getUsername());

    // bind once — list updates automatically via Observer
    partyList.setItems(viewmodel.partiesProperty());

    partyList.getSelectionModel().selectedItemProperty().addListener(
        (obs, oldVal, newVal) -> viewmodel.selectedPartyProperty().set(newVal));

    partyList.getSelectionModel().selectedItemProperty().addListener(
        (obs, oldVal, newVal) -> selectedLabel.setText(newVal != null ? newVal.getName() : ""));

    loadParties();
  }

  private void loadParties() {
    userLabel.setText(LocalUser.getUser().getUsername());
    partyList.setVisible(false);
    selectedLabel.setVisible(false);
    onFurtherButton.setVisible(false);
    loadingIndicator.setVisible(true);

    new Thread(() -> {
      viewmodel.updateParties(); // updates the observable list directly
      Platform.runLater(() -> {
        partyList.setVisible(true);
        selectedLabel.setVisible(true);
        onFurtherButton.setVisible(true);
        loadingIndicator.setVisible(false);
      });
    }).start();
  }

  @FXML public void onFurther() {
    if (viewmodel.getSelectedParty() == null) {
      selectedLabel.setText("please select a party first");
      return;
    }
    viewhandler.openView("party");
  }

  @FXML private void onMyParties() { viewhandler.openView("my parties"); }
  @FXML private void onDiscover()  { viewhandler.openView("discover"); }
  @FXML private void onFriends()   { viewhandler.openView("friends"); }
  @FXML private void onLogout()    { viewhandler.openView("login"); }

  public Region getRoot() { return root; }
  public void reset()     { loadParties(); }
}