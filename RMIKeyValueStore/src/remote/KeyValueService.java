package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface defines a remote key-value store
 * service that supports basic operations over Java RMI.
 * Clients can interact with this service to perform GET, PUT, and DELETE operations
 * on a distributed key-value store.
 */
public interface KeyValueService extends Remote {

  /**
   * Retrieves the value associated with the given key.
   *
   * @param key The key whose associated value is to be returned.
   * @return The value associated with the key.
   * @throws RemoteException If an RMI communication error occurs or is key does not exist.
   */
  String get(String key) throws RemoteException;

  /**
   * Stores or updates a key-value pair in the key-value store.
   *
   * @param key   The key to store or update.
   * @param value The value associated with the key.
   * @return The previous value associated with the key.
   * @throws RemoteException If an RMI communication error occurs or is key does not exist.
   */
  String put(String key, String value) throws RemoteException;

  /**
   * Removes the key-value pair associated with the given key.
   *
   * @param key The key to be removed from the store.
   * @return The value that was associated with the key.
   * @throws RemoteException If an RMI communication error occurs or is key does not exist.
   */
  String delete(String key) throws RemoteException;
}
