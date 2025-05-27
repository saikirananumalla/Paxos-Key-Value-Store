package client;

import java.io.*;
import java.net.*;
import java.util.logging.*;

import util.ClientInputUtil;
import util.LoggerUtil;
import util.ValidationUtil;

/**
 * TCPClient is a command-line client that connects to a TCP server to perform key-value operations.
 * It supports sending PUT, GET, and DELETE commands and handles server responses.
 * The client retries connection attempts if the server is unresponsive.
 */
public class TCPClient {
  private static final Logger LOGGER = Logger.getLogger(TCPClient.class.getName());

  static {
    LoggerUtil.setupCustomLogger(LOGGER);
  }

  /**
   * Main method to start the TCP client.
   * It takes server hostname/IP and port as command-line arguments and continuously
   * interacts with the server until the user exits.
   *
   * @param args Command-line arguments: [0] - Server hostname/IP, [1] - Server port.
   */
  public static void main(String[] args) {
    ValidationUtil.validateClientArgs(args);
    boolean prePopulate = false;

    String hostnameOrIp = args[0];
    int port = Integer.parseInt(args[1]);
    int retry = 0; // Counter to track connection retries

    // Infinite loop to keep the client running
    while (true) {
      try (
              // Establish a TCP connection to the server
              Socket socket = new Socket(InetAddress.getByName(hostnameOrIp), port);
              BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
              BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
      ) {
        socket.setSoTimeout(5000); // Set a timeout for server responses
        LOGGER.info("Connected to server.");
        retry = 0; // Reset retry counter on successful connection

        // Pre-populate the key-value store
        if (!prePopulate) {
          prePopulateStore(writer, reader);
          prePopulate = true;
        }

        // Continuously send requests and process responses
        while (true) {
          String[] command = ClientInputUtil.getUserCommand();

          if ("EXIT".equals(command[0])) {
            LOGGER.info("Client exiting...");
            return;
          }

          // Convert the command array into a single request string
          String request = String.join(" ", command);

          // Send the request to the server
          writer.write(request);
          writer.newLine();
          writer.flush();

          // Read server response
          String response = reader.readLine();

          if (response == null) {
            LOGGER.severe("Server disconnected.");
            break;
          }

          LOGGER.info("Received: " + response);
        }

      } catch (SocketTimeoutException e) {
        LOGGER.warning("Server timeout! No response received.");
      } catch (IOException e) {
        // Handle connection loss with retry mechanism
        if (retry == 5) {
          LOGGER.warning("Retry limit exceeded. Exiting...");
          System.exit(0);
        }

        LOGGER.severe("Connection lost. Retrying in 3 seconds...");
        retry++;
        try {
          Thread.sleep(3000); // Wait before retrying
        } catch (InterruptedException ignored) {
          LOGGER.warning("Interrupted while waiting for server response. Exiting...");
          System.exit(1);
        }
      }
    }
  }

  /**
   * Pre-populates the key-value store with 5 PUTs, 5 GETs, and 5 DELETEs.
   */
  private static void prePopulateStore(BufferedWriter writer, BufferedReader reader) throws IOException {
    String[] keys = {"name", "city", "course", "language", "IDE"};
    String[] values = {"Sai", "Boston", "CS6650", "Java", "IntelliJ"};

    // Send 5 PUT commands for pre population
    for (int i = 0; i < values.length; i++) {
      sendAndLog(writer, reader, "PUT " + values[i] + " " + keys[i]);
    }

    // Send 5 PUT commands
    for (int i = 0; i < keys.length; i++) {
      sendAndLog(writer, reader, "PUT " + keys[i] + " " + values[i]);
    }

    // Send 5 GET commands
    for (String key : keys) {
      sendAndLog(writer, reader, "GET " + key);
    }

    // Send 5 DELETE commands
    for (String key : keys) {
      sendAndLog(writer, reader, "DELETE " + key);
    }
  }

  /**
   * Sends a request to the server and logs the response.
   */
  private static void sendAndLog(BufferedWriter writer, BufferedReader reader, String command)
          throws IOException {
    writer.write(command);
    writer.newLine();
    writer.flush();

    String response = reader.readLine();
    if (response != null) {
      LOGGER.info("Sent: " + command + " | Received: " + response);
    }
  }
}
