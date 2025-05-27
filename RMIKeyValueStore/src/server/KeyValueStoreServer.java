package server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import remote.KeyValueService;
import server.service.KeyValueServiceImpl;
import util.LoggerUtil;
import util.ValidationUtil;

/**
 * Multithreaded RMI-based Key-Value Store Server.
 * Handles concurrent GET, PUT, and DELETE requests over RMI.
 */
public class KeyValueStoreServer {
  private static final Logger LOGGER = Logger.getLogger(KeyValueStoreServer.class.getName());

  static {
    LoggerUtil.setupCustomLogger(LOGGER);
  }

  /**
   * The main entry point for the Key-Value Store Server.
   * Initializes the RMI registry, creates the key-value store service,
   * and binds it to the registry for remote client access.
   *
   * @param args Command-line arguments. The argument should be the port number.
   */
  public static void main(String[] args) {
    int port = ValidationUtil.validateServerArgs(args);

    try {
      // Start RMI registry
      LocateRegistry.createRegistry(port);

      // Initialize Key-Value Store
      ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
      KeyValueService keyValueService = new KeyValueServiceImpl(new ConcurrentHashMap<>(), executorService);

      // Bind the server.service to RMI
      Naming.rebind("rmi://localhost:" + port + "/keyValueService", keyValueService);
      LOGGER.info("Multi-threaded Key-Value Store Server running on port " + port + "...");

      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        LOGGER.info("Shutting down server...");
        executorService.shutdown();
      }));

    } catch (Exception e) {
      LOGGER.severe("Server failed to start: " + e.getMessage());
    }
  }
}
