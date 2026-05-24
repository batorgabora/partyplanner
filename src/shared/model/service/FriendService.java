package shared.model.service;

import shared.model.User;
import java.util.List;

/**
 * Defines operations for retrieving and managing user friendships.
 *  * @author Loke Hansen
 *  * @author Victor Tonu
 *  * @author Marko Stokic
 *  * @author Mike Lorenzen
 *  * @author Bator Gabora
 */
public interface FriendService extends ObservableService {
  /**
   * Returns the current friends of the given user.
   * @param user the user whose friends should be retrieved
   * @return a list of the user's friends
   */
  List<User> getFriends(User user);

  /**
   * Returns users who are not currently friends with the given user.
   * @param user the user whose non-friends should be retrieved
   * @return a list of users who can still be added as friends
   */
  List<User> getNonFriends(User user);

  /**
   * Creates a friendship between the given user and another user.
   * @param user the user initiating the friendship
   * @param friend the user to add as a friend
   */
  void addFriend(User user, User friend);

  /**
   * Removes an existing friendship between the given user and another user.
   * @param user the user removing the friend
   * @param friend the user to remove from the friend list
   */
  void removeFriend(User user, User friend);
}
