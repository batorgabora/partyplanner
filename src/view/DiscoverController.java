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

  private ListView partyList;
  private Label selectedLabel;
  private Button onFurtherButton;


  public void init(ViewHandler viewhandler, DiscoverViewModel viewmodel, Region root){
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;

    //bindings to viewmodel
  }

  @FXML public void onFurther() {
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

  public Region getRoot()
  {
    return root;
  }

  public void reset(){}

}