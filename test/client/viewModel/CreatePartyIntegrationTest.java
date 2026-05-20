package client.viewModel;

import client.view.CreatePartyController;
import client.view.ViewHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import shared.model.*;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreatePartyIntegrationTest
{
  private CreatePartyController controller;
  private CreatePartyViewModel viewModel;
  private ViewHandler mockViewHandler;
  private PartyModel mockModel;
  private User testUser;

  @BeforeEach public void setUp() throws Exception
  {
    testUser = new User("o", "ye", "pass", "test@test.com");
    LocalUser.setUser(testUser);

    mockModel = Mockito.mock(PartyModel.class);
    mockViewHandler = Mockito.mock(ViewHandler.class);
    viewModel = new CreatePartyViewModel(mockModel);
    controller = new CreatePartyController();

    Field vmField = CreatePartyController.class.getDeclaredField("viewmodel");
    vmField.setAccessible(true);
    vmField.set(controller, viewModel);

    Field vhField = CreatePartyController.class.getDeclaredField("viewhandler");
    vhField.setAccessible(true);
    vhField.set(controller, mockViewHandler);
  }

  @Test public void validInput_navigatesToMyParties()
  {
    viewModel.nameProperty().set("bs");
    viewModel.locationProperty().set("bs place");
    viewModel.dateProperty().set(LocalDate.now());
    when(mockModel.createParty(any(), any(), any(), any(), any()))
        .thenReturn(new Party("bs", "", "bs place", testUser));

    controller.onCreate();

    verify(mockViewHandler).openView("my parties");
  }

  @Test public void invalidInput_staysOnForm()
  {
    controller.onCreate();

    verify(mockViewHandler, never()).openView(any());
    verify(mockModel, never()).createParty(any(), any(), any(), any(), any());
  }

  @Test public void serverError_staysOnForm()
  {
    viewModel.nameProperty().set("i am tired mastah");
    viewModel.locationProperty().set("cotton fields");
    viewModel.dateProperty().set(LocalDate.of(2027, 6, 15));
    when(mockModel.createParty(any(), any(), any(), any(), any())).thenReturn(null);

    controller.onCreate();

    verify(mockViewHandler, never()).openView(any());
    verify(mockModel).createParty(any(), any(), any(), any(), any());
  }
}