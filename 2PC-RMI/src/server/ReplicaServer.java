package server;

import server.actions.Action;
import server.actions.DeleteAction;
import server.actions.PutAction;
import util.LoggerUtil;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Represents a replica server in a distributed key-value store.
 * This class handles PUT, GET, and DELETE operations and participates
 * in a Two-Phase Commit (2PC) protocol to ensure consistency across replicas.
 */
public class ReplicaServer implements IKeyValueServer {
  private static final Logger LOGGER = Logger.getLogger(ReplicaServer.class.getName());
  private final ConcurrentHashMap<String, Action> transactionStore = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, String> keyValueStore = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, ReentrantLock> keyLocks = new ConcurrentHashMap<>();
  private List<IKeyValueServer> replicas;
  private final ExecutorService executorService = Executors.newCachedThreadPool();
  private final String replicaId;

  static {
    LoggerUtil.setupCustomLogger(LOGGER);
  }

  /**
   * Constructs a ReplicaServer with a unique replica ID.
   *
   * @param replicaId The unique identifier for this replica.
   */
  public ReplicaServer(String replicaId) {
    this.replicaId = replicaId;
  }

  /**
   * Retrieves the replica ID.
   *
   * @return The unique identifier of this replica.
   */
  @Override
  public String getServerID() {
    return replicaId;
  }

  /**
   * Sets the list of replica servers in the distributed system.
   *
   * @param replicas The list of other replica servers.
   */
  public void setReplicas(List<ReplicaServer> replicas) {
    this.replicas = replicas.stream()
            .filter(replica -> replica != this)
            .collect(Collectors.toList());
  }

  /**
   * Creates or retrieves a lock for the given key.
   *
   * @param key The key for which a lock is needed.
   * @return A reentrant lock for the specified key.
   */
  private ReentrantLock createLockForKey(String key) {
    return keyLocks.computeIfAbsent(key, k -> new ReentrantLock());
  }

  /**
   * Retrieves the value associated with a given key.
   *
   * @param key The key to retrieve.
   * @return The value associated with the key, or null if not found.
   * @throws RemoteException If an RMI communication error occurs.
   */
  @Override
  public String get(String key) throws RemoteException {
    if (keyValueStore.containsKey(key)) {
      String value = keyValueStore.get(key);
      LOGGER.info(String.format("[Replica %s] GET SUCCESS for key %s : value %s", replicaId, key, value));
      return keyValueStore.get(key);
    }
    LOGGER.info(String.format("[Replica %s] GET FAILED key %s not found", replicaId, key));
    return null;
  }

  /**
   * Performs a PUT operation using the Two-Phase Commit protocol.
   *
   * @param key   The key to store.
   * @param value The value associated with the key.
   * @return True if the PUT operation was successful, false otherwise.
   * @throws RemoteException If an RMI communication error occurs.
   */
  @Override
  public boolean put(String key, String value) throws RemoteException {
    String transactionId = UUID.randomUUID().toString();
    LOGGER.info(String.format("%n [Replica %s] Initiating 2 phase commit for PUT %s %s with transactionID %s", replicaId, key, value, transactionId));
    boolean response = twoPhaseCommit(UUID.randomUUID().toString(), new PutAction(key, value)).join();
    LOGGER.info(String.format("[Replica %s] 2 phase commit ended for transactionID %s %n", replicaId, transactionId));
    return response;
  }

  /**
   * Performs a DELETE operation using the Two-Phase Commit protocol.
   *
   * @param key The key to delete.
   * @return True if the DELETE operation was successful, false otherwise.
   * @throws RemoteException If an RMI communication error occurs.
   */
  @Override
  public boolean delete(String key) throws RemoteException {
    String transactionId = UUID.randomUUID().toString();
    LOGGER.info(String.format("%n [Replica %s] Initiating 2 phase commit for DELETE %s with transactionID %s", replicaId, key, transactionId));
    boolean response = twoPhaseCommit(UUID.randomUUID().toString(), new DeleteAction(key)).join();
    LOGGER.info(String.format("[Replica %s] 2 phase commit ended for transactionID %s %n", replicaId, transactionId));
    return response;
  }

  /**
   * Prepares a transaction for commit.
   *
   * @param transactionId The unique transaction identifier.
   * @param action        The action to be executed (PUT or DELETE).
   * @return A CompletableFuture resolving to true if prepared successfully, false otherwise.
   */
  @Override
  public CompletableFuture<Boolean> prepareForCommit(String transactionId, Action action) {
    return CompletableFuture.supplyAsync(() -> {
      ReentrantLock lock = createLockForKey(action.getKey());
      if (!lock.tryLock()) {
        LOGGER.warning(String.format("[Replica %s] Prepare phase FAILED: Lock already acquired for key %s", replicaId, action.getKey()));
        return false; // Another transaction is modifying this key
      }
      transactionStore.put(transactionId, action);
      LOGGER.info(String.format("[Replica %s] Prepare phase SUCCESS for transaction %s for action %s", replicaId, transactionId, action));
      return true; // Prepared successfully
    }, executorService);
  }

