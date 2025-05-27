package server.actions;

import java.util.Map;

/**
 * Represents an action that can be executed on the key-value store.
 * Actions include operations like PUT and DELETE.
 */
public interface Action {

  /**
   * Executes the action on the provided key-value store.
   *
   * @param keyValueStore The key-value store where the action will be applied.
   */
  void execute(Map<String, String> keyValueStore);

  /**
   * Retrieves the key associated with this action.
   *
   * @return The key on which this action operates.
   */
  String getKey();

  /**
   * Provides a string representation of the action.
   *
   * @return A human-readable string describing the action.
   */
  @Override
  String toString();
}
