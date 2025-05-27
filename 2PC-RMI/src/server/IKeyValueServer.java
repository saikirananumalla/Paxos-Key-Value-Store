package server;

import remote.KeyValueService;
import server.actions.Action;

import java.rmi.RemoteException;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a key-value store server that supports distributed transactions
 * using the Two-Phase Commit (2PC) protocol.
 */
public interface IKeyValueServer extends KeyValueService {

  /**
   * Initiates the prepare phase of the Two-Phase Commit (2PC) protocol.
   * Stores the transaction in a pending state and ensures all replicas
   * can prepare for the commit.
   *
   * @param transactionId Unique transaction identifier.
   * @param action        The action to be executed (PUT or DELETE).
   * @return A {@link CompletableFuture} that resolves to {@code true} if prepared successfully, {@code false} otherwise.
   * @throws RemoteException If an RMI error occurs.
   */
  CompletableFuture<Boolean> prepareForCommit(String transactionId, Action action) throws RemoteException;

  /**
   * Commits a previously prepared transaction.
   * The commit phase ensures that all replicas finalize the transaction.
   *
   * @param transactionId Unique transaction identifier.
   * @return A {@link CompletableFuture} that resolves to {@code true} if committed successfully, {@code false} otherwise.
   * @throws Exception If the transaction does not exist or commit fails.
   */
  CompletableFuture<Boolean> commit(String transactionId) throws Exception;

  /**
   * Aborts a previously prepared transaction.
   * If the commit phase fails, the transaction should be rolled back.
   *
   * @param transactionId Unique transaction identifier.
   * @return A {@link CompletableFuture} that resolves to {@code true} if aborted successfully, {@code false} otherwise.
   * @throws Exception If the transaction does not exist or abort fails.
   */
  CompletableFuture<Boolean> abort(String transactionId) throws Exception;

  /**
   * Get the server id of the replica server.
   * @return the server id.
   */
  String getServerID();
}
