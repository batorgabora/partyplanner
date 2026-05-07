package view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import viewModel.DiscoverViewModel;
import model.Party;
import viewModel.MyPartiesViewModel;

public class MyPartiesController
{

  private Region root;
  private MyPartiesViewModel viewmodel;
  private ViewHandler viewhandler;

  private ListView partyList;
  private Button furtherButton;
  private Label selectedLabel;




  public void init(ViewHandler viewhandler, MyPartiesViewModel viewmodel, Region root){
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;

    //bindings to viewmodel
  }

  @FXML public void onMyParties() {
    viewhandler.openView("my parties");
  }

  @FXML public void onDiscover() {
    viewhandler.openView("discover");
  }

  @FXML public void onFriends() {
    viewhandler.openView("friends");
  }

  @FXML public void onFurther() {
    viewhandler.openView("party");
  }


  public Region getRoot()
  {
    return root;
  }

  public void reset(){}

}