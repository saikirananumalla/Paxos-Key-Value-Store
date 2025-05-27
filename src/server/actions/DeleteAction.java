package server.actions;

import java.util.Map;

/**
 * Represents a delete action that removes a key from the key-value store.
 */
public class DeleteAction implements Action {
  private final String key;

  /**
   * Constructs a {@code DeleteAction} with the specified key.
   *
   * @param key The key to be deleted from the key-value store.
   */
  public DeleteAction(String key) {
    this.key = key;
  }

  /**
   * Executes the delete operation on the provided key-value store.
   * Removes the key from the store if it exists.
   *
   * @param keyValueStore The key-value store where the action will be applied.
   */
  @Override
  public void execute(Map<String, String> keyValueStore) {
    keyValueStore.remove(key);
  }

  /**
   * Retrieves the key associated with this delete action.
   *
   * @return The key to be deleted.
   */
  @Override
  public String getKey() {
    return key;
  }

  /**
   * Provides a string representation of the delete action.
   *
   * @return A human-readable string describing the action.
   */
  @Override
  public String toString() {
    return "DeleteAction [key=" + key + "]";
  }
}
