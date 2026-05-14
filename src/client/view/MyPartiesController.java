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

import java.util.HashMap;
import java.util.Map;

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
  @FXML private ImageView loadingIndicator;

  public void init(ViewHandler viewHandler, MyPartiesViewModel viewModel, Region root)
  {
    this.root = root;
    this.viewModel = viewModel;
    this.viewHandler = viewHandler;

    errorLabel.textProperty().bind(viewModel.errorProperty());
    userLabel.setText(LocalUser.getUser().getUsername());

    partyList.getSelectionModel().selectedItemProperty().addListener(
        (obs, oldVal, newVal) -> viewModel.selectedPartyProperty().set(newVal));

    loadParties();
  }

  private void loadParties()
  {
    partyList.setVisible(false);
    loadingIndicator.setVisible(true);

    new Thread(() -> {
      viewModel.updateParties();
      var items = viewModel.getMyParties();

      // fetch all roles off UI thread so cell factory doesn't make server calls while rendering
      Map<String, String> roles = new HashMap<>();
      for (Party party : items) {
        roles.put(party.getId(), viewModel.getRoleForParty(party));
      }

      Platform.runLater(() -> {
        partyList.setCellFactory(lv -> new ListCell<Party>() {
          @Override
          protected void updateItem(Party party, boolean empty) {
            super.updateItem(party, empty);
            if (empty || party == null) { setText(null); setStyle(""); return; }
            String role = roles.getOrDefault(party.getId(), "participant");
            setText(party.getName() + " (" + role + ")");
            setStyle("organizer".equals(role) ? "-fx-font-weight: bold;" : "-fx-text-fill: inherit;");
          }
        });
        partyList.setItems(items);
        partyList.setVisible(true);
        loadingIndicator.setVisible(false);
      });
    }).start();
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

  public Region getRoot() { return root; }
  public void reset()     { loadParties(); }
}