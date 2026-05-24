package client.viewModel;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import shared.model.LocalUser;
import shared.model.Party;
import shared.model.service.PartyService;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Type;
import java.util.List;

public class MyPartiesViewModel implements PropertyChangeListener {

  private final PartyService model;
  private final ObjectProperty<Party> selectedParty;
  private final StringProperty error  = new SimpleStringProperty("");
  private final ObservableList<Party> parties = FXCollections.observableArrayList();
  private final Gson gson = new Gson();

  public MyPartiesViewModel(PartyService model, ObjectProperty<Party> selectedParty) {
    this.model = model;
    this.selectedParty = selectedParty;
    model.addListener("getMyParties", this);
    model.addListener("error", this);
  }

  @Override public void propertyChange(PropertyChangeEvent evt) {
    switch (evt.getPropertyName()) {
      case "error" -> Platform.runLater(() ->
          error.set((String) evt.getNewValue()));
      case "getMyParties" -> {
        JsonObject json = (JsonObject) evt.getNewValue();
        Type type = new TypeToken<List<Party>>(){}.getType();
        List<Party> result = gson.fromJson(json.get("data"), type);
        Platform.runLater(() -> parties.setAll(result));
      }
    }
  }

  public void updateParties() {
    var result = model.getMyParties(LocalUser.getUser());
    Platform.runLater(() -> parties.setAll(result != null ? result : List.of()));
  }

  public ObservableList<Party> partiesProperty()       { return parties; }
  public ObjectProperty<Party> selectedPartyProperty() { return selectedParty; }
  public Party getSelectedParty()                      { return selectedParty.get(); }
  public StringProperty errorProperty()                { return error; }
}