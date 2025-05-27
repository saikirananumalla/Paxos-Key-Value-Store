package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.logging.Logger;

import service.KeyValueService;
import service.KeyValueServiceImpl;
import util.RequestHandler;
import util.LoggerUtil;
import util.ValidationUtil;

/**
 * UDPServer is a single-threaded key-value store server that communicates with clients over UDP.
 * It listens for incoming datagram packets, processes PUT, GET, and DELETE requests, and sends responses.
 * The server runs indefinitely until manually stopped.
 */
public class UDPServer {
  private static final Logger LOGGER = Logger.getLogger(UDPServer.class.getName());
  private static final KeyValueService keyValueService;

  static {
    LoggerUtil.setupCustomLogger(LOGGER);
    keyValueService = new KeyValueServiceImpl(new HashMap<>());
  }

  /**
   * Main method to start the UDP server.
   * The server listens for incoming UDP packets and processes client requests.
   *
   * @param args Command-line arguments: [0] - Server port number.
   */
  public static void main(String[] args) {
    int port = ValidationUtil.validateServerArgs(args);

    try (DatagramSocket datagramSocket = new DatagramSocket(port)) {
      LOGGER.info("Server started on port " + port);

      // Buffer for receiving data
      byte[] buffer = new byte[1024];

      // Continuous loop to listen for client requests
      while (true) {
        DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
        datagramSocket.receive(requestPacket); // Receive incoming request

        // Get client address and port
        String clientAddress = requestPacket.getAddress().toString() + ":" + requestPacket.getPort();
        String message = new String(requestPacket.getData(), 0, requestPacket.getLength()).trim();
        LOGGER.info("Received request: " + message + " from client: " + clientAddress);

        // Validate packet size to prevent over-sized messages
        if (requestPacket.getLength() > 1024) {
          LOGGER.warning("ERROR: Received oversize message from " + clientAddress);
          String errorResponse = "ERROR: Message exceeds max allowed size (1024 bytes)";
          sendPacket(errorResponse, requestPacket, datagramSocket);
          continue;
        }

        // Process the request using the request handler
        String result = RequestHandler.handle(message, keyValueService);

        // Handle malformed requests - empty or whitespace input or invalid operation or key value
        if (result == null) {
          LOGGER.warning("Received malformed request: " + message + " from " + clientAddress);
          String errorResponse = "ERROR: Malformed request";
          sendPacket(errorResponse, requestPacket, datagramSocket);
          continue;
        }

        // Send response back to the client
        sendPacket(result, requestPacket, datagramSocket);
        LOGGER.info("Sent response to " + clientAddress + ": " + result);
      }
    } catch (IOException e) {
      LOGGER.severe("Server error: " + e.getMessage());
    }
  }

  /**
   * Sends a response packet to the client.
   *
   * @param message        The message to be sent as a response.
   * @param requestPacket  The original request packet (used for client address and port).
   * @param datagramSocket The UDP socket used for communication.
   * @throws IOException If an error occurs while sending the packet.
   */
  private static void sendPacket(String message, DatagramPacket requestPacket,
                                 DatagramSocket datagramSocket) throws IOException {
    DatagramPacket responsePacket = new DatagramPacket(
            message.getBytes(), message.length(),
            requestPacket.getAddress(), requestPacket.getPort()
    );
    datagramSocket.send(responsePacket);
  }
}
