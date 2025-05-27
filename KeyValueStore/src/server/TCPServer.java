package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Logger;

import service.KeyValueService;
import service.KeyValueServiceImpl;
import util.RequestHandler;
import util.LoggerUtil;
import util.ValidationUtil;

/**
 * TCPServer is a single-threaded key-value store server that communicates with clients over TCP.
 * It listens for client connections, processes PUT, GET, and DELETE requests, and sends responses.
 * The server remains active indefinitely until manually stopped.
 */
public class TCPServer {
  private static final Logger LOGGER = Logger.getLogger(TCPServer.class.getName());
  private static final KeyValueService keyValueService;

  static {
    LoggerUtil.setupCustomLogger(LOGGER);
    keyValueService = new KeyValueServiceImpl(new HashMap<>());
  }

  /**
   * Main method to start the TCP server.
   * The server listens on the given port and processes incoming client connections.
   *
   * @param args Command-line arguments: [0] - Server port number.
   */
  public static void main(String[] args) {
    int port = ValidationUtil.validateServerArgs(args);

    try (ServerSocket server = new ServerSocket(port)) {
      LOGGER.info("Server started on port " + port);

      // Continuously accept and process client connections
      while (true) {
        try (Socket clientSocket = server.accept()) {
          LOGGER.info("Client connected: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

          // Handle the client request
          handleClient(clientSocket);
        } catch (IOException e) {
          LOGGER.warning("Client disconnected or connection error: " + e.getMessage());
        }
      }
    } catch (IOException e) {
      LOGGER.severe("Server error: " + e.getMessage());
    }
  }

  /**
   * Handles client communication.
   * Reads incoming messages, handles them using the RequestHandler, and sends responses.
   *
   * @param clientSocket The client socket connection.
   */
  private static void handleClient(Socket clientSocket) {
    try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))
    ) {
      String message;

      // Continuously process incoming requests from the client
      while ((message = reader.readLine()) != null) {
        LOGGER.info("Received request: " + message + " from client: " +
                clientSocket.getInetAddress() + ":" + clientSocket.getPort());

        // Handle request and generate response
        String result = RequestHandler.handle(message, keyValueService);

        // Send response back to the client
        if (result != null) {
          writer.write(result);
          LOGGER.info("Sent response: " + result + " to client: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
        } else {
          // Handle malformed requests - empty or whitespace input or invalid operation or key value
          LOGGER.warning("Received malformed request " + message + " from " +
                  clientSocket.getInetAddress() + ":" + clientSocket.getPort());
          writer.write("ERROR: Malformed request");
        }
        writer.newLine();
        writer.flush();
      }

      LOGGER.info("Client disconnected.");

    } catch (IOException e) {
      LOGGER.warning("Error handling client: " + e.getMessage());
    }
  }
}
