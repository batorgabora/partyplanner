package client.viewModel;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import shared.model.LocalUser;
import shared.model.User;
import shared.model.service.FriendService;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FriendsViewModel implements PropertyChangeListener {

  private final FriendService model;
  private final StringProperty error       = new SimpleStringProperty("");
  private final ObservableList<User> friends    = FXCollections.observableArrayList();
  private final ObservableList<User> nonFriends = FXCollections.observableArrayList();

  public FriendsViewModel(FriendService model) {
    this.model = model;
    model.addListener("error", this);
  }

  @Override public void propertyChange(PropertyChangeEvent evt) {
    if ("error".equals(evt.getPropertyName())) {
      Platform.runLater(() -> error.set((String) evt.getNewValue()));
    }
  }

  // called once on load
  public void loadData() {
    var f  = model.getFriends(LocalUser.getUser());
    var nf = model.getNonFriends(LocalUser.getUser());
    Platform.runLater(() -> {
      friends.setAll(f);
      nonFriends.setAll(nf);
    });
  }

  public void addFriend(User friend) {
    model.addFriend(LocalUser.getUser(), friend);
  }

  public void removeFriend(User friend) {
    model.removeFriend(LocalUser.getUser(), friend);
  }

  public ObservableList<User> friendsProperty()    { return friends; }
  public ObservableList<User> nonFriendsProperty() { return nonFriends; }
  public StringProperty errorProperty()            { return error; }
}