package client.viewModel;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import shared.model.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import shared.model.service.PartyViewService;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PartyViewModel implements PropertyChangeListener {

  private final PartyViewService model;
  private final ObjectProperty<Party> selectedParty;
  private final StringProperty errorProperty = new SimpleStringProperty("");
  private final StringProperty messageInput  = new SimpleStringProperty("");
  private final Gson gson = new Gson();

  // observable lists — view binds once
  private final ObservableList<Item> items        = FXCollections.observableArrayList();
  private final ObservableList<Participant> members = FXCollections.observableArrayList();
  private final ObservableList<Option> options    = FXCollections.observableArrayList();

  //chat
  private final ObservableList<Message> messages = FXCollections.observableArrayList();
  public ObservableList<Message> messagesProperty() { return messages; }

  public PartyViewModel(PartyViewService model, ObjectProperty<Party> selectedParty) {
    this.model = model;
    this.selectedParty = selectedParty;
    model.addListener("error", this);
    model.addListener("getItems", this);
    model.addListener("getOptions", this);
    model.addListener("getParticipants", this);
    model.addListener("sendMessage", this); // for real-time chat
  }

  @Override public void propertyChange(PropertyChangeEvent evt) {
    switch (evt.getPropertyName()) {
      case "error" -> Platform.runLater(() ->
          errorProperty.set((String) evt.getNewValue()));
      case "getItems" -> {
        JsonObject json = (JsonObject) evt.getNewValue();
        Type type = new TypeToken<List<Item>>(){}.getType();
        List<Item> result = gson.fromJson(json.get("data"), type);
        Platform.runLater(() -> items.setAll(result));
      }
      case "getParticipants" -> {
        JsonObject json = (JsonObject) evt.getNewValue();
        Type type = new TypeToken<List<Participant>>(){}.getType();
        List<Participant> result = gson.fromJson(json.get("data"), type);
        Platform.runLater(() -> members.setAll(result));
      }
      case "getOptions" -> {
        JsonObject json = (JsonObject) evt.getNewValue();
        Type type = new TypeToken<List<Option>>(){}.getType();
        List<Option> result = gson.fromJson(json.get("data"), type);
        Platform.runLater(() -> options.setAll(result));
      }
      case "sendMessage" -> {
        JsonObject json = (JsonObject) evt.getNewValue();
        Message message = gson.fromJson(json.get("data"), Message.class);
        Platform.runLater(() -> messages.add(message));
      }
    }
  }

  public void loadData() {
    if (selectedParty.get() == null) return;
    var i = model.getItems(selectedParty.get());
    var m = model.getParticipants(selectedParty.get());
    var o = model.getOptions(selectedParty.get());
    Platform.runLater(() -> {
      items.setAll(i);
      members.setAll(m);
      options.setAll(o);
    });
  }

  public ObservableList<Item> itemsProperty()         { return items; }
  public ObservableList<Participant> membersProperty() { return members; }
  public ObservableList<Option> optionsProperty()     { return options; }
  public StringProperty errorProperty()               { return errorProperty; }
  public StringProperty messageInputProperty()        { return messageInput; }

  public Party getSelectedParty() { return selectedParty.get(); }

  public String getRoleForCurrentUser(String partyId) {
    if (selectedParty.get() == null) return null;
    return model.getRole(LocalUser.getUser(), selectedParty.get());
  }

  public String getStatusForCurrentUser(String partyId) {
    return model.getStatus(LocalUser.getUser(), selectedParty.get());
  }

  public String getTopVotedOption(String partyId) {
    return model.getTopVotedOption(partyId);
  }

  public boolean hasVotedInParty(String id) {
    return model.hasVotedInParty(LocalUser.getUser().getId(), id);
  }

  public boolean hasVotedForOption(String optionId) {
    return model.hasVotedForOption(LocalUser.getUser().getId(), optionId);
  }

  public void voteForOption(String optionId) {
    List<Option> updated = model.voteForOption(optionId, LocalUser.getUser().getId());
    Platform.runLater(() -> options.setAll(updated));
  }

  public void removeVote(String optionId) {
    List<Option> updated = model.removeVote(optionId, LocalUser.getUser().getId());
    Platform.runLater(() -> options.setAll(updated));
  }

  public void claimItem(String itemId) {
    List<Item> updated = model.claimItem(itemId, LocalUser.getUser().getId());
    Platform.runLater(() -> items.setAll(updated));
  }

  public void unclaimItem(String itemId) {
    List<Item> updated = model.unclaimItem(itemId);
    Platform.runLater(() -> items.setAll(updated));
  }

  public void acceptInvitation() {
    if (selectedParty.get() == null) return;
    model.acceptInvite(LocalUser.getUser(), selectedParty.get());
  }

  public void declineInvitation() {
    if (selectedParty.get() == null) return;
    model.declineInvite(LocalUser.getUser(), selectedParty.get());
  }

  public void leaveParty() {
    if (selectedParty.get() == null) return;
    model.leaveParty(LocalUser.getUser(), selectedParty.get());
  }

  public void addFriend(User friend) {
    model.addFriend(LocalUser.getUser(), friend);
  }

  public List<Message> getMessages() {
    Party party = selectedParty.get();
    if (party == null) return new ArrayList<>();
    return model.getMessages(party.getId());
  }

  public void loadMessages() {
    Party party = selectedParty.get();
    if (party == null) return;
    List<Message> msgs = model.getMessages(party.getId());
    if (msgs == null) return;
    List<Message> sorted = new ArrayList<>(msgs);
    sorted.sort(Comparator.comparing(m -> {
      try {
        String s = m.getSentAt();
        return LocalDateTime.parse(s.length() > 19 ? s.substring(0, 19) : s);
      } catch (Exception e) { return LocalDateTime.MIN; }
    }));
    Platform.runLater(() -> messages.setAll(sorted));
  }

  public Message sendMessage() {
    Party party = selectedParty.get();
    String content = messageInput.get().trim();
    if (party == null || content.isEmpty()) return null;
    return model.sendMessage(party.getId(), LocalUser.getUser().getId(), content);
  }
}