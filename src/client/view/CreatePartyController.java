package client.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.util.StringConverter;
import shared.model.LocalUser;
import client.viewModel.CreatePartyViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CreatePartyController {

  private Region root;
  private CreatePartyViewModel viewmodel;
  private ViewHandler viewhandler;

  @FXML private TextField nameField;
  @FXML private TextField descriptionField;
  @FXML private TextField locationField;
  @FXML private DatePicker datePicker;
  @FXML private Label messageLabel;
  @FXML private Label userLabel;

  public void init(ViewHandler viewhandler, CreatePartyViewModel viewmodel, Region root) {
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;

    userLabel.setText(LocalUser.getUser().getUsername());

    // bind fields to viewmodel properties — View never pulls, just binds
    nameField.textProperty().bindBidirectional(viewmodel.nameProperty());
    descriptionField.textProperty().bindBidirectional(viewmodel.descriptionProperty());
    locationField.textProperty().bindBidirectional(viewmodel.locationProperty());
    datePicker.valueProperty().bindBidirectional(viewmodel.dateProperty());

    // bind error label — updates automatically when ViewModel sets error
    messageLabel.textProperty().bind(viewmodel.errorProperty());

    // observe createdParty — navigate automatically when party is created
    viewmodel.createdPartyProperty().addListener((obs, oldVal, newVal) -> {
      if (newVal != null) viewhandler.openView("my parties");
    });
  }

  @FXML public void onCreate() {
    viewmodel.createParty();
  }

  @FXML public void onBack()      { viewhandler.openView("my parties"); }
  @FXML public void onMyParties() { viewhandler.openView("my parties"); }
  @FXML public void onDiscover()  { viewhandler.openView("discover"); }
  @FXML public void onFriends()   { viewhandler.openView("friends"); }
  @FXML public void onLogout()    { viewhandler.openView("login"); }

  public Region getRoot() { return root; }
  public void reset()     { viewmodel.clear(); }
}