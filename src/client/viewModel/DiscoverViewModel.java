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
import shared.model.PartyModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Type;
import java.util.List;

public class DiscoverViewModel implements PropertyChangeListener {

  private final PartyModel model;
  private final ObjectProperty<Party> selectedParty;
  private final ObservableList<Party> parties = FXCollections.observableArrayList();
  private final StringProperty errorProperty = new SimpleStringProperty("");
  private final Gson gson = new Gson();

  public DiscoverViewModel(PartyModel model, ObjectProperty<Party> selectedParty) {
    this.model = model;
    this.selectedParty = selectedParty;
    model.addListener("getInvitedParties", this); // listen for server push
    model.addListener("error", this);
  }

  @Override public void propertyChange(PropertyChangeEvent evt) {
    switch (evt.getPropertyName()) {
      case "error" -> Platform.runLater(() ->
          errorProperty.set((String) evt.getNewValue()));
      case "getInvitedParties" -> {
        // parse data directly from event — no extra network call
        JsonObject json = (JsonObject) evt.getNewValue();
        Type type = new TypeToken<List<Party>>(){}.getType();
        List<Party> result = gson.fromJson(json.get("data"), type);
        Platform.runLater(() -> parties.setAll(result));
      }
    }
  }

  // called once on load — after this, updates come via propertyChange
  public void updateParties() {
    var result = model.getInvitedParties(LocalUser.getUser());
    Platform.runLater(() -> parties.setAll(result));
  }

  public ObservableList<Party> partiesProperty()    { return parties; }
  public ObservableList<Party> getInvitedParties()  { return parties; }
  public ObjectProperty<Party> selectedPartyProperty() { return selectedParty; }
  public Party getSelectedParty()                   { return selectedParty.get(); }
  public StringProperty errorProperty()             { return errorProperty; }
}