package client.viewModel;

import shared.model.PartyModel;
import shared.model.User;

public class RegisterViewModel
{
  private PartyModel model;

  public RegisterViewModel(PartyModel model){
    this.model = model;
  }

  public boolean createAccount(String username, String password, String confirmPassword, String mail)
  {
    User user = model.createAccount(username, password, confirmPassword, mail);
    return user != null;
  }
}
