package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import viewModel.ViewModelFactory;

import java.io.IOException;

public class ViewHandler
{
  private Stage primaryStage;
  private Scene currentScene;
  private DiscoverController discovercontroller;
  private LoginController logincontroller;
  private PartyController partycontroller;
  private ViewModelFactory viewmodelfactory;

  public ViewHandler(ViewModelFactory viewmodelfactory)
  {
    this.viewmodelfactory = viewmodelfactory;
    currentScene = new Scene(new Region());
  }

  public void start(Stage stage) throws IOException
  {
    this.primaryStage = stage;
    primaryStage.setResizable(false);
    //currentScene.getStylesheets().add(getClass().getResource("/view/styles.css").toExternalForm());
    openView("discover"); // use id strings instead of fxml filenames
  }

  public void openView(String id)
  {
    try {
      Region root = switch (id)
      {
        case "discover" -> loadDiscoverView();
        case "login" -> loadLoginView();
        case "party" -> loadPartyView();
        default -> throw new IllegalArgumentException("Unknown view: " + id);
      };

      currentScene.setRoot(root);
      primaryStage.setTitle("pp.");
      primaryStage.setScene(currentScene);
      primaryStage.show();
    }
    catch (IOException e) {e.printStackTrace();}
  }

  // each loadview() separated into its own method with no parameters.
  // only loads FXML once - reuses controller after that.
  private Region loadDiscoverView() throws IOException
  {
    if (discovercontroller == null)
    {
      var url = getClass().getResource("/view/DiscoverView.fxml");
      System.out.println("FXML URL: " + url);
      FXMLLoader loader = new FXMLLoader(url);
      Region root = loader.load();

      discovercontroller = loader.getController();
      discovercontroller.init(this, viewmodelfactory.getDiscoverViewModel(), root);
    }
    else
    {
      // already loaded before --> just clear the fields
      discovercontroller.reset();
    }

    return discovercontroller.getRoot();
  }

  private Region loadLoginView() throws IOException
  {
    if (logincontroller == null)
    {
      // first time opening --> load the FXML and wire everything up
      var url = getClass().getResource("/view/LoginView.fxml");
      System.out.println("FXML URL: " + url); // add this
      FXMLLoader loader = new FXMLLoader(url);
      Region root = loader.load();

      logincontroller = loader.getController();
      logincontroller.init(this, viewmodelfactory.getLoginViewModel(), root);
    }
    else
    {
      // already loaded before --> just clear the fields
      logincontroller.reset();
    }

    return logincontroller.getRoot();
  }

  private Region loadPartyView() throws IOException
  {
    if (partycontroller == null)
    {
      // first time opening --> load the FXML and wire everything up
      var url = getClass().getResource("/view/PartyView.fxml");
      System.out.println("FXML URL: " + url); // add this
      FXMLLoader loader = new FXMLLoader(url);
      Region root = loader.load();

      partycontroller = loader.getController();
      partycontroller.init(this, viewmodelfactory.getPartyViewModel(), root);
    }
    else
    {
      // already loaded before --> just clear the fields
      partycontroller.reset();
    }

    return partycontroller.getRoot();
  }
}