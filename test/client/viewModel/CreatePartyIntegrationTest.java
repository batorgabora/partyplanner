package client.viewModel;

import client.view.CreatePartyController;
import client.view.ViewHandler;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import shared.model.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
    try { Platform.startup(() -> {}); } catch (IllegalStateException ignored) {}

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
    
    
    viewModel.createdPartyProperty().addListener((obs, o, n) -> {
      if (n != null) mockViewHandler.openView("my parties");
    });
  }

  @Test public void validInput_navigatesToMyParties() throws InterruptedException
  {
    viewModel.nameProperty().set("bs");
    viewModel.locationProperty().set("bs place");
    viewModel.dateProperty().set(LocalDate.now());
    when(mockModel.createParty(any(), any(), any(), any(), any()))
        .thenReturn(new Party(UUID.randomUUID().toString(), "bs", "", "bs place", LocalDate.now(), testUser));

    controller.onCreate();
    Thread.sleep(200);
    CountDownLatch latch = new CountDownLatch(1);
    Platform.runLater(latch::countDown);
    latch.await(5, TimeUnit.SECONDS);

    verify(mockViewHandler).openView("my parties");
  }

  @Test public void invalidInput_staysOnForm()
  {
    controller.onCreate();

    verify(mockViewHandler, never()).openView(any());
    verify(mockModel, never()).createParty(any(), any(), any(), any(), any());
  }

  @Test public void serverError_staysOnForm() throws InterruptedException
  {
    viewModel.nameProperty().set("i am tired mastah");
    viewModel.locationProperty().set("cotton fields");
    viewModel.dateProperty().set(LocalDate.of(2027, 6, 15));
    when(mockModel.createParty(any(), any(), any(), any(), any())).thenReturn(null);

    controller.onCreate();
    Thread.sleep(200);

    verify(mockViewHandler, never()).openView(any());
    verify(mockModel).createParty(any(), any(), any(), any(), any());
  }
}