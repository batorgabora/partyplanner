package client.viewModel;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import shared.model.LocalUser;
import shared.model.Party;
import shared.model.service.PartyService;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;

public class CreatePartyViewModel implements PropertyChangeListener {

  private final PartyService model;
  private final StringProperty name        = new SimpleStringProperty("");
  private final StringProperty description = new SimpleStringProperty("");
  private final StringProperty location    = new SimpleStringProperty("");
  private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>(null);
  private final StringProperty error       = new SimpleStringProperty("");
  private final ObjectProperty<Party> createdParty = new SimpleObjectProperty<>(null);

  public CreatePartyViewModel(PartyService model) {
    this.model = model;
    model.addListener("error", this);
  }

  @Override public void propertyChange(PropertyChangeEvent evt) {
    if ("error".equals(evt.getPropertyName())) {
      Platform.runLater(() -> error.set((String) evt.getNewValue()));
    }
  }

  public void createParty() {
    if (name.get() == null || name.get().isBlank()) { error.set("name is required"); return; }
    if (location.get() == null || location.get().isBlank()) { error.set("location is required"); return; }
    if (date.get() == null || date.get().isBefore(LocalDate.now())) { error.set("please pick a valid date"); return; }

    new Thread(() -> {
      Party party = model.createParty(
          name.get(), description.get(),
          location.get(), LocalUser.getUser().getId(), date.get());
      Platform.runLater(() -> {
        if (party != null) createdParty.set(party);
        else error.set("failed to create party");
      });
    }).start();
  }

  public void clear() {
    name.set("");
    description.set("");
    location.set("");
    date.set(null);
    error.set("");
    createdParty.set(null);
  }

  public StringProperty nameProperty()           { return name; }
  public StringProperty descriptionProperty()    { return description; }
  public StringProperty locationProperty()       { return location; }
  public ObjectProperty<LocalDate> dateProperty(){ return date; }
  public StringProperty errorProperty()          { return error; }
  public ObjectProperty<Party> createdPartyProperty() { return createdParty; }
}