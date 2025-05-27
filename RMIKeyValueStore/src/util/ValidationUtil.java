package util;

/**
 * The ValidationUtil interface provides utility methods to validate server and client command-line arguments.
 * It ensures that valid port numbers and required parameters are provided before execution.
 */
public interface ValidationUtil {

  /**
   * Validates the server's command-line arguments.
   * Ensures that exactly one argument (port number) is provided and that it is valid.
   * If the validation fails, the program terminates with an error message.
   *
   * @param args The command-line arguments passed to the server.
   * @return The validated port number.
   */
  static int validateServerArgs(String[] args) {
    if (args.length != 1) {
      System.err.println("Usage: java <KeyValueStoreServer.java> <port: Number>");
      System.exit(1);
    }

    try {
      validatePort(args, 0); // Validate the provided port number
    } catch (NumberFormatException e) {
      System.err.println("ERROR: Invalid port number.");
      System.exit(1);
    }

    return Integer.parseInt(args[0]); // Return validated port number
  }

  /**
   * Validates the client's command-line arguments.
   * Ensures that exactly two arguments (hostname and port number) are provided and that the port is valid.
   * If the validation fails, the program terminates with an error message.
   *
   * @param args The command-line arguments passed to the client.
   */
  static void validateClientArgs(String[] args) {
    if (args.length != 2) {
      System.out.println("Usage: java <***Client.java> <hostname: String> <port number: Integer>");
      System.exit(1);
    }

    try {
      validatePort(args, 1); // Validate the provided port number
    } catch (NumberFormatException e) {
      System.err.println("Invalid port number.");
      System.exit(1);
    }
  }

  /**
   * Validates a port number by checking if it is within the valid range (1-65535).
   * If the port number is invalid, the program terminates with an error message.
   *
   * @param args The command-line arguments array.
   * @param index The index in the array where the port number is expected.
   * @throws NumberFormatException If the port number is not a valid integer.
   */
  private static void validatePort(String[] args, int index) {
    int port = Integer.parseInt(args[index]); // Convert port argument to an integer
    if (port < 1 || port > 65535) {
      System.err.println("Invalid port: " + port);
      System.exit(1);
    }
  }
}
