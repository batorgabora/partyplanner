package client;

import javafx.application.Application;
import javafx.stage.Stage;
import server.model.ModelManager;
import shared.model.Party;
import shared.model.PartyModel;
import client.viewModel.ViewModelFactory;
import client.view.ViewHandler;

public class Main extends Application {
//scrummin

  @Override
  public void start(Stage stage) throws Exception {
    PartyModel model = new ModelManager();

    ViewModelFactory viewModelFactory = new ViewModelFactory(model);
    ViewHandler viewHandler = new ViewHandler(viewModelFactory);
    viewHandler.start(stage);
  }

  public static void main(String[] args) {
    launch(args);
  }
}
