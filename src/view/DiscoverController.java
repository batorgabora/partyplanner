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

public class DiscoverController
{

  private Region root;
  private DiscoverViewModel viewmodel;
  private ViewHandler viewhandler;


  public void init(ViewHandler viewhandler, DiscoverViewModel viewmodel, Region root){
    this.root = root;
    this.viewmodel = viewmodel;
    this.viewhandler = viewhandler;

    //bindings to viewmodel
  }

  @FXML public void onParty() {
    viewhandler.openView("party");
  }

  public Region getRoot()
  {
    return root;
  }

  public void reset(){}

}