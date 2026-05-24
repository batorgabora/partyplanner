package shared.model.service;

import shared.model.User;
import java.util.List;

public interface FriendService extends ObservableService {
  List<User> getFriends(User user);
  List<User> getNonFriends(User user);
  void addFriend(User user, User friend);
  void removeFriend(User user, User friend);
}