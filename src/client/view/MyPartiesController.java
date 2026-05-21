package client.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import shared.model.LocalUser;
import shared.model.Party;
import client.viewModel.MyPartiesViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyPartiesController {

  private Region root;
  private MyPartiesViewModel viewModel;
  private ViewHandler viewHandler;

  @FXML private ListView<Party> partyList;
  @FXML private Button furtherButton;
  @FXML private Button createButton;
  @FXML private Label selectedLabel;
  @FXML private Label userLabel;
  @FXML private Label errorLabel;
  @FXML private ImageView loadingIndicator;
  private volatile boolean loading = false;
  //always read from and written to main memory rather than a thread's local CPU cache
  //every read and write goes straight to main memory so all threads always see the latest value instantly

  public void init(ViewHandler viewHandler, MyPartiesViewModel viewModel, Region root) {
    this.root = root;
    this.viewModel = viewModel;
    this.viewHandler = viewHandler;

    errorLabel.textProperty().bind(viewModel.errorProperty());

    // bind list once
    partyList.setItems(viewModel.partiesProperty());

    partyList.setCellFactory(lv -> new ListCell<Party>() {
      @Override protected void updateItem(Party party, boolean empty) {
        super.updateItem(party, empty);
        if (empty || party == null) { setText(null); setStyle(""); return; }
        boolean isOrganizer = party.getOrganizer() != null &&
            party.getOrganizer().getId().equals(LocalUser.getUser().getId());
        setText(party.getName() + (isOrganizer ? " (organizer)" : ""));
        setStyle(isOrganizer ? "-fx-font-weight: bold;" : "");
      }
    });

    partyList.getSelectionModel().selectedItemProperty().addListener(
        (obs, oldVal, newVal) -> viewModel.selectedPartyProperty().set(newVal));

    partyList.getSelectionModel().selectedItemProperty().addListener(
        (obs, oldVal, newVal) -> selectedLabel.setText(newVal != null ? newVal.getName() : ""));

    loadParties();
  }

  private void loadParties() {
    userLabel.setText(LocalUser.getUser().getUsername());
    partyList.setVisible(false);
    selectedLabel.setVisible(false);
    createButton.setVisible(false);
    furtherButton.setVisible(false);
    loadingIndicator.setVisible(true);

    new Thread(() -> {
      viewModel.updateParties();
      Platform.runLater(() -> {
        partyList.setVisible(true);
        selectedLabel.setVisible(true);
        createButton.setVisible(true);
        furtherButton.setVisible(true);
        loadingIndicator.setVisible(false);
      });
    }).start();
  }

  @FXML public void onFurther() {
    if (viewModel.getSelectedParty() == null) {
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

  public Region getRoot() { return root; }
  public void reset()     { loadParties(); }
}