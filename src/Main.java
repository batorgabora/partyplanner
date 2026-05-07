import javafx.application.Application;
import javafx.stage.Stage;
import model.ModelManager;
import model.Party;
import model.PartyModel;
import viewModel.ViewModelFactory;
import view.ViewHandler;

public class Main extends Application {


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