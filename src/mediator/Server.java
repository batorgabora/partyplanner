package mediator;

import model.ModelManager;
import model.PartyModel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
  public static final int PORT = 9999;
  private final PartyModel model;

  public Server(PartyModel model) {
    this.model = model;
  }

  public void start() {
    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
      System.out.println("Server started on port " + PORT);
      while (true) {
        Socket socket = serverSocket.accept();
        System.out.println("Client connected: " + socket.getInetAddress());
        PartyClientHandler handler = new PartyClientHandler(socket, model);
        Thread thread = new Thread(handler);
        thread.start();
      }
    } catch (IOException e) {
      throw new IllegalStateException("Server failed.", e);
    }
  }
}