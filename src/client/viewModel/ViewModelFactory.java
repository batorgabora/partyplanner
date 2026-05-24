package client.viewModel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import shared.model.PartyModel;
import shared.model.Party;

import java.util.HashMap;
import java.util.Map;

public class ViewModelFactory {

  private final Map<Class<?>, Object> registry = new HashMap<>();
  protected final ObjectProperty<Party> selectedParty = new SimpleObjectProperty<>();

  public ViewModelFactory(PartyModel model) {
    register(LoginViewModel.class,       new LoginViewModel(model));
    register(RegisterViewModel.class,    new RegisterViewModel(model));
    register(DiscoverViewModel.class,    new DiscoverViewModel(model, selectedParty));
    register(CreatePartyViewModel.class, new CreatePartyViewModel(model));
    register(MyPartiesViewModel.class,   new MyPartiesViewModel(model, selectedParty));
    register(FriendsViewModel.class,     new FriendsViewModel(model));
    register(PartyViewModel.class,       new PartyViewModel(model, selectedParty));
    register(EditPartyViewModel.class,   new EditPartyViewModel(model, selectedParty));
  }

  protected final <T> void register(Class<T> type, T instance) {
    registry.put(type, instance);
  }

  @SuppressWarnings("unchecked")
  public <T> T get(Class<T> type) {
    T vm = (T) registry.get(type);
    if (vm == null) throw new IllegalArgumentException("No ViewModel registered: " + type.getSimpleName());
    return vm;
  }

  public LoginViewModel       getLoginViewModel()        { return get(LoginViewModel.class); }
  public RegisterViewModel    getRegisterViewModel()     { return get(RegisterViewModel.class); }
  public DiscoverViewModel    getDiscoverViewModel()     { return get(DiscoverViewModel.class); }
  public CreatePartyViewModel getCreatePartyViewModel()  { return get(CreatePartyViewModel.class); }
  public MyPartiesViewModel   getMyPartiesViewModel()    { return get(MyPartiesViewModel.class); }
  public FriendsViewModel     getFriendsViewModel()      { return get(FriendsViewModel.class); }
  public PartyViewModel       getPartyViewModel()        { return get(PartyViewModel.class); }
  public EditPartyViewModel   getEditPartyViewModel()    { return get(EditPartyViewModel.class); }
}