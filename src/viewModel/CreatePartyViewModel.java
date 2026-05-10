package viewModel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.PartyModel;

import java.time.LocalDate;

public class CreatePartyViewModel
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
  }

  public boolean createParty()
  {
    if (name.get() == null || name.get().trim().isEmpty())               { error.set("Name is required.");        return false; }
    if (description.get() == null || description.get().trim().isEmpty()) { error.set("Description is required."); return false; }
    if (location.get() == null || location.get().trim().isEmpty())       { error.set("Location is required.");    return false; }
    error.set("");
    return false; // TODO: server call
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
