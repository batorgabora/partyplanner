package view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import model.LocalUser;
import viewModel.CreatePartyViewModel;

public class CreatePartyController
{
  private Region root;
  private CreatePartyViewModel viewmodel;
  private ViewHandler viewhandler;

  @FXML private TextField nameField;
  @FXML private TextField descriptionField;
  @FXML private TextField locationField;
  @FXML private DatePicker datePicker;
  @FXML private Button createButton;
  @FXML private Button backButton;
  @FXML private Label messageLabel;
  @FXML private Label userLabel;

  public void init(ViewHandler viewhandler, CreatePartyViewModel viewmodel, Region root)
  {
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;

    nameField.textProperty().bindBidirectional(viewmodel.nameProperty());
    descriptionField.textProperty().bindBidirectional(viewmodel.descriptionProperty());
    locationField.textProperty().bindBidirectional(viewmodel.locationProperty());
    datePicker.valueProperty().bindBidirectional(viewmodel.dateProperty());
    messageLabel.textProperty().bind(viewmodel.errorProperty());

    userLabel.setText(LocalUser.getUser().getUsername());
  }

  @FXML public void onCreate()
  {
    if (viewmodel.createParty()) {
      viewhandler.openView("my parties");
    }
  }

  @FXML public void onBack()      { reset(); viewhandler.openView("my parties"); }
  @FXML public void onMyParties() { reset(); viewhandler.openView("my parties"); }
  @FXML public void onDiscover()  { reset(); viewhandler.openView("discover"); }
  @FXML public void onFriends()   { reset(); viewhandler.openView("friends"); }
  @FXML public void onLogout()    { reset(); viewhandler.openView("login"); }

  public Region getRoot() { return root; }

  public void reset()
  {
    nameField.clear();
    descriptionField.clear();
    locationField.clear();
    datePicker.setValue(null);
    viewmodel.clearError();
  }
}