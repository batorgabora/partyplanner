package shared.model.service;

import shared.model.User;
import java.util.List;

/**
 * Defines operations related to authentication and user account access.
 *  * @author Loke Hansen
 *  * @author Victor Tonu
 *  * @author Marko Stokic
 *  * @author Mike Lorenzen
 *  * @author Bator Gabora
 */
public interface UserService extends ObservableService {
  /**
   * Attempts to authenticate a user with the provided credentials.
   * @param username the username entered by the user
   * @param password the plain text password entered by the user
   * @return the authenticated user, or null if the credentials are not
   * valid
   */
  User login(String username, String password);

  /**
   * Creates a new user account if the provided information is accepted.
   * @param username the requested username
   * @param password the requested password
   * @param confirmPassword the repeated password used for confirmation
   * @param mail the email address for the new account
   * @return the created user, or null if account creation fails
   */
  User createAccount(String username, String password, String confirmPassword, String mail);

  /**
   * Returns all users known to the system.
   * @return a list of all users
   */
  List<User> getAllUsers();
}
