package server.mediator;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import server.dao.*;
import shared.model.*;
import shared.util.Action;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class PartyClientHandler implements Runnable {
  private final Socket socket;
  private final PartyModel model;
  private final BufferedReader inputReader;
  private final PrintWriter outputWriter;
  private final Gson gson;
  private final PropertyChangeSupport support = new PropertyChangeSupport(this);

  public PartyClientHandler(Socket socket, PartyModel model) {
    this.socket = socket;
    this.model = model;
    this.gson = new Gson();
    try {
      inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
      outputWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
    } catch (IOException e) {
      throw new IllegalStateException("Could not initialize client handler.", e);
    }
  }

  @Override public void run() {
    try {
      String line;
      while ((line = inputReader.readLine()) != null) {
        JsonObject request = JsonParser.parseString(line).getAsJsonObject();
        handleRequest(request);
      }
    } catch (IOException e) {
      System.out.println("Client disconnected.");
    } finally {
      close();
    }
  }

  public void addListener(String propertyName, PropertyChangeListener listener) {
    support.addPropertyChangeListener(propertyName, listener);
  }

  private void handleRequest(JsonObject request) {
    Action action;
    try {
      action = Action.fromValue(request.get("action").getAsString());
    } catch (IllegalArgumentException e) {
      sendError(e.getMessage());
      return;
    }
    switch (action) {
      case LOGIN                -> handleLogin(request);
      case CREATE_ACCOUNT       -> handleCreateAccount(request);
      case GET_ALL_USERS        -> handleGetAllUsers();
      case ADD_FRIEND           -> handleAddFriend(request);
      case GET_ALL              -> handleGetAll(request);
      case GET_MY_PARTIES       -> handleGetMyParties(request);
      case GET_INVITED_PARTIES  -> handleGetInvitedParties(request);
      case CREATE_PARTY         -> handleCreateParty(request);
      case UPDATE_PARTY         -> handleUpdateParty(request);
      case UPDATE_PARTY_DATE    -> handleUpdatePartyDate(request);
      case DELETE_PARTY         -> handleDeleteParty(request);
      case JOIN_PARTY           -> handleJoinParty(request);
      case LEAVE_PARTY          -> handleLeaveParty(request);
      case ACCEPT_INVITE        -> handleAcceptInvite(request);
      case DECLINE_INVITE       -> handleDeclineInvite(request);
      case GET_STATUS           -> handleGetStatus(request);
      case GET_ROLE             -> handleGetRole(request);
      case GET_ITEMS            -> handleGetItems(request);
      case ADD_ITEM             -> handleAddItem(request);
      case REMOVE_ITEM          -> handleRemoveItem(request);
      case GET_OPTIONS          -> handleGetOptions(request);
      case ADD_OPTION           -> handleAddOption(request);
      case REMOVE_OPTION        -> handleRemoveOption(request);
      case VOTE_FOR_OPTION      -> handleVoteForOption(request);
      case REMOVE_VOTE          -> handleRemoveVote(request);
      case GET_TOP_VOTED_OPTION -> handleGetTopVotedOption(request);
      case HAS_VOTED            -> handleHasVoted(request);
      case GET_PARTICIPANTS     -> handleGetParticipants(request);
      case ADD_PARTICIPANT      -> handleAddParticipant(request);
      case REMOVE_PARTICIPANT   -> handleRemoveParticipant(request);
      case SEND_MESSAGE         -> handleSendMessage(request);
      case GET_MESSAGES         -> handleGetMessages(request);
    }
  }

  private void handleLogin(JsonObject request) {
    String username = request.get("username").getAsString();
    String password  = request.get("password").getAsString();
    User user = model.login(username, password);
    if (user != null) sendResponse("login", gson.toJson(user));
    else sendError("Invalid credentials.");
  }

  private void handleCreateAccount(JsonObject request) {
    String username        = request.get("username").getAsString();
    String password        = request.get("password").getAsString();
    String confirmPassword = request.get("confirmPassword").getAsString();
    String mail            = request.get("mail").getAsString();
    User user = model.createAccount(username, password, confirmPassword, mail);
    if (user != null) sendResponse("createAccount", gson.toJson(user));
    else sendError("Failed to create account.");
  }

  private void handleGetAllUsers() {
    sendResponse("getAllUsers", gson.toJson(model.getAllUsers()));
  }

  private void handleAddFriend(JsonObject request) {
    User user   = new UserDAO().getById(request.get("userId").getAsString());
    User friend = new UserDAO().getById(request.get("friendId").getAsString());
    if (user == null || friend == null) { sendError("User not found."); return; }
    model.addFriend(user, friend);
    sendResponse("addFriend", "ok");
  }

  private void handleGetAll(JsonObject request) {
    if (!request.has("userId")) { sendResponse("getAll", "[]"); return; }
    User user = new UserDAO().getById(request.get("userId").getAsString());
    if (user == null) { sendResponse("getAll", "[]"); return; }
    sendResponse("getAll", gson.toJson(model.getInvitedParties(user)));
  }

  private void handleGetMyParties(JsonObject request) {
    User user = new UserDAO().getById(request.get("userId").getAsString());
    if (user == null) { sendResponse("getMyParties", "[]"); return; }
    sendResponse("getMyParties", gson.toJson(model.getMyParties(user)));
  }

  private void handleGetInvitedParties(JsonObject request) {
    User user = new UserDAO().getById(request.get("userId").getAsString());
    if (user == null) { sendResponse("getInvitedParties", "[]"); return; }
    sendResponse("getInvitedParties", gson.toJson(model.getInvitedParties(user)));
  }

  private void handleCreateParty(JsonObject request) {
    String name        = request.get("name").getAsString();
    String description = request.get("description").getAsString();
    String location    = request.get("location").getAsString();
    String organizerId = request.get("organizerId").getAsString();
    LocalDate date = (request.has("date") && !request.get("date").isJsonNull())
        ? LocalDate.parse(request.get("date").getAsString()) : null;
    if (name.trim().isEmpty()) { sendError("Party name is required."); return; }
    Party party = model.createParty(name, description, location, organizerId, date);
    sendResponse("createParty", gson.toJson(party));
  }

  private void handleUpdateParty(JsonObject request) {
    Party party = new PartyDAO().getById(request.get("partyId").getAsString());
    if (party == null) { sendError("Party not found."); return; }
    model.updateParty(party, request.get("name").getAsString(),
        request.get("description").getAsString(), request.get("location").getAsString());
    sendResponse("updateParty", "ok");
  }

  private void handleUpdatePartyDate(JsonObject request) {
    Party party = new PartyDAO().getById(request.get("partyId").getAsString());
    if (party == null) { sendError("Party not found."); return; }
    model.updatePartyDate(party, request.get("date").getAsString());
    sendResponse("updatePartyDate", "ok");
  }

  private void handleDeleteParty(JsonObject request) {
    Party party = new PartyDAO().getById(request.get("partyId").getAsString());
    if (party == null) { sendError("Party not found."); return; }
    model.deleteParty(party);
    sendResponse("deleteParty", "ok");
  }

  private void handleJoinParty(JsonObject request) {
    User user   = new UserDAO().getById(request.get("userId").getAsString());
    Party party = new PartyDAO().getById(request.get("partyId").getAsString());
    if (user == null || party == null) { sendError("User or party not found."); return; }
    model.joinParty(user, party);
    sendResponse("joinParty", "ok");
  }

  private void handleLeaveParty(JsonObject request) {
    User user   = new UserDAO().getById(request.get("userId").getAsString());
    Party party = new PartyDAO().getById(request.get("partyId").getAsString());
    if (user == null || party == null) { sendError("User or party not found."); return; }
    model.leaveParty(user, party);
    sendResponse("leaveParty", "ok");
  }

  private void handleAcceptInvite(JsonObject request) {
    User user   = new UserDAO().getById(request.get("userId").getAsString());
    Party party = new PartyDAO().getById(request.get("partyId").getAsString());
    if (user == null || party == null) { sendError("User or party not found."); return; }
    model.acceptInvite(user, party);
    sendResponse("acceptInvite", "ok");
  }

  private void handleDeclineInvite(JsonObject request) {
    User user   = new UserDAO().getById(request.get("userId").getAsString());
    Party party = new PartyDAO().getById(request.get("partyId").getAsString());
    if (user == null || party == null) { sendError("User or party not found."); return; }
    model.declineInvite(user, party);
    sendResponse("declineInvite", "ok");
  }

  private void handleGetStatus(JsonObject request) {
    User user   = new UserDAO().getById(request.get("userId").getAsString());
    Party party = new PartyDAO().getById(request.get("partyId").getAsString());
    if (user == null || party == null) { sendError("User or party not found."); return; }
    sendResponse("getStatus", model.getStatus(user, party));
  }

  private void handleGetRole(JsonObject request) {
    User user   = new UserDAO().getById(request.get("userId").getAsString());
    Party party = new PartyDAO().getById(request.get("partyId").getAsString());
    if (user == null || party == null) { sendError("User or party not found."); return; }
    sendResponse("getRole", model.getRole(user, party));
  }

  private void handleGetItems(JsonObject request) {
    Party party = new PartyDAO().getById(request.get("partyId").getAsString());
    if (party == null) { sendError("Party not found."); return; }
    sendResponse("getItems", gson.toJson(model.getItems(party)));
  }

  private void handleAddItem(JsonObject request) {
    Party party = new PartyDAO().getById(request.get("partyId").getAsString());
    if (party == null) { sendError("Party not found."); return; }
    model.addItem(party, request.get("name").getAsString());
    sendResponse("addItem", "ok");
  }

  private void handleRemoveItem(JsonObject request) {
    String itemId = request.get("itemId").getAsString();
    Item item = new ItemDAO().getById(itemId);
    if (item == null) { sendError("Item not found."); return; }
    model.removeItem(item);
    sendResponse("removeItem", "ok");
  }

  private void handleGetOptions(JsonObject request) {
    Party party = new PartyDAO().getById(request.get("partyId").getAsString());
    if (party == null) { sendError("Party not found."); return; }
    sendResponse("getOptions", gson.toJson(model.getOptions(party)));
  }

  private void handleAddOption(JsonObject request) {
    Party party = new PartyDAO().getById(request.get("partyId").getAsString());
    if (party == null) { sendError("Party not found."); return; }
    model.addOption(party, request.get("proposal").getAsString());
    sendResponse("addOption", "ok");
  }

  private void handleRemoveOption(JsonObject request) {
    String optionId = request.get("optionId").getAsString();
    Option option = new OptionDAO().getById(optionId);
    if (option == null) { sendError("Option not found."); return; }
    model.removeOption(option);
    sendResponse("removeOption", "ok");
  }

  private void handleVoteForOption(JsonObject request) {
    model.voteForOption(request.get("optionId").getAsString(), request.get("userId").getAsString());
    sendResponse("voteForOption", "ok");
  }

  private void handleRemoveVote(JsonObject request) {
    model.removeVote(request.get("optionId").getAsString(), request.get("userId").getAsString());
    sendResponse("removeVote", "ok");
  }

  private void handleGetTopVotedOption(JsonObject request) {
    String top = model.getTopVotedOption(request.get("partyId").getAsString());
    sendResponse("getTopVotedOption", top != null ? top : "");
  }

  private void handleHasVoted(JsonObject request) {
    boolean result = model.hasVotedInParty(request.get("userId").getAsString(), request.get("partyId").getAsString());
    sendResponse("hasVoted", String.valueOf(result));
  }

  private void handleGetParticipants(JsonObject request) {
    Party party = new PartyDAO().getById(request.get("partyId").getAsString());
    if (party == null) { sendError("Party not found."); return; }
    sendResponse("getParticipants", gson.toJson(model.getParticipants(party)));
  }

  private void handleAddParticipant(JsonObject request) {
    Party party = new PartyDAO().getById(request.get("partyId").getAsString());
    User user   = new UserDAO().getById(request.get("userId").getAsString());
    if (party == null || user == null) { sendError("Party or user not found."); return; }
    model.addParticipant(party, new Participant(party, user));
    sendResponse("addParticipant", "ok");
  }

  private void handleRemoveParticipant(JsonObject request) {
    Party party = new PartyDAO().getById(request.get("partyId").getAsString());
    User user   = new UserDAO().getById(request.get("userId").getAsString());
    if (party == null || user == null) { sendError("Party or user not found."); return; }
    model.removeParticipant(party, new Participant(party, user));
    sendResponse("removeParticipant", "ok");
  }

  private void handleSendMessage(JsonObject request) {
    String partyId = request.get("partyId").getAsString();
    String userId  = request.get("userId").getAsString();
    String content = request.get("content").getAsString();
    if (content.trim().isEmpty()) { sendError("Message content cannot be empty."); return; }
    Party party = new PartyDAO().getById(partyId);
    if (party == null) { sendError("Party not found."); return; }
    Message message = model.sendMessage(partyId, userId, content);
    if (message == null) { sendError("Failed to send message."); return; }
    sendResponse("sendMessage", gson.toJson(message));
  }

  private void handleGetMessages(JsonObject request) {
    String partyId = request.get("partyId").getAsString();
    Party party = new PartyDAO().getById(partyId);
    if (party == null) { sendError("Party not found."); return; }
    sendResponse("getMessages", gson.toJson(model.getMessages(partyId)));
  }

  private void sendResponse(String action, String data) {
    JsonObject response = new JsonObject();
    response.addProperty("type", "response");
    response.addProperty("action", action);
    response.addProperty("data", data);
    outputWriter.println(gson.toJson(response));
  }

  private void sendError(String message) {
    JsonObject response = new JsonObject();
    response.addProperty("type", "error");
    response.addProperty("message", message);
    outputWriter.println(gson.toJson(response));
  }

  private void close() {
    try { socket.close(); } catch (IOException ignored) {}
  }
}