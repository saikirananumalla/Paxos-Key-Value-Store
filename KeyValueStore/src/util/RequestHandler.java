package util;

import service.KeyValueService;

/**
 * The RequestHandler interface provides utility methods for processing incoming client requests.
 * It maps raw string messages to Action objects and invokes the corresponding operation in the key-value store.
 */
public interface RequestHandler {

  /**
   * Handles a client request by converting it into an Action and executing it on the key-value store.
   *
   * @param message         The raw client request string.
   * @param keyValueService The key-value store service that executes the action.
   * @return The result of the operation, or null if the request is invalid.
   */
  static String handle(String message, KeyValueService keyValueService) {
    Action action = mapToAction(message); // Convert message to Action object
    String result = null;
    if (action != null) {
      result = keyValueService.act(action); // Execute the action
    }
    return result;
  }

  /**
   * Parses a raw message string and maps it to an Action object.
   * Supports the following operations:
   * - PUT <key> <value>
   * - GET <key>
   * - DELETE <key>
   *
   * @param message The raw client request string.
   * @return The corresponding Action object, or null if the request is malformed.
   */
  private static Action mapToAction(String message) {
    if (message == null || message.trim().isEmpty()) {
      return null; // Reject empty or null messages
    }

    message = message.trim(); // Remove leading/trailing spaces

    Operation operation;
    String key;
    String value = null;

    // Split message into parts (command, key, and optional value)
    String[] parts = message.split("\\s+");

    // Ensure at least an operation and a key exist
    if (parts.length < 2) {
      return null;
    }

    String action = parts[0];

    // Process PUT operation (must have exactly 3 parts: PUT key value)
    if (action.equals(Operation.PUT.getValue())) {
      if (parts.length != 3) {
        return null;
      }
      operation = Operation.PUT;
      key = parts[1];
      value = parts[2];
    }

    // Process GET operation (must have exactly 2 parts: GET key)
    else if (action.equals(Operation.GET.getValue())) {
      if (parts.length != 2) {
        return null;
      }
      operation = Operation.GET;
      key = parts[1];
    }

    // Process DELETE operation (must have exactly 2 parts: DELETE key)
    else if (action.equals(Operation.DELETE.getValue())) {
      if (parts.length != 2) {
        return null;
      }
      operation = Operation.DELETE;
      key = parts[1];
    }

    // Invalid command
    else {
      return null;
    }

    return new Action(operation, key, value);
  }
}
