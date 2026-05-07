package viewModel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import viewModel.DiscoverViewModel;
import viewModel.LoginViewModel;
import viewModel.PartyViewModel;
import model.PartyModel;
import model.Party;

public class ViewModelFactory {
  private DiscoverViewModel discoverviewmodel;
  private LoginViewModel loginviewmodel;
  private PartyViewModel partyviewmodel;
  private MyPartiesViewModel mypartiesviewmodel;
  private ObjectProperty<Party> selectedParty;

  public ViewModelFactory(PartyModel model) {
    selectedParty = new SimpleObjectProperty<>();
    discoverviewmodel = new DiscoverViewModel(model, selectedParty);
    loginviewmodel = new LoginViewModel(model);
    partyviewmodel = new PartyViewModel(model, selectedParty);
    mypartiesviewmodel = new MyPartiesViewModel(model);
  }

  public DiscoverViewModel getDiscoverViewModel() { return discoverviewmodel; }
  public LoginViewModel getLoginViewModel() { return loginviewmodel; }
  public PartyViewModel getPartyViewModel() { return partyviewmodel; }
  public MyPartiesViewModel getMyPartiesViewModel() {return mypartiesviewmodel;}
}