package server;

import remote.KeyValueService;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import util.LoggerUtil;

/**
 * The LoadBalancer class distributes incoming client requests across multiple
 * key-value store replicas using a round-robin strategy.
 */
public class LoadBalancer extends UnicastRemoteObject implements KeyValueService {
  private static final Logger LOGGER = Logger.getLogger(LoadBalancer.class.getName());
  private final List<? extends IKeyValueServer> replicas;
  private final AtomicInteger roundRobinIndex = new AtomicInteger(0);

  static {
    LoggerUtil.setupCustomLogger(LOGGER);
  }

  /**
   * Constructs a LoadBalancer with the given list of key-value store replicas.
   *
   * @param replicas The list of replicas that handle key-value store requests.
   * @throws RemoteException If an error occurs during RMI object export.
   */
  public LoadBalancer(List<? extends IKeyValueServer> replicas) throws RemoteException {
    super();
    this.replicas = replicas;
  }

  /**
   * Selects the next replica in a round-robin fashion.
   *
   * @return The next replica server to handle a request.
   */
  private IKeyValueServer getNextReplica() {
    int index = roundRobinIndex.getAndUpdate(i -> (i + 1) % replicas.size());
    IKeyValueServer selectedReplica = replicas.get(index);
    LOGGER.info("Forwarding request to replica: " +  selectedReplica.getServerID());
    return selectedReplica;
  }

  /**
   * Handles a GET request by forwarding it to the next available replica.
   *
   * @param key The key to retrieve from the key-value store.
   * @return The value associated with the key.
   * @throws RemoteException If an error occurs during remote communication.
   */
  @Override
  public String get(String key) throws RemoteException {
    LOGGER.info("LoadBalancer received GET request for key: " + key);
    return getNextReplica().get(key);
  }

  /**
   * Handles a PUT request by forwarding it to the next available replica.
   *
   * @param key   The key to store in the key-value store.
   * @param value The value to associate with the key.
   * @return True if the PUT operation was successful, false otherwise.
   * @throws RemoteException If an error occurs during remote communication.
   */
  @Override
  public boolean put(String key, String value) throws RemoteException {
    LOGGER.info("LoadBalancer received PUT request for key: " + key);
    return getNextReplica().put(key, value);
  }

  /**
   * Handles a DELETE request by forwarding it to the next available replica.
   *
   * @param key The key to delete from the key-value store.
   * @return True if the DELETE operation was successful, false otherwise.
   * @throws RemoteException If an error occurs during remote communication.
   */
  @Override
  public boolean delete(String key) throws RemoteException {
    LOGGER.info("LoadBalancer received DELETE request for key: " + key);
    return getNextReplica().delete(key);
  }
}
