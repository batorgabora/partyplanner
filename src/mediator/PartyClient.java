package mediator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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

  public void requestGetAll()
  {
    JsonObject request = createRequest("getAll");
    sendRequest(request);
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


  private JsonObject createRequest(String action)
  {
    JsonObject request = new JsonObject();
    request.addProperty("type", "request");
    request.addProperty("action", action);
    return request;
  }

  // User operations
  public void requestLogin(String username, String password) {
    JsonObject request = createRequest("login");
    request.addProperty("username", username);
    request.addProperty("password", password);
    sendRequest(request);
  }

  public void requestAddFriend(int userId, int friendId) {
    JsonObject request = createRequest("addFriend");
    request.addProperty("userId", userId);
    request.addProperty("friendId", friendId);
    sendRequest(request);
  }

  // Party operations
  public void requestJoinParty(int userId, int partyId) {
    JsonObject request = createRequest("joinParty");
    request.addProperty("userId", userId);
    request.addProperty("partyId", partyId);
    sendRequest(request);
  }

  public void requestLeaveParty(int userId, int partyId) {
    JsonObject request = createRequest("leaveParty");
    request.addProperty("userId", userId);
    request.addProperty("partyId", partyId);
    sendRequest(request);
  }

  // Organizer operations
  public void requestCreateParty(String title, String description, String location, int organizerId) {
    JsonObject request = createRequest("createParty");
    request.addProperty("title", title);
    request.addProperty("description", description);
    request.addProperty("location", location);
    request.addProperty("organizerId", organizerId);
    sendRequest(request);
  }

  public void requestDeleteParty(int partyId) {
    JsonObject request = createRequest("deleteParty");
    request.addProperty("partyId", partyId);
    sendRequest(request);
  }

  public void requestAddParticipant(int partyId, int userId) {
    JsonObject request = createRequest("addParticipant");
    request.addProperty("partyId", partyId);
    request.addProperty("userId", userId);
    sendRequest(request);
  }

  public void requestRemoveParticipant(int partyId, int userId) {
    JsonObject request = createRequest("removeParticipant");
    request.addProperty("partyId", partyId);
    request.addProperty("userId", userId);
    sendRequest(request);
  }

  private synchronized void sendRequest(JsonObject request)
  {
    outputWriter.println(gson.toJson(request));
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
