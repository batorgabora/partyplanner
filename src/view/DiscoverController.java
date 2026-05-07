package view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import viewModel.DiscoverViewModel;
import model.Party;

public class DiscoverController
{

  private Region root;
  private DiscoverViewModel viewmodel;
  private ViewHandler viewhandler;

  private ListView<Party> partyList;
  private Label selectedLabel;
  private Button onFurtherButton;


  public void init(ViewHandler viewhandler, DiscoverViewModel viewmodel, Region root){
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;

    //bindings to viewmodel
    partyList.setItems(viewmodel.getParties());
    //what is selected can later be used --> we just forward it to the other view
    partyList.getSelectionModel().selectedItemProperty().addListener(
        (obs, oldVal, newVal) -> viewmodel.selectedPartyProperty().set(newVal)
    );

    //make selected one show up:
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
      selectedLabel.setText("please select a vinyl first");
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

  public void reset(){}

}