package shared.model;

public class LocalUser {
  private static User currentUser;

  public static void setUser(User user) {
    currentUser = user;
  }

  public static User getUser() {
    return currentUser;
  }
}
