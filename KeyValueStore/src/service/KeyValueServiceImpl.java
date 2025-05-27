package service;

import java.util.Map;
import util.Action;
import util.Operation;

/**
 * KeyValueServiceImpl is an implementation of the KeyValueService interface.
 * It provides a simple in-memory key-value store supporting PUT, GET, and DELETE operations.
 */
public class KeyValueServiceImpl implements KeyValueService {
  private final Map<String, String> store;

  /**
   * Constructs a KeyValueServiceImpl with the provided key-value store.
   *
   * @param store A map representing the in-memory key-value storage.
   */
  public KeyValueServiceImpl(Map<String, String> store) {
    this.store = store;
  }

  /**
   * Executes a given action (PUT, GET, DELETE) on the key-value store.
   *
   * @param action The action to perform, including the operation, key, and value.
   * @return A success message, failure message, or the retrieved value for GET operations.
   */
  public String act(Action action) {
    if (action.getOperation().equals(Operation.PUT)) {
      store.put(action.getKey(), action.getValue());
      return "Success: PUT. For key: " + action.getKey() + " and value: " + action.getValue();
    }

    else if (action.getOperation().equals(Operation.DELETE)) {
      if (!store.containsKey(action.getKey())) {
        return "Failed DELETE. Key: " + action.getKey() + " NOT FOUND.";
      }
      store.remove(action.getKey());
      return "Success: DELETE. For key: " + action.getKey();
    }

    else if (action.getOperation().equals(Operation.GET)) {
      if (!store.containsKey(action.getKey())) {
        return "Failed GET. Key: " + action.getKey() + " NOT FOUND.";
      }
      return "Success: GET. Value: " + store.get(action.getKey()) + " for key: " + action.getKey();
    }

    // Return null for unrecognized operations
    return null;
  }
}