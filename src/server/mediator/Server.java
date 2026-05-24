package server.mediator;

import shared.model.PartyModel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {
  public static final int PORT = 9999;
  private final PartyModel model;
  private final List<PartyClientHandler> handlers = Collections.synchronizedList(new ArrayList<>());

  public Server(PartyModel model) {
    this.model = model;
  }

  public void start() {
    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
      System.out.println("Server started on port " + PORT);
      while (true) {
        Socket socket = serverSocket.accept();
        System.out.println("Client connected: " + socket.getInetAddress());
        PartyClientHandler handler = new PartyClientHandler(socket, model, handlers);
        handlers.add(handler);
        new Thread(handler).start();
      }
    } catch (IOException e) {
      throw new IllegalStateException("Server failed.", e);
    }
  }
}