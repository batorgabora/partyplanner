package client.viewModel;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import shared.model.PartyModel;
import shared.model.Party;
import shared.model.LocalUser;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;

public class CreatePartyViewModel implements PropertyChangeListener
{
  private final PartyModel model;
  private final StringProperty name = new SimpleStringProperty("");
  private final StringProperty description = new SimpleStringProperty("");
  private final StringProperty location = new SimpleStringProperty("");
  private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>(null);
  private final StringProperty error = new SimpleStringProperty("");

  public CreatePartyViewModel(PartyModel model)
  {
    this.model = model;
    model.addListener("error", this);
  }

  @Override public void propertyChange(PropertyChangeEvent evt) {
    Platform.runLater(() -> error.set((String) evt.getNewValue()));
  }

  public boolean createParty()
  {
    System.out.println("Create party function called");
    //added validation here since existing validation is server side, cannot unit test it
    if (name.get() == null || name.get().isBlank()) return false;
    if (location.get() == null || location.get().isBlank()) return false;
    if (date.get() == null || date.get().isBefore(LocalDate.now())) return false;
    Party party = model.createParty(
        name.get(),
        description.get(),
        location.get(),
        LocalUser.getUser().getId(),
        date.get()
    );
    System.out.println("Model create party called with arguments from view model");
    if (party != null)
    {
      System.out.println("Party created successfully");
      return true;
    }
    System.out.println("Party creation failed");
    error.set("Failed to create party.");
    return false;
  }

  public void clearError()
  {
    error.set("");
  }

  public StringProperty nameProperty() { return name; }
  public StringProperty descriptionProperty() { return description; }
  public StringProperty locationProperty() { return location; }
  public ObjectProperty<LocalDate> dateProperty() { return date; }
  public StringProperty errorProperty() { return error; }
}
