package server.actions;

import java.util.Map;

/**
 * Represents a put action that inserts or updates a key-value pair in the key-value store.
 */
public class PutAction implements Action {
  private final String key;
  private final String value;

  /**
   * Constructs a {@code PutAction} with the specified key and value.
   *
   * @param key   The key to be inserted or updated in the key-value store.
   * @param value The value to be associated with the key.
   */
  public PutAction(String key, String value) {
    this.key = key;
    this.value = value;
  }

  /**
   * Executes the put operation on the provided key-value store.
   * Inserts the key-value pair into the store or updates it if the key already exists.
   *
   * @param keyValueStore The key-value store where the action will be applied.
   */
  @Override
  public void execute(Map<String, String> keyValueStore) {
    keyValueStore.put(key, value);
  }

  /**
   * Retrieves the key associated with this put action.
   *
   * @return The key to be inserted or updated.
   */
  @Override
  public String getKey() {
    return key;
  }

  /**
   * Provides a string representation of the put action.
   *
   * @return A human-readable string describing the action.
   */
  @Override
  public String toString() {
    return "PutAction [key=" + key + ", value=" + value + "]";
  }
}
