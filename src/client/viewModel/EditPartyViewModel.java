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
import shared.model.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Type;
import java.util.List;

public class EditPartyViewModel implements PropertyChangeListener {

  private final PartyModel model;
  private final ObjectProperty<Party> selectedParty;
  private final StringProperty error = new SimpleStringProperty("");
  private final Gson gson = new Gson();

  // observable lists — view binds to these once
  private final ObservableList<Item> items       = FXCollections.observableArrayList();
  private final ObservableList<Participant> members = FXCollections.observableArrayList();
  private final ObservableList<Option> options   = FXCollections.observableArrayList();
  private final ObservableList<User> friends     = FXCollections.observableArrayList();
  private final StringProperty errorProperty = new SimpleStringProperty("");

  public EditPartyViewModel(PartyModel model, ObjectProperty<Party> selectedParty) {
    this.model = model;
    this.selectedParty = selectedParty;
    model.addListener("error", this);
    model.addListener("getItems", this);
    model.addListener("getParticipants", this);
    model.addListener("getOptions", this);
  }

  @Override public void propertyChange(PropertyChangeEvent evt) {
    switch (evt.getPropertyName()) {
      case "error" -> Platform.runLater(() ->
          error.set((String) evt.getNewValue()));
      case "getItems" -> {
        JsonObject json = (JsonObject) evt.getNewValue();
        Type type = new TypeToken<List<Item>>(){}.getType();
        List<Item> result = gson.fromJson(json.get("data"), type);
        Platform.runLater(() -> items.setAll(result));
      }
      case "getParticipants" -> {
        JsonObject json = (JsonObject) evt.getNewValue();
        Type type = new TypeToken<List<Participant>>(){}.getType();
        List<Participant> result = gson.fromJson(json.get("data"), type);
        Platform.runLater(() -> members.setAll(result));
      }
      case "getOptions" -> {
        JsonObject json = (JsonObject) evt.getNewValue();
        Type type = new TypeToken<List<Option>>(){}.getType();
        List<Option> result = gson.fromJson(json.get("data"), type);
        Platform.runLater(() -> options.setAll(result));
      }
    }
  }

  public void loadData() {
    if (selectedParty.get() == null) return;
    var i = model.getItems(selectedParty.get());
    var m = model.getParticipants(selectedParty.get());
    var o = model.getOptions(selectedParty.get());
    Platform.runLater(() -> {
      items.setAll(i);
      members.setAll(m);
      options.setAll(o);
    });
  }

  public void loadFriends() {
    var f = model.getFriends(LocalUser.getUser());
    Platform.runLater(() -> friends.setAll(f));
  }

  // observable properties for View to bind to
  public ObservableList<Item> itemsProperty()        { return items; }
  public ObservableList<Participant> membersProperty(){ return members; }
  public ObservableList<Option> optionsProperty()    { return options; }
  public ObservableList<User> friendsProperty()      { return friends; }
  public StringProperty errorProperty()              { return error; }
  public void setError(String message)               { error.set(message); }

  public Party getSelectedParty() { return selectedParty.get(); }

  public String getRoleForCurrentUser(String partyId) {
    if (selectedParty.get() == null) return null;
    return model.getRole(LocalUser.getUser(), selectedParty.get());
  }

  public void updateName(String name) {
    if (selectedParty.get() == null || name.equals(selectedParty.get().getName())) return;
    selectedParty.get().setName(name);
    model.updateParty(selectedParty.get(), name,
        selectedParty.get().getDescription(), selectedParty.get().getLocation());
  }

  public void updateDescription(String description) {
    if (selectedParty.get() == null || description.equals(selectedParty.get().getDescription())) return;
    selectedParty.get().setDescription(description);
    model.updateParty(selectedParty.get(), selectedParty.get().getName(),
        description, selectedParty.get().getLocation());
  }

  public void updateLocation(String location) {
    if (selectedParty.get() == null || location.equals(selectedParty.get().getLocation())) return;
    selectedParty.get().setLocation(location);
    model.updateParty(selectedParty.get(), selectedParty.get().getName(),
        selectedParty.get().getDescription(), location);
  }

  public void addItem(String name) {
    List<Item> updated = model.addItem(selectedParty.get(), name);
    Platform.runLater(() -> items.setAll(updated));
  }

  public void removeItem(Item item) {
    List<Item> updated = model.removeItem(item);
    Platform.runLater(() -> items.setAll(updated));
  }

  public void addOption(String proposal) {
    List<Option> updated = model.addOption(selectedParty.get(), proposal);
    Platform.runLater(() -> options.setAll(updated));
  }

  public void removeOption(Option option) {
    List<Option> updated = model.removeOption(option);
    Platform.runLater(() -> options.setAll(updated));
  }

  public void addParticipant(User user) {
    if (user == null || selectedParty.get() == null) return;
    List<Participant> updated = model.addParticipant(selectedParty.get(), new Participant(selectedParty.get(), user));
    Platform.runLater(() -> members.setAll(updated));
  }

  public void removeParticipant(Participant participant) {
    if (participant == null || selectedParty.get() == null) return;
    List<Participant> updated = model.removeParticipant(selectedParty.get(), participant);
    Platform.runLater(() -> members.setAll(updated));
  }

  public boolean isAlreadyParticipant(User user) {
    if (user == null || selectedParty.get() == null) return false;
    for (Participant p : members) {
      if (p.getUser().getId().equals(user.getId())) return true;
    }
    return false;
  }

  public void deleteParty() {
    if (selectedParty.get() == null) return;
    model.deleteParty(selectedParty.get());
    selectedParty.set(null);
  }
}