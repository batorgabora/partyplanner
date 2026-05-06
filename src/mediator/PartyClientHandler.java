package mediator;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.ModelManager;
import model.PartyModel;

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

  private void handleRequest(JsonObject request) {
    String action = request.get("action").getAsString();

    switch (action) {
      case "getAll" -> handleGetAll();
      case "login" -> handleLogin(request);
      case "joinParty" -> handleJoinParty(request);
      case "leaveParty" -> handleLeaveParty(request);
      case "createParty" -> handleCreateParty(request);
      case "deleteParty" -> handleDeleteParty(request);
      case "addParticipant" -> handleAddParticipant(request);
      case "removeParticipant" -> handleRemoveParticipant(request);
      case "addFriend" -> handleAddFriend(request);
      default -> sendError("Unknown action: " + action);
    }
  }

  private void handleGetAll() {
    sendResponse("getAll", gson.toJson(model.getParties(null)));
  }

  private void handleLogin(JsonObject request) {
    String username = request.get("username").getAsString();
    String password = request.get("password").getAsString();
    var user = model.login(username, password);
    if (user != null) {
      sendResponse("login", gson.toJson(user));
    } else {
      sendError("Invalid credentials.");
    }
  }

  private void handleJoinParty(JsonObject request) {
    int userId = request.get("userId").getAsInt();
    int partyId = request.get("partyId").getAsInt();
    // model.joinParty(userId, partyId);
    sendResponse("joinParty", "success");
  }

  private void handleLeaveParty(JsonObject request) {
    int userId = request.get("userId").getAsInt();
    int partyId = request.get("partyId").getAsInt();
    // model.leaveParty(userId, partyId);
    sendResponse("leaveParty", "success");
  }

  private void handleCreateParty(JsonObject request) {
    String title = request.get("title").getAsString();
    String description = request.get("description").getAsString();
    String location = request.get("location").getAsString();
    int organizerId = request.get("organizerId").getAsInt();
    // model.createParty(title, description, location, organizerId);
    sendResponse("createParty", "success");
  }

  private void handleDeleteParty(JsonObject request) {
    int partyId = request.get("partyId").getAsInt();
    // model.deleteParty(partyId);
    sendResponse("deleteParty", "success");
  }

  private void handleAddParticipant(JsonObject request) {
    int partyId = request.get("partyId").getAsInt();
    int userId = request.get("userId").getAsInt();
    // model.addParticipant(partyId, userId);
    sendResponse("addParticipant", "success");
  }

  private void handleRemoveParticipant(JsonObject request) {
    int partyId = request.get("partyId").getAsInt();
    int userId = request.get("userId").getAsInt();
    // model.removeParticipant(partyId, userId);
    sendResponse("removeParticipant", "success");
  }

  private void handleAddFriend(JsonObject request) {
    int userId = request.get("userId").getAsInt();
    int friendId = request.get("friendId").getAsInt();
    // model.addFriend(userId, friendId);
    sendResponse("addFriend", "success");
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