package server.mediator;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import server.model.ModelManager;
import shared.model.Party;
import shared.model.PartyModel;
import shared.model.User;
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
  private final PropertyChangeSupport support = new PropertyChangeSupport(this);;

  public PartyClientHandler(Socket socket, PartyModel model) {
    this.socket = socket;
    this.model = model;
    this.gson = new Gson();
    try {
      inputReader = new BufferedReader(
          new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
      outputWriter = new PrintWriter(
          new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
          true);
    } catch (IOException e) {
      throw new IllegalStateException("Could not initialize client handler.", e);
    }
  }

  @Override
  public void run() {
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
      case GET_ALL -> {
        handleGetAll();
        support.firePropertyChange("parties", null, null);
      }
      case JOIN_PARTY -> {
        handleJoinParty(request);
        support.firePropertyChange("parties", null, null);
      }
      case LEAVE_PARTY -> {
        handleLeaveParty(request);
        support.firePropertyChange("parties", null, null);
      }
      case CREATE_PARTY -> {
        handleCreateParty(request);
        support.firePropertyChange("parties", null, null);
      }
      case DELETE_PARTY -> {
        handleDeleteParty(request);
        support.firePropertyChange("parties", null, null);
      }
      case ADD_PARTICIPANT -> {
        handleAddParticipant(request);
        support.firePropertyChange("parties", null, null);
      }
      case REMOVE_PARTICIPANT -> {
        handleRemoveParticipant(request);
        support.firePropertyChange("parties", null, null);
      }
      case ADD_FRIEND -> {
        handleAddFriend(request);
        support.firePropertyChange("parties", null, null);
      }
      case LOGIN -> handleLogin(request);
    }
  }

  private void handleGetAll() {
    sendResponse("getAll", gson.toJson(model.getParties(null)));
  }

  private void handleLogin(JsonObject request) {
    String username = request.get("username").getAsString();
    String password = request.get("password").getAsString();

    System.out.println("partyclienthandler " + username + " -- " + password);

    User user = model.login(username, password);
    if (user != null) {
      sendResponse("login", gson.toJson(user));
    } else {
      sendError("Invalid credentials.");
    }
  }

  private void handleJoinParty(JsonObject request) {

  }

  private void handleLeaveParty(JsonObject request) {

  }

  private void handleCreateParty(JsonObject request) {
    String name = request.get("name").getAsString();
    String description = request.get("description").getAsString();
    String location = request.get("location").getAsString();
    String organizerId = request.get("organizerId").getAsString();
    LocalDate date = (request.has("date") && !request.get("date").isJsonNull())
        ? LocalDate.parse(request.get("date").getAsString())
        : null;

    if (name == null || name.trim().isEmpty()) {
      sendError("Party name is required.");
      return;
    }
    if (organizerId == null || organizerId.trim().isEmpty()) {
      sendError("Organizer ID is required.");
      return;
    }

    Party party = model.createParty(name, description, location, organizerId, date);
    sendResponse("createParty", gson.toJson(party));
  }

  private void handleDeleteParty(JsonObject request) {

  }

  private void handleAddParticipant(JsonObject request) {

  }

  private void handleRemoveParticipant(JsonObject request) {

  }

  private void handleAddFriend(JsonObject request) {

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
    try {
      socket.close();
    } catch (IOException ignored) {
    }
  }
}
