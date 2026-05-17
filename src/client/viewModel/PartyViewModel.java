package client.viewModel;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import shared.model.*;

import java.util.ArrayList;
import java.util.List;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class PartyViewModel implements PropertyChangeListener
{
  private PartyModel model;
  private ObjectProperty<Party> selectedParty;
  private final StringProperty errorProperty;
  private final StringProperty messageInput;
  private ObservableList<Participant> members;
  private ObservableList<Item> items;

  public PartyViewModel(PartyModel model, ObjectProperty<Party> selectedParty) {
    this.model = model;
    this.selectedParty = selectedParty;
    errorProperty  = new SimpleStringProperty("");
    messageInput   = new SimpleStringProperty("");
    model.addListener("something", this);
  }

  public StringProperty messageInputProperty() { return messageInput; }

  public ObservableList<Item> getItems() {
    if (selectedParty.get() == null) return FXCollections.emptyObservableList();
    return FXCollections.observableArrayList(model.getItems(selectedParty.get()));
  }

  public ObservableList<Participant> getMembers() {
    return FXCollections.observableArrayList(
        model.getParticipants(selectedParty.get())
    );
  }

  public String getRole() {
    return model.getRole(LocalUser.getUser(), selectedParty.get());
  }

  public ObservableList<Option> getOptions() {
    if (selectedParty.get() == null) return FXCollections.emptyObservableList();
    return FXCollections.observableArrayList(model.getOptions(selectedParty.get()));
  }

  public Party getSelectedParty() {
    return selectedParty.get();
  }

  public String getRoleForCurrentUser(String partyId) {
    if (selectedParty.get() == null) return null;
    return model.getRole(LocalUser.getUser(), selectedParty.get());
  }

  public void acceptInvitation() {
    Party party = selectedParty.get();
    if (party == null) return;
    model.acceptInvite(LocalUser.getUser(), party);
  }

  public void declineInvitation() {
    Party party = selectedParty.get();
    if (party == null) return;
    model.declineInvite(LocalUser.getUser(), party);
  }

  public String getStatusForCurrentUser(String partyId) {
    return model.getStatus(LocalUser.getUser(), selectedParty.get());
  }

  public String getTopVotedOption(String partyId) {
    return model.getTopVotedOption(partyId);
  }

  public void claimItem(String itemId) {
    model.claimItem(itemId, LocalUser.getUser().getId());
  }

  public void unclaimItem(String itemId) {
    model.unclaimItem(itemId);
  }

  public void leaveParty() {
    Party party = selectedParty.get();
    if (party == null) return;
    model.leaveParty(LocalUser.getUser(), party);
  }

  public boolean hasVotedInParty(String id){
    return model.hasVotedInParty(LocalUser.getUser().getId(), id);
  }

  public boolean hasVotedForOption(String optionId) {
    return model.hasVotedForOption(LocalUser.getUser().getId(), optionId);
  }

  public void voteForOption(String optionId) {
    model.voteForOption(optionId, LocalUser.getUser().getId());
  }

  public void removeVote(String optionId) {
    model.removeVote(optionId, LocalUser.getUser().getId());
  }

  public void addFriend(User friend) {
    model.addFriend(LocalUser.getUser(), friend);
  }

  public List<Message> getMessages() {
    Party party = selectedParty.get();
    if (party == null) return new ArrayList<>();
    return model.getMessages(party.getId());
  }

  public Message sendMessage() {
    Party party   = selectedParty.get();
    String content = messageInput.get().trim();
    if (party == null || content.isEmpty()) return null;
    return model.sendMessage(party.getId(), LocalUser.getUser().getId(), content);
  }

  @Override public void propertyChange(PropertyChangeEvent evt)
  {

  }
}
