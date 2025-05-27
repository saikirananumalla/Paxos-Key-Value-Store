package client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.*;

import client.util.Operation;
import remote.KeyValueService;
import client.util.Action;
import client.util.ClientInputUtil;
import util.LoggerUtil;
import util.ValidationUtil;

/**
 * RMIClient is a command-line client that connects to a server to perform key-value operations.
 * It supports sending PUT, GET, DELETE and EXIT commands and handles server responses.
 * The client retries connection attempts if the server is unresponsive.
 */
public class Client {
  private static final Logger LOGGER = Logger.getLogger(Client.class.getName());

  static {
    LoggerUtil.setupCustomLogger(LOGGER);
  }

  /**
   * Main method to start the RMI client.
   * It takes server hostname and port as command-line arguments and continuously
   * interacts with the server until the user exits.
   *
   * @param args Command-line arguments: [0] - Server hostname, [1] - Server port.
   */
  public static void main(String[] args) {
    ValidationUtil.validateClientArgs(args);

    String hostname = args[0];
    int port = Integer.parseInt(args[1]);

    try {
      Registry registry = LocateRegistry.getRegistry(hostname, port);
      KeyValueService keyValueService = (KeyValueService) registry.lookup("keyValueService");
      LOGGER.info("Connected to server on " + hostname + ":" + port);

      RequestHandler.prePopulateStore(keyValueService);

      // Continuously send requests and process responses
      while (true) {
        Action command = ClientInputUtil.getUserCommand();
        if (Operation.EXIT.equals(command.getOperation())) {
          LOGGER.warning("Exiting...");
          break;
        }
        try {
          RequestHandler.execute(command, keyValueService);
        } catch (RemoteException e) {
          LOGGER.log(Level.SEVERE, "ERROR: " + e.getCause().getMessage());
        }
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to connect to server", e);
      throw new RuntimeException(e);
    }
  }
}
