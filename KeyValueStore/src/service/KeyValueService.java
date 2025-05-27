package service;

import util.Action;

/**
 * The KeyValueService interface defines the contract for a key-value store service.
 * It provides a method to execute actions such as PUT, GET, and DELETE on the key-value store.
 */
public interface KeyValueService {

  /**
   * Executes the specified action on the key-value store.
   *
   * @param action The action to perform (e.g., PUT, GET, DELETE).
   * @return The result of the action, such as the retrieved value, success message, or error message.
   */
  String act(Action action);
}
