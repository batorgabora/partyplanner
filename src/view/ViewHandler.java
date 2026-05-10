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
  private MyPartiesController mypartiescontroller;
  private RegisterController registercontroller;
  private FriendsController friendscontroller;
  private CreatePartyController createpartycontroller;
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
    currentScene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());
    openView("login"); // use id strings instead of fxml filenames
  }

  public void openView(String id)
  {
    try {
      Region root = switch (id)
      {
        case "discover" -> loadDiscoverView();
        case "login" -> loadLoginView();
        case "party" -> loadPartyView();
        case "my parties" -> loadMyPartiesView();
        case "register" -> loadRegisterView();
        case "friends" -> loadFriendsView();
        case "create party" -> loadCreatePartyView();
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
      var url = getClass().getResource("/view/DiscoverView2.fxml");
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
      var url = getClass().getResource("/view/LoginView2.fxml");
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
      var url = getClass().getResource("/view/PartyView2.fxml");
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

  private Region loadCreatePartyView() throws IOException
  {
    if (createpartycontroller == null)
    {
      var url = getClass().getResource("/view/CreatePartyView.fxml");
      FXMLLoader loader = new FXMLLoader(url);
      Region root = loader.load();

      createpartycontroller = loader.getController();
      createpartycontroller.init(this, viewmodelfactory.getCreatePartyViewModel(), root);
    }
    else
    {
      createpartycontroller.reset();
    }

    return createpartycontroller.getRoot();
  }

  private Region loadMyPartiesView() throws IOException
  {
    if (mypartiescontroller == null)
    {
      // first time opening --> load the FXML and wire everything up
      var url = getClass().getResource("/view/MyPartiesView.fxml");
      FXMLLoader loader = new FXMLLoader(url);
      Region root = loader.load();

      mypartiescontroller = loader.getController();
      mypartiescontroller.init(this, viewmodelfactory.getMyPartiesViewModel(), root);
    }
    else
    {
      // already loaded before --> just clear the fields
      mypartiescontroller.reset();
    }

    return mypartiescontroller.getRoot();
  }

  private Region loadRegisterView() throws IOException
  {
    if (registercontroller == null)
    {
      // first time opening --> load the FXML and wire everything up
      var url = getClass().getResource("/view/RegisterView.fxml");
      System.out.println("FXML URL: " + url); // add this
      FXMLLoader loader = new FXMLLoader(url);
      Region root = loader.load();

      registercontroller = loader.getController();
      registercontroller.init(this, viewmodelfactory.getRegisterViewModel(), root);
    }
    else
    {
      // already loaded before --> just clear the fields
      registercontroller.reset();
    }

    return registercontroller.getRoot();
  }

  private Region loadFriendsView() throws IOException
  {
    if (friendscontroller == null)
    {
      // first time opening --> load the FXML and wire everything up
      var url = getClass().getResource("/view/FriendsView.fxml");
      System.out.println("FXML URL: " + url); // add this
      FXMLLoader loader = new FXMLLoader(url);
      Region root = loader.load();

      friendscontroller = loader.getController();
      friendscontroller.init(this, viewmodelfactory.getFriendsViewModel(), root);
    }
    else
    {
      // already loaded before --> just clear the fields
      friendscontroller.reset();
    }

    return friendscontroller.getRoot();
  }

}