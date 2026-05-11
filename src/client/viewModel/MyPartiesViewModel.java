package client.viewModel;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import shared.model.LocalUser;
import shared.model.Party;
import shared.model.PartyModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MyPartiesViewModel implements PropertyChangeListener
{
  private final PartyModel model;
  private final ObjectProperty<Party> selectedParty;
  private final ObservableList<Party> parties;

  public MyPartiesViewModel(PartyModel model, ObjectProperty<Party> selectedParty)
  {
    this.model = model;
    this.selectedParty = selectedParty;
    this.parties = FXCollections.observableArrayList();
    model.addListener("getAll", this);
  }

  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    Platform.runLater(() -> parties.setAll(model.getParties(LocalUser.getUser())));
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
}