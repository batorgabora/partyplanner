package client.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import shared.model.LocalUser;
import shared.model.PartyModel;
import shared.model.User;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FriendsViewModel implements PropertyChangeListener
{
  private final PartyModel model;
  private final StringProperty error = new SimpleStringProperty("");
  private final ObservableList<User> friends = FXCollections.observableArrayList();
  private final ObservableList<User> nonFriends = FXCollections.observableArrayList();

  public FriendsViewModel(PartyModel model) {
    this.model = model;
  }

  public ObservableList<User> getFriends() {
    friends.setAll(model.getFriends(LocalUser.getUser()));
    return friends;
  }

  public ObservableList<User> getNonFriends() {
    nonFriends.setAll(model.getNonFriends(LocalUser.getUser()));
    return nonFriends;
  }

  public void addFriend(User friend) {
    model.addFriend(LocalUser.getUser(), friend);
  }

  public void removeFriend(User friend) {
    model.removeFriend(LocalUser.getUser(), friend);
  }

  public StringProperty errorProperty() { return error; }

  @Override public void propertyChange(PropertyChangeEvent evt) {}
}