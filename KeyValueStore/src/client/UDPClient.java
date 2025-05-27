package client;

import java.io.IOException;
import java.net.*;
import java.util.logging.Logger;

import util.ClientInputUtil;
import util.LoggerUtil;
import util.ValidationUtil;

/**
 * UDPClient is a command-line client that communicates with a UDP server to perform key-value operations.
 * It supports sending PUT, GET, and DELETE commands while handling server responses.
 * The client also enforces a message size limit (1024 bytes) and implements timeout handling.
 */
public class UDPClient {
  private static final Logger LOGGER = Logger.getLogger(UDPClient.class.getName());

  static {
    LoggerUtil.setupCustomLogger(LOGGER);
  }

  /**
   * Main method to start the UDP client.
   * It takes server hostname/IP and port as command-line arguments and continuously
   * interacts with the server until the user exits.
   *
   * @param args Command-line arguments: [0] - Server hostname/IP, [1] - Server port.
   */
  public static void main(String[] args) {
    ValidationUtil.validateClientArgs(args);

    String hostname = args[0];
    int port = Integer.parseInt(args[1]);

    // Create a DatagramSocket for UDP communication
    try (DatagramSocket datagramSocket = new DatagramSocket()) {
      datagramSocket.setSoTimeout(5000);  // Set a timeout for server response

      LOGGER.info("UDP Client started. Connecting to server at " + hostname + ":" + port);

      // Prepopulates the store
      prePopulateStore(datagramSocket, hostname, port);

      // Infinite loop to send and receive messages
      while (true) {
        String[] command = ClientInputUtil.getUserCommand();

        if ("EXIT".equalsIgnoreCase(command[0])) {
          LOGGER.info("Client exiting...");
          break;
        }

        // Convert the command array into a single request string
        String request = String.join(" ", command);
        byte[] sendData = request.getBytes();

        // Enforce message size limit (1024 bytes)
        if (sendData.length > 1024) {
          LOGGER.warning("ERROR: Request exceeds max length (1024 bytes). Please shorten your input.");
          continue;
        }

        // Create a UDP packet and send it to the server
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                InetAddress.getByName(hostname), port);
        datagramSocket.send(sendPacket);
        LOGGER.info("Sent request to server: " + request);

        try {
          // Buffer to store the server response
          byte[] receiveData = new byte[1024];
          DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
          datagramSocket.receive(receivePacket);

          // Convert received data into a string response
          String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
          LOGGER.info("Server Response: " + response);
        } catch (SocketTimeoutException e) {
          LOGGER.warning("No response from server. It may be down or busy.");
        }
      }
    } catch (IOException ioException) {
      LOGGER.severe("Connection error: " + ioException.getMessage());
    } catch (Exception e) {
      LOGGER.severe("Unexpected error occurred in client: " + e.getMessage());
    }
  }

  /**
   * Pre-populates the key-value store with 5 PUTs, 5 GETs, and 5 DELETEs.
   */
  private static void prePopulateStore(DatagramSocket socket, String hostname, int port) throws IOException {
    String[] keys = {"name", "city", "course", "language", "IDE"};
    String[] values = {"Sai", "Boston", "CS6650", "Java", "IntelliJ"};

    // Send 5 PUT commands for pre population
    for (int i = 0; i < values.length; i++) {
      sendAndLog(socket, hostname, port, "PUT " + values[i] + " " + keys[i]);
    }

    // Send 5 PUT commands
    for (int i = 0; i < keys.length; i++) {
      sendAndLog(socket, hostname, port, "PUT " + keys[i] + " " + values[i]);
    }

    // Send 5 GET commands
    for (String key : keys) {
      sendAndLog(socket, hostname, port, "GET " + key);
    }

    // Send 5 DELETE commands
    for (String key : keys) {
      sendAndLog(socket, hostname, port, "DELETE " + key);
    }
  }

  /**
   * Sends a request to the server and logs the response.
   */
  private static void sendAndLog(DatagramSocket socket, String hostname, int port, String request) throws IOException {
    byte[] sendData = request.getBytes();
    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(hostname), port);
    socket.send(sendPacket);
    LOGGER.info("Sent: " + request);

    try {
      byte[] receiveData = new byte[1024];
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      socket.receive(receivePacket);

      String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
      LOGGER.info("Received: " + response);
    } catch (SocketTimeoutException e) {
      LOGGER.warning("No response from server. It may be down or busy.");
    }
  }
}
