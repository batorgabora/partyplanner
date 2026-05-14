package client.mediator;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import shared.util.Action;
import java.time.LocalDate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class PartyClient
{
  private final Socket socket;
  private final BufferedReader inputReader;
  private final PrintWriter outputWriter;
  private final Gson gson;

  public PartyClient(String host, int port)
  {
    try
    {
      socket = new Socket(host, port);
      inputReader = new BufferedReader(
          new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
      outputWriter = new PrintWriter(
          new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
          true);
      gson = new Gson();
    }
    catch (IOException e)
    {
      throw new IllegalStateException("Could not connect to server.", e);
    }
  }

  public String receive()
  {
    try
    {
      return inputReader.readLine();
    }
    catch (IOException e)
    {
      throw new IllegalStateException("Connection to server was lost.", e);
    }
  }

  private JsonObject createRequest(Action action)
  {
    JsonObject request = new JsonObject();
    request.addProperty("type", "request");
    request.addProperty("action", action.getValue());
    return request;
  }

  private synchronized void sendRequest(JsonObject request)
  {
    outputWriter.println(gson.toJson(request));
  }

  // User operations
  public void requestLogin(String username, String password) {
    JsonObject request = createRequest(Action.LOGIN);
    request.addProperty("username", username);
    request.addProperty("password", password);
    sendRequest(request);
  }

  public void requestCreateAccount(String username, String password, String confirmPassword, String mail) {
    JsonObject request = createRequest(Action.CREATE_ACCOUNT);
    request.addProperty("username", username);
    request.addProperty("password", password);
    request.addProperty("confirmPassword", confirmPassword);
    request.addProperty("mail", mail);
    sendRequest(request);
  }

  public void requestGetAllUsers() {
    JsonObject request = createRequest(Action.GET_ALL_USERS);
    sendRequest(request);
  }

  public void requestAddFriend(String userId, String friendId) {
    JsonObject request = createRequest(Action.ADD_FRIEND);
    request.addProperty("userId", userId);
    request.addProperty("friendId", friendId);
    sendRequest(request);
  }

  // Party operations
  public void requestGetAll() {
    JsonObject request = createRequest(Action.GET_ALL);
    sendRequest(request);
  }

  public void requestGetMyParties(String userId) {
    JsonObject request = createRequest(Action.GET_MY_PARTIES);
    request.addProperty("userId", userId);
    sendRequest(request);
  }

  public void requestGetInvitedParties(String userId) {
    JsonObject request = createRequest(Action.GET_INVITED_PARTIES);
    request.addProperty("userId", userId);
    sendRequest(request);
  }

  public void requestCreateParty(String name, String description, String location, String organizerId, LocalDate date) {
    JsonObject request = createRequest(Action.CREATE_PARTY);
    request.addProperty("name", name);
    request.addProperty("description", description);
    request.addProperty("location", location);
    request.addProperty("organizerId", organizerId);
    if (date != null) {
      request.addProperty("date", date.toString());
    }
    sendRequest(request);
  }

  public void requestDeleteParty(String partyId) {
    JsonObject request = createRequest(Action.DELETE_PARTY);
    request.addProperty("partyId", partyId);
    sendRequest(request);
  }

  public void requestUpdateParty(String partyId, String name, String description, String location) {
    JsonObject request = createRequest(Action.UPDATE_PARTY);
    request.addProperty("partyId", partyId);
    request.addProperty("name", name);
    request.addProperty("description", description);
    request.addProperty("location", location);
    sendRequest(request);
  }

  public void requestUpdatePartyDate(String partyId, String date) {
    JsonObject request = createRequest(Action.UPDATE_PARTY_DATE);
    request.addProperty("partyId", partyId);
    request.addProperty("date", date);
    sendRequest(request);
  }

  public void requestJoinParty(String userId, String partyId) {
    JsonObject request = createRequest(Action.JOIN_PARTY);
    request.addProperty("userId", userId);
    request.addProperty("partyId", partyId);
    sendRequest(request);
  }

  public void requestLeaveParty(String userId, String partyId) {
    JsonObject request = createRequest(Action.LEAVE_PARTY);
    request.addProperty("userId", userId);
    request.addProperty("partyId", partyId);
    sendRequest(request);
  }

  public void requestAcceptInvite(String userId, String partyId) {
    JsonObject request = createRequest(Action.ACCEPT_INVITE);
    request.addProperty("userId", userId);
    request.addProperty("partyId", partyId);
    sendRequest(request);
  }

  public void requestDeclineInvite(String userId, String partyId) {
    JsonObject request = createRequest(Action.DECLINE_INVITE);
    request.addProperty("userId", userId);
    request.addProperty("partyId", partyId);
    sendRequest(request);
  }

  public void requestGetStatus(String userId, String partyId) {
    JsonObject request = createRequest(Action.GET_STATUS);
    request.addProperty("userId", userId);
    request.addProperty("partyId", partyId);
    sendRequest(request);
  }

  public void requestGetRole(String userId, String partyId) {
    JsonObject request = createRequest(Action.GET_ROLE);
    request.addProperty("userId", userId);
    request.addProperty("partyId", partyId);
    sendRequest(request);
  }

  // Item operations
  public void requestGetItems(String partyId) {
    JsonObject request = createRequest(Action.GET_ITEMS);
    request.addProperty("partyId", partyId);
    sendRequest(request);
  }

  public void requestAddItem(String partyId, String name) {
    JsonObject request = createRequest(Action.ADD_ITEM);
    request.addProperty("partyId", partyId);
    request.addProperty("name", name);
    sendRequest(request);
  }

  public void requestRemoveItem(String itemId) {
    JsonObject request = createRequest(Action.REMOVE_ITEM);
    request.addProperty("itemId", itemId);
    sendRequest(request);
  }

  // Option operations
  public void requestGetOptions(String partyId) {
    JsonObject request = createRequest(Action.GET_OPTIONS);
    request.addProperty("partyId", partyId);
    sendRequest(request);
  }

  public void requestAddOption(String partyId, String proposal) {
    JsonObject request = createRequest(Action.ADD_OPTION);
    request.addProperty("partyId", partyId);
    request.addProperty("proposal", proposal);
    sendRequest(request);
  }

  public void requestRemoveOption(String optionId) {
    JsonObject request = createRequest(Action.REMOVE_OPTION);
    request.addProperty("optionId", optionId);
    sendRequest(request);
  }

  public void requestVoteForOption(String optionId, String userId) {
    JsonObject request = createRequest(Action.VOTE_FOR_OPTION);
    request.addProperty("optionId", optionId);
    request.addProperty("userId", userId);
    sendRequest(request);
  }

  public void requestRemoveVote(String optionId, String userId) {
    JsonObject request = createRequest(Action.REMOVE_VOTE);
    request.addProperty("optionId", optionId);
    request.addProperty("userId", userId);
    sendRequest(request);
  }

  public void requestGetTopVotedOption(String partyId) {
    JsonObject request = createRequest(Action.GET_TOP_VOTED_OPTION);
    request.addProperty("partyId", partyId);
    sendRequest(request);
  }

  public void requestHasVoted(String userId, String partyId) {
    JsonObject request = createRequest(Action.HAS_VOTED);
    request.addProperty("userId", userId);
    request.addProperty("partyId", partyId);
    sendRequest(request);
  }

  // Participant operations
  public void requestGetParticipants(String partyId) {
    JsonObject request = createRequest(Action.GET_PARTICIPANTS);
    request.addProperty("partyId", partyId);
    sendRequest(request);
  }

  public void requestAddParticipant(String partyId, String userId) {
    JsonObject request = createRequest(Action.ADD_PARTICIPANT);
    request.addProperty("partyId", partyId);
    request.addProperty("userId", userId);
    sendRequest(request);
  }

  public void requestRemoveParticipant(String partyId, String userId) {
    JsonObject request = createRequest(Action.REMOVE_PARTICIPANT);
    request.addProperty("partyId", partyId);
    request.addProperty("userId", userId);
    sendRequest(request);
  }

  public void requestSendMessage(String partyId, String userId, String content) {
    JsonObject request = createRequest(Action.SEND_MESSAGE);
    request.addProperty("partyId", partyId);
    request.addProperty("userId", userId);
    request.addProperty("content", content);
    sendRequest(request);
  }

  public void requestGetMessages(String partyId) {
    JsonObject request = createRequest(Action.GET_MESSAGES);
    request.addProperty("partyId", partyId);
    sendRequest(request);
  }

  public void close()
  {
    try
    {
      socket.close();
    }
    catch (IOException ignored)
    {
    }
  }
}