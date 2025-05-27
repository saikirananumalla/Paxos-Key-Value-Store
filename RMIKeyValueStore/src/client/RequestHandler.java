package client;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import client.util.Action;
import client.util.Operation;
import remote.KeyValueService;
import util.LoggerUtil;

/**
 * This utility class provides a method to execute operations
 * (PUT, GET, DELETE, EXIT) on the key-value store using an instance of {@link KeyValueService}.
 * This class serves as a centralized execution point for processing {@link Action} requests
 * and delegating them to the remote RMI key-value store.
 */
public class RequestHandler {

  static Logger LOGGER = Logger.getLogger(RequestHandler.class.getName());

  static {
    LoggerUtil.setupCustomLogger(LOGGER);
  }

  /**
   * This method checks the operation type from the {@code Action} object and invokes the
   * corresponding method on the key-value service.
   *
   * @param action          The action to be performed (PUT, GET, DELETE, EXIT).
   * @param keyValueService The remote key-value service handling the request.
   * @throws RemoteException If an error occurs during remote execution.
   */
  public static void execute(Action action, KeyValueService keyValueService) throws RemoteException {
    LOGGER.info("Sending request to server: " + action);
    String response;
    switch (action.getOperation()) {
      case PUT:
        response = keyValueService.put(action.getKey(), action.getValue());
        LOGGER.info("Success. Put Value: " + response + " for Key : " + action.getKey());
        break;
      case DELETE:
        response = keyValueService.delete(action.getKey());
        LOGGER.info("Success. Deleted value: " + response + " for key : " + action.getKey());
        break;
      case GET:
        response = keyValueService.get(action.getKey());
        LOGGER.info("Success. Value: " + response + " for Key : " + action.getKey());
        break;
      default:
        throw new IllegalArgumentException("Unknown action: " + action);
    }
  }

  /**
   * Pre-populates the key-value store with 10 PUTs, 5 GETs, and 5 DELETE.
   */
  public static void prePopulateStore(KeyValueService keyValueService) throws IOException {
    String[] keys = {"name", "city", "course", "language", "IDE"};
    String[] values = {"Sai", "Boston", "CS6650", "Java", "IntelliJ"};

    LOGGER.info("-------------Prepopulating the key value store --------------------------");

    // Send 5 PUT commands for pre population
    for (int i = 0; i < values.length; i++) {
      Action action = new Action(Operation.PUT, values[i], keys[i]);
      execute(action, keyValueService);
    }

    // Send 5 PUT commands
    for (int i = 0; i < keys.length; i++) {
      Action action = new Action(Operation.PUT, keys[i], values[i]);
      execute(action, keyValueService);
    }

    // Send 5 GET commands
    for (String key : keys) {
      Action action = new Action(Operation.GET, key);
      execute(action, keyValueService);
    }

    // Send 5 DELETE commands
    for (String key : keys) {
      Action action = new Action(Operation.DELETE, key);
      execute(action, keyValueService);
    }
    LOGGER.info("-------------Success: pre population of the key value store--------------");
  }
}