  /**
   * Commits a transaction.
   *
   * @param transactionId The unique transaction identifier.
   * @return A CompletableFuture resolving to true if committed successfully, false otherwise.
   */
  @Override
  public CompletableFuture<Boolean> commit(String transactionId) {
    return CompletableFuture.supplyAsync(() -> {
      Action action = transactionStore.remove(transactionId);
      if (action == null) {
        return false;
      }
      try {
        action.execute(keyValueStore);
        LOGGER.info(String.format("[Replica %s] Commit phase SUCCESS for transaction %s for action %s", replicaId, transactionId, action));
        return true;
      } finally {
        keyLocks.remove(action.getKey());
      }
    }, executorService);
  }

  /**
   * Aborts a transaction.
   *
   * @param transactionId The unique transaction identifier.
   * @return A CompletableFuture resolving to true if aborted successfully, false otherwise.
   */
  @Override
  public CompletableFuture<Boolean> abort(String transactionId) {
    return CompletableFuture.supplyAsync(() -> {
      transactionStore.remove(transactionId);
      return true;
    }, executorService);
  }

  /**
   * Executes a Two-Phase Commit (2PC) protocol for an action.
   *
   * @param transactionId The unique transaction identifier.
   * @param action        The action to be executed.
   * @return A CompletableFuture resolving to true if the 2PC completes successfully, false otherwise.
   */
  private CompletableFuture<Boolean> twoPhaseCommit(String transactionId, Action action) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        boolean selfPrepared = prepareForCommit(transactionId, action).join();
        if (!selfPrepared) {
          LOGGER.warning(String.format("[Replica %s] Self-prepare FAILED for transaction %s %s.", replicaId, transactionId, action));
          return false;
        }
        LOGGER.info(String.format("[Replica %s] Self-prepare SUCCESS for transaction %s: %s.", replicaId, transactionId, action));

        if (replicas == null || replicas.isEmpty()) {
          LOGGER.severe(String.format("[Replica %s] No replicas available for 2PC. for action %s", replicaId, action));
          return false;
        }

        // Phase 1: Prepare Requests
        List<CompletableFuture<Boolean>> prepareFutures = replicas.stream()
                .map(replica -> {
                  try {
                    return replica.prepareForCommit(transactionId, action)
                            .orTimeout(2, TimeUnit.SECONDS)
                            .exceptionally(ex -> {
                              LOGGER.warning(String.format("[Replica %s] Prepare phase FAILED for replica: %s for action: %s", replicaId, action, ex.getMessage()));
                              return false;
                            });
                  } catch (Exception e) {
                    LOGGER.severe(String.format("[Replica %s] Exception in prepare phase: %s for transaction %s action %s",
                            replicaId, e.getMessage(), transactionId, action));
                    return CompletableFuture.completedFuture(false);
                  }
                })
                .collect(Collectors.toList());

        boolean allPrepared = prepareFutures.stream().allMatch(CompletableFuture::join);
        if (!allPrepared) {
          LOGGER.warning(String.format("[Replica %s] 2PC FAILED: Some replicas did not acknowledge prepare phase. for action %s", replicaId, action));
          abortTransaction(transactionId);
          return false;
        }

        // Phase 2: Commit Requests
        boolean selfCommit = commit(transactionId).join();
        if (!selfCommit) {
          LOGGER.warning(String.format("[Replica %s] Self-Commit FAILED for transaction %s %s.", replicaId, transactionId, action));
          return false;
        }
        LOGGER.info(String.format("[Replica %s] Self-Commit SUCCESS for transaction %s %s.", replicaId, transactionId, action));
        List<CompletableFuture<Boolean>> commitFutures = replicas.stream()
                .map(replica -> {
                  try {
                    return replica.commit(transactionId)
                            .orTimeout(2, TimeUnit.SECONDS)
                            .exceptionally(ex -> {
                              LOGGER.warning(String.format("[Replica %s] Commit phase FAILED for replica: %s %s", replicaId, ex.getMessage(), action));
                              return false;
                            });
                  } catch (Exception e) {
                    LOGGER.severe(String.format("[Replica %s] Exception in commit phase: %s %s", replicaId, e.getMessage(), action));
                    return CompletableFuture.completedFuture(false);
                  }
                })
                .collect(Collectors.toList());

        boolean allCommitted = commitFutures.stream().allMatch(CompletableFuture::join);
        if (!allCommitted) {
          LOGGER.warning(String.format("[Replica %s] 2PC FAILED: Some replicas did not acknowledge commit phase for action %s.", replicaId, action));
          return false;
        }
        return true;
      } finally {
        keyLocks.remove(action.getKey());
      }
    }, executorService);
  }

  /**
   * Aborts a transaction across all replicas.
   *
   * @param transactionId The unique transaction identifier.
   */
  private void abortTransaction(String transactionId) {
    if (replicas == null || replicas.isEmpty()) {
      LOGGER.warning(String.format("[Replica %s] No replicas available to abort transaction %s.", replicaId, transactionId));
      return;
    }
    replicas.forEach(replica -> {
      try {
        replica.abort(transactionId)
                .orTimeout(2, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                  LOGGER.warning(String.format("[Replica %s] Abort FAILED for replica: %s %s", replicaId, ex.getMessage(), transactionId));
                  return false;
                });
      } catch (Exception e) {
        LOGGER.severe(String.format("[Replica %s] Exception in abort phase: %s %s", replicaId, e.getMessage(), transactionId));
      }
    });
  }
}
