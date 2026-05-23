package shared.model.service;

import shared.model.User;
import java.util.List;

public interface UserService extends ObservableService {
  User login(String username, String password);
  User createAccount(String username, String password, String confirmPassword, String mail);
  List<User> getAllUsers();
}