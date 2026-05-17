package client.viewModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import shared.model.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CreatePartyViewModelTest {

  private PartyModel model;
  private CreatePartyViewModel vm;
  private User fakeUser;

  @BeforeEach void setUp() {
    model = Mockito.mock(PartyModel.class);
    fakeUser = new User("testuser", "password", "test@test.com");
    LocalUser.setUser(fakeUser);
    vm = new CreatePartyViewModel(model);
  }

  // Z — zero:
  @Test void createParty_emptyFields_returnsFalse() {
    when(model.createParty(any(), any(), any(), any(), any())).thenReturn(null);

    assertFalse(vm.createParty());
  }

  @Test void createParty_nullDate_returnsFalse() {
    vm.nameProperty().set("Birthday");
    vm.locationProperty().set("Copenhagen");
    vm.dateProperty().set(null);
    when(model.createParty(any(), any(), any(), any(), any())).thenReturn(null);

    assertFalse(vm.createParty());
  }

  // O one:
  @Test void createParty_validInputs_returnsTrue() {
    vm.nameProperty().set("Birthday Party");
    vm.locationProperty().set("Copenhagen");
    vm.dateProperty().set(LocalDate.now());
    Party fakeParty = new Party("1", "Birthday Party", "", "Copenhagen", fakeUser);
    when(model.createParty(any(), any(), any(), any(), any())).thenReturn(fakeParty);

    assertTrue(vm.createParty());
  }

  // M — many:
  @Test void createParty_calledTwice_bothSucceed() {
    vm.nameProperty().set("Party One");
    vm.locationProperty().set("Oslo");
    vm.dateProperty().set(LocalDate.now());
    Party fakeParty = new Party("1", "Party One", "", "Oslo", fakeUser);
    when(model.createParty(any(), any(), any(), any(), any())).thenReturn(fakeParty);

    assertTrue(vm.createParty());
    assertTrue(vm.createParty());
  }

  // B — boundary:
  @Test void createParty_whitespaceOnlyName_returnsFalse() {
    vm.nameProperty().set("   ");
    vm.locationProperty().set("Copenhagen");
    vm.dateProperty().set(LocalDate.now());
    when(model.createParty(any(), any(), any(), any(), any())).thenReturn(null);

    assertFalse(vm.createParty());
  }

  @Test void createParty_pastDate_returnsFalse() {
    vm.nameProperty().set("Old Party");
    vm.locationProperty().set("Copenhagen");
    vm.dateProperty().set(LocalDate.of(2000, 1, 1));
    when(model.createParty(any(), any(), any(), any(), any())).thenReturn(null);

    assertFalse(vm.createParty());
  }

  // I — interface:
  @Test void createParty_modelReturnsNull_setsErrorProperty() {
    vm.nameProperty().set("Test Party");
    when(model.createParty(any(), any(), any(), any(), any())).thenReturn(null);

    vm.createParty();

    assertFalse(vm.errorProperty().get().isEmpty());
  }

  @Test void createParty_success_errorPropertyEmpty() {
    vm.nameProperty().set("Test Party");
    vm.locationProperty().set("Copenhagen");
    vm.dateProperty().set(LocalDate.now());
    Party fakeParty = new Party("1", "Test Party", "", "Copenhagen", fakeUser);
    when(model.createParty(any(), any(), any(), any(), any())).thenReturn(fakeParty);

    vm.createParty();

    assertTrue(vm.errorProperty().get().isEmpty());
  }

  // E — exception:
  @Test void clearError_resetsErrorProperty() {
    vm.errorProperty().set("something went wrong");

    vm.clearError();

    assertTrue(vm.errorProperty().get().isEmpty());
  }

  // S — simple:
  @Test void properties_reflectSetValues() {
    vm.nameProperty().set("Garden Party");
    vm.locationProperty().set("Aarhus");
    vm.descriptionProperty().set("Fun outdoor event");

    assertEquals("Garden Party", vm.nameProperty().get());
    assertEquals("Aarhus", vm.locationProperty().get());
    assertEquals("Fun outdoor event", vm.descriptionProperty().get());
  }
}