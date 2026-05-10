package server;

import server.model.ModelManager;
import server.mediator.Server;

public class ServerMain {
  public static void main(String[] args) {
    Server server = new Server(new ModelManager());
    server.start();
  }
}
