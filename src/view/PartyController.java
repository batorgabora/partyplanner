package view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import viewModel.PartyViewModel;

public class PartyController
{

  private Region root;
  private PartyViewModel viewmodel;
  private ViewHandler viewhandler;

  private Label roleLabel;
  private Label nameLabel;
  private Label descriptionLabel;
  private Label dateLabel;
  private Label locationLabel;
  private ListView itemList;
  private ListView timeList;
  private ListView memberList;
  private Button editButton;
  private Button leaveButton;



  public void init(ViewHandler viewhandler, PartyViewModel viewmodel, Region root){
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;

    //bindings to viewmodel
  }

  @FXML public void onDiscover() {
    viewhandler.openView("discover");
  }
  @FXML public void onFriends() {
    viewhandler.openView("friends");
  }
  @FXML public void onMyParties() {
    viewhandler.openView("my parties");
  }


  public Region getRoot()
  {
    return root;
  }

  public void reset(){}

}