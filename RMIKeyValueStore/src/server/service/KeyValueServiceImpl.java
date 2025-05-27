package server.service;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import remote.KeyValueService;
import util.LoggerUtil;

/**
 * Implementation of the {@link KeyValueService} interface, providing a multithreaded,
 * distributed key-value store over RMI.
 * This implementation utilizes an {@link ExecutorService} to handle concurrent client requests
 * efficiently. All operations (GET, PUT, DELETE) are executed asynchronously but return
 * results synchronously.
 */
public class KeyValueServiceImpl extends UnicastRemoteObject implements KeyValueService {

  private static final Logger LOGGER = Logger.getLogger(KeyValueServiceImpl.class.getName());
  private final ConcurrentMap<String, String> keyValueMap;
  private final ExecutorService executorService;

  static {
    LoggerUtil.setupCustomLogger(LOGGER);
  }

  public KeyValueServiceImpl(ConcurrentMap<String, String> keyValueMap,
                             ExecutorService executorService) throws RemoteException {
    super();
    this.keyValueMap = keyValueMap;
    this.executorService = executorService;
  }

  /**
   * Retrieves the value for a given key.
   *
   * @param key The key to retrieve.
   * @return The value associated with the given key.
   * @throws RemoteException If the operation encounters an error or the key does not exist.
   */
  @Override
  public String get(String key) throws RemoteException {
    LOGGER.info("Received GET request key: " + key);
    try {
      return executorService.submit(() -> {
        if (keyValueMap.containsKey(key)) {
          String value = keyValueMap.get(key);
          LOGGER.info("GET request processed successfully for key: " + key + " with value: " + value);
          return value;
        } else {
          throw new NoSuchObjectException("Key not found: " + key);
        }
      }).get();
    } catch (InterruptedException | ExecutionException e) {
      String message = "Error processing GET request for key: " + key;
      if (e.getCause() instanceof NoSuchObjectException) {
        message = "Key not found: " + key;
      }
      LOGGER.log(Level.SEVERE, message, e);
      throw new RemoteException(message);
    }
  }

  /**
   * Sets a key-value pair in the store.
   *
   * @param key   The key to store or update.
   * @param value The value associated with the key.
   * @throws RemoteException If the operation encounters an error.
   */
  @Override
  public String put(String key, String value) throws RemoteException {
    LOGGER.info("Received PUT request key: " + key + " value: " + value);
    try {
      return executorService.submit(() -> {
        keyValueMap.put(key, value);
        LOGGER.info("PUT request processed successfully for key: " + key + " with value: " + value);
        return value;
      }).get();
    } catch (InterruptedException | ExecutionException e) {
      LOGGER.log(Level.SEVERE, "Error processing PUT request for key: " + key + e.getMessage());
      throw new RemoteException("Error in PUT operation for key: " + key);
    }
  }

  /**
   * Removes a key-value pair from the store.
   *
   * @param key The key to be removed.
   * @return The value that was associated with the key, or null if the key did not exist.
   * @throws RemoteException If the operation encounters an error.
   */
  @Override
  public String delete(String key) throws RemoteException {
    LOGGER.info("Received DELETE request key: " + key);
    try {
      return executorService.submit(() -> {
        if (!keyValueMap.containsKey(key)) {
          throw new NoSuchObjectException("Key not found: " + key);
        }
        String value = keyValueMap.remove(key);
        LOGGER.info("DELETE request processed successfully for key: " + key + " with value: " + value);
        return value;
      }).get();
    }
    catch (InterruptedException | ExecutionException e) {
      String message = "Error processing DELETE request for key: " + key;
      if (e.getCause() instanceof NoSuchObjectException) {
        message = "Key not found: " + key;
      }
      LOGGER.log(Level.SEVERE, message, e);
      throw new RemoteException(message);
    }
  }
}
