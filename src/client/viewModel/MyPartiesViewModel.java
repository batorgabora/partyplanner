package client.viewModel;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import shared.model.LocalUser;
import javafx.collections.ObservableList;
import shared.model.Party;
import shared.model.PartyModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

public class MyPartiesViewModel implements PropertyChangeListener
{
  private PartyModel model;
  private ObjectProperty<Party> selectedParty;
  private final StringProperty error;
  private ObservableList<Party> parties;
  private final Gson gson;

  public MyPartiesViewModel(PartyModel model, ObjectProperty<Party> selectedParty){
    //for wiring them together --> common selected
    this.model = model;
    this.gson = new Gson();
    this.selectedParty = selectedParty;
    error = new SimpleStringProperty("");
    model.addListener("getMy", this);
    parties = FXCollections.observableArrayList();  // initialize empty
    // Register as listener for all 3 event types.
    // From this point on, whenever the model fires these events,
    // our propertyChange() method below is called automatically.
  }

  public ObjectProperty<Party> selectedPartyProperty() {
    return selectedParty;
  }
  public Party getSelectedParty() {
    return selectedParty.get();
  }

  public ObservableList<Party> updateParties() {
    var result = model.getMyParties(LocalUser.getUser());
    return FXCollections.observableArrayList(result != null ? result : new ArrayList<>());
  }

  public StringProperty errorProperty() { return error; }

  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    //JsonObject json = (JsonObject) evt.getNewValue();
    //Party[] updated = gson.fromJson(json.get("data").getAsString(), Party[].class);
    //Platform.runLater(() -> parties.setAll(Arrays.asList(updated)));

    Platform.runLater(() -> {
      parties.setAll(model.getMyParties(LocalUser.getUser()));
    });
  }

  public String getRoleForParty(Party party) {
    return model.getRole(LocalUser.getUser(), party);
  }
}
