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
    String key = action.getKey(), value = action.getValue();
    switch (action.getOperation()) {
      case PUT:
        boolean resp = keyValueService.put(action.getKey(), action.getValue());
        if (resp) {
          LOGGER.info("Success. Put Value: " + value + " for Key : " + key);
        } else {
          LOGGER.info("Operation failed. Put Value: " + value + " for Key : " + key);
        }
        break;
      case DELETE:
        boolean delResp = keyValueService.delete(action.getKey());
        if (delResp) {
          LOGGER.info("Success. Delete for Key : " + key);
        } else {
          LOGGER.info("Operation failed. Delete for Key : " + key);
        }
        break;
      case GET:
        response = keyValueService.get(action.getKey());
        if (response != null) {
          LOGGER.info("GET Success. Value: " + response + " for Key : " + action.getKey());
        } else {
          LOGGER.info("GET Failed. Value: " + response + " for Key : " + action.getKey());
        }

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
