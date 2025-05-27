package util;

/**
 * The Operation enum defines the supported operations for the key-value store.
 * It includes:
 * - PUT (Insert or update a key-value pair)
 * - GET (Retrieve a value by key)
 * - DELETE (Remove a key-value pair)
 */
public enum Operation {
  PUT("PUT"), GET("GET"), DELETE("DELETE");

  private final String operation;

  /**
   * Constructs an Operation enum with the specified operation string.
   *
   * @param operation The string representation of the operation.
   */
  Operation(String operation) {
    this.operation = operation;
  }

  /**
   * Retrieves the string representation of the operation.
   *
   * @return The operation as a string (e.g., "PUT", "GET", "DELETE").
   */
  public String getValue() {
    return operation;
  }
}
