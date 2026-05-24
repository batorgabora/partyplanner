package client.viewModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import shared.model.*;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CreatePartyViewModelTest {

  private PartyModel model;
  private CreatePartyViewModel vm;
  private User fakeUser;

  @BeforeEach void setUp() {
    model = Mockito.mock(PartyModel.class);
    fakeUser = new User("micheal jackson", "hee hee", "test@test.com");
    LocalUser.setUser(fakeUser);
    vm = new CreatePartyViewModel(model);
  }

  // Z — zero:
  @Test void emptyFields_blocked() {
    vm.createParty();

    assertFalse(vm.errorProperty().get().isEmpty());
    verify(model, never()).createParty(any(), any(), any(), any(), any());
  }

  @Test void missingDate_blocked() {
    vm.nameProperty().set("doesn't");
    vm.locationProperty().set("matter");
    vm.dateProperty().set(null);

    vm.createParty();

    assertFalse(vm.errorProperty().get().isEmpty());
    verify(model, never()).createParty(any(), any(), any(), any(), any());
  }

  // O — one:
  @Test void validInputs_correctArgsForwardedToModel() throws InterruptedException {
    LocalDate year = LocalDate.of(2027, 1, 1);
    vm.nameProperty().set("eurovision");
    vm.locationProperty().set("moldova");
    vm.dateProperty().set(year);
    Party realFutureItWillHappen = new Party(UUID.randomUUID().toString(), "eurovision", "", "moldova", year, fakeUser);

    when(model.createParty(any(), any(), any(), any(), any())).thenReturn(realFutureItWillHappen);
    vm.createParty();
    Thread.sleep(200);

    verify(model).createParty(eq("eurovision"), eq(""), eq("moldova"), any(), eq(year));
    assertTrue(vm.errorProperty().get().isEmpty());
  }

  // M — many:
  @Test void calledTwice_modelReachedBothTimes() throws InterruptedException {
    LocalDate today = LocalDate.now();
    vm.nameProperty().set("Party One");
    vm.locationProperty().set("Oslo");
    vm.dateProperty().set(today);
    Party fakeParty = new Party(UUID.randomUUID().toString(), "Party One", "", "Oslo", today, fakeUser);
    when(model.createParty(any(), any(), any(), any(), any())).thenReturn(fakeParty);

    vm.createParty();
    vm.createParty();
    Thread.sleep(200);

    verify(model, times(2)).createParty(eq("Party One"), eq(""), eq("Oslo"), eq(fakeUser.getId()), eq(today));
  }

  // B — boundary:
  @Test void whitespaceOnlyName_blocked() {
    vm.nameProperty().set("   ");
    vm.locationProperty().set("whatever room Loke finds");
    vm.dateProperty().set(LocalDate.now());

    vm.createParty();

    assertFalse(vm.errorProperty().get().isEmpty());
    verify(model, never()).createParty(any(), any(), any(), any(), any());
  }

  @Test void pastDate_blocked() {
    vm.nameProperty().set("Time traveller party");
    vm.locationProperty().set("idk where that was");
    vm.dateProperty().set(LocalDate.of(2000, 1, 1));

    vm.createParty();

    assertFalse(vm.errorProperty().get().isEmpty());
    verify(model, never()).createParty(any(), any(), any(), any(), any());
  }

  // E — exception:
  @Test void clearError_resetsErrorProperty() {
    vm.errorProperty().set("kaboom");

    vm.clear();

    assertTrue(vm.errorProperty().get().isEmpty());
  }

  // I is its own test class

  // S — simple:
  @Test void properties_reflectSetValues() {
    vm.nameProperty().set("a");
    vm.locationProperty().set("aa");
    vm.descriptionProperty().set("aaa");

    assertEquals("a", vm.nameProperty().get());
    assertEquals("aa", vm.locationProperty().get());
    assertEquals("aaa", vm.descriptionProperty().get());
  }
}