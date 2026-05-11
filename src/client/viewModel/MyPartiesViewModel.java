package client.viewModel;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
import java.util.Arrays;
import java.util.List;

public class MyPartiesViewModel implements PropertyChangeListener
{
  private final PartyModel model;
  private final ObjectProperty<Party> selectedParty;
  private final ObservableList<Party> parties;
  private final StringProperty error = new SimpleStringProperty("");
  private final Gson gson;

  public MyPartiesViewModel(PartyModel model, ObjectProperty<Party> selectedParty)
  {
    this.model = model;
    this.selectedParty = selectedParty;
    this.gson = new Gson();
    this.parties = FXCollections.observableArrayList();
    model.addListener("getAll", this);
  }

  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    JsonObject json = (JsonObject) evt.getNewValue();
    Party[] updated = gson.fromJson(json.get("data").getAsString(), Party[].class);
    Platform.runLater(() -> parties.setAll(Arrays.asList(updated)));
  }

  public ObservableList<Party> getParties()
  {
    return parties;
  }

  public void updateParties()
  {
    parties.setAll(model.getParties(LocalUser.getUser()));
  }

  public ObjectProperty<Party> selectedPartyProperty()
  {
    return selectedParty;
  }

  public Party getSelectedParty()
  {
    return selectedParty.get();
  }

  public StringProperty errorProperty() {
    return error;
  }
}