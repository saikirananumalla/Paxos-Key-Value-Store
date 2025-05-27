package client.util;

/**
 * The Action class encapsulates an operation (PUT, GET, DELETE, EXIT) to be performed on the key-value store.
 * It holds the operation type, associated key, and optional value (for PUT operations).
 */
public class Action {
  // The operation type (PUT, GET, DELETE, EXIT)
  private final Operation operation;
  private final String key;
  private final String value;


  /**
   * Constructs an Action instance representing a request to the key-value store.
   *
   * @param operation The type of operation (EXIT).
   */
  public Action(Operation operation) {
    this.operation = operation;
    this.key = null;
    this.value = null;
  }

  /**
   * Constructs an Action instance representing a request to the key-value store.
   *
   * @param operation The type of operation (GET, DELETE).
   * @param key       The key to be used in the operation.
   */
  public Action(Operation operation, String key) {
    this.operation = operation;
    this.key = key;
    this.value = null;
  }


  /**
   * Constructs an Action instance representing a request to the key-value store.
   *
   * @param operation The type of operation (PUT, GET, DELETE, EXIT).
   * @param key       The key to be used in the operation.
   * @param value     The value associated with the key (only applicable for PUT operations).
   */
  public Action(Operation operation, String key, String value) {
    this.operation = operation;
    this.key = key;
    this.value = value;
  }

  /**
   * Retrieves the operation type (PUT, GET, DELETE, EXIT).
   *
   * @return The operation to be performed.
   */
  public Operation getOperation() {
    return operation;
  }

  /**
   * Retrieves the key associated with the operation.
   *
   * @return The key for the operation.
   */
  public String getKey() {
    return key;
  }

  /**
   * Retrieves the value associated with the key (only relevant for PUT operations).
   *
   * @return The value for the key, or null if not applicable.
   */
  public String getValue() {
    return value;
  }

  /**
   * Converts the action to a string representation.
   *
   * @return string form of the object
   */
  @Override
  public String toString() {
    String val = operation +
            ", key='" + key + '\'';

    if (value != null) {
      val += ", value='" + value + '\'';
    }

    val += '}';

    return val;
  }
}
