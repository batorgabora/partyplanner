package client.viewModel;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import shared.model.LocalUser;
import shared.model.Party;
import shared.model.PartyModel;
import shared.model.User;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FriendsViewModel implements PropertyChangeListener
{
  private PartyModel model;

  private final StringProperty errorProperty;

  private ObservableList<User> friends;


  public FriendsViewModel(PartyModel model) {
    this.model = model;
    errorProperty = new SimpleStringProperty("");
    model.addListener("something", this);
    friends = FXCollections.observableArrayList(); // empty for now
  }

  public void loadFriends() {
    friends.setAll(LocalUser.getUser().getFriendList());
  }

  public ObservableList<User> getFriends() {
    return friends;
  }

  @Override public void propertyChange(PropertyChangeEvent evt)
  {

  }

}
