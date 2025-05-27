package client.util;

import java.util.Scanner;

/**
 * The ClientInputUtil class provides a utility for handling user input in the client application.
 * It prompts users to enter commands (PUT, GET, DELETE, or EXIT) and validates their input.
 */
public class ClientInputUtil {
  // Scanner instance for reading user input from the console
  private static final Scanner scanner = new Scanner(System.in);

  /**
   * Prompts the user to select an operation and returns the corresponding command.
   * Supports:
   * - PUT (Add Key-Value)
   * - GET (Retrieve Value)
   * - DELETE (Remove Key)
   * - EXIT (Terminate the client)
   *
   * @return Action representing the selected command and its parameters.
   */
  public static Action getUserCommand() {
    while (true) {
      // Display menu options for the user
      System.out.println("\nSelect an operation:");
      System.out.println("1. PUT (Add Key-Value)");
      System.out.println("2. GET (Retrieve Value)");
      System.out.println("3. DELETE (Remove Key)");
      System.out.println("4. EXIT");

      System.out.print("Enter your choice: ");
      String choice = scanner.nextLine().trim();

      // Process the user choice and return the corresponding command
      switch (choice) {
        case "1":
          return getPutCommand();
        case "2":
          return getGetCommand();
        case "3":
          return getDeleteCommand();
        case "4":
          return getExitCommand();
        default:
          System.out.println("Invalid choice. Please enter 1, 2, 3, or 4.");
      }
    }
  }

  /**
   * Handles input for the PUT command (adding a key-value pair).
   *
   * @return Action containing the command and its arguments (["PUT", key, value]).
   */
  private static Action getPutCommand() {
    System.out.print("Enter key: ");
    String key = getValidInput();
    System.out.print("Enter value: ");
    String value = getValidInput();
    return new Action(Operation.PUT, key, value);
  }

  /**
   * Handles input for the GET command (retrieving a value by key).
   *
   * @return Action containing the command and its argument (["GET", key]).
   */
  private static Action getGetCommand() {
    System.out.print("Enter key: ");
    String key = getValidInput();
    return new Action(Operation.GET, key);
  }

  /**
   * Handles input for the DELETE command (removing a key-value pair).
   *
   * @return Action containing the command and its argument (["DELETE", key]).
   */
  private static Action getDeleteCommand() {
    System.out.print("Enter key: ");
    String key = getValidInput();
    return new Action(Operation.DELETE, key);
  }

  /**
   * Handles input for the EXIT command
   *
   * @return Action containing the command.
   */
  private static Action getExitCommand() {
    return new Action(Operation.EXIT);
  }

  /**
   * Reads and validates user input, ensuring it is non-empty.
   *
   * @return A valid user input string.
   */
  private static String getValidInput() {
    while (true) {
      String input = scanner.nextLine().trim();
      if (!input.isEmpty()) {
        return input;
      }
      System.out.print("Invalid input. Please enter a non-empty value: ");
    }
  }
}
