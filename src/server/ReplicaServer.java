package server;

import server.actions.PutAction;
import server.actions.DeleteAction;
import server.roles.*;

import util.LoggerUtil;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Represents a replica node in the Paxos-based distributed key-value store.
 * Manages its own acceptor, proposer, and learner components, and handles client requests.
 */
public class ReplicaServer implements IKeyValueServer {
    private static final Logger LOGGER = Logger.getLogger(ReplicaServer.class.getName());

    static {
        LoggerUtil.setupCustomLogger(LOGGER);
    }

    private final int serverId;
    private final AcceptorSupervisor acceptorSupervisor;
    private final Learner learner;
    private final Proposer proposer;
    private final Map<String, String> keyValueStore;

    /**
     * Constructs a new ReplicaServer with the given ID.
     *
     * @param serverId Unique ID of this replica.
     */
    public ReplicaServer(int serverId) throws RemoteException {
        this.serverId = serverId;
        this.acceptorSupervisor = new AcceptorSupervisor(serverId);
        this.keyValueStore = new ConcurrentHashMap<>();
        this.learner = new Learner(serverId, keyValueStore, acceptorSupervisor);
        this.proposer = new Proposer(serverId);

        this.learner.start();
        this.proposer.start();

        LOGGER.info("ReplicaServer " + serverId + " Initialized.");
    }

    /**
     * Retrieves the value for a given key from this replica’s local key-value store.
     */
    @Override
    public String get(String key) {
        LOGGER.info("[Replica " + serverId + "] GET request for key: " + key);
        if (key == null) {
            LOGGER.warning("[Replica " + serverId + "] GET failed: key is null.");
            return null;
        }

        String value = keyValueStore.get(key);
        LOGGER.info("[Replica " + serverId + "] GET response for key [" + key + "] → " + value);
        return value;
    }

    /**
     * Handles a PUT request by proposing a value using Paxos.
     */
    @Override
    public boolean put(String key, String value) {
        LOGGER.info("[Replica " + serverId + "] PUT request: [" + key + "] = " + value);
        boolean result = proposer.propose(new PutAction(key, value));
        LOGGER.info("[Replica " + serverId + "] PUT result: " + result);
        return result;
    }

    /**
     * Handles a DELETE request by proposing a deletion using Paxos.
     */
    @Override
    public boolean delete(String key) {
        LOGGER.info("[Replica " + serverId + "] DELETE request for key: " + key);
        boolean result = proposer.propose(new DeleteAction(key));
        LOGGER.info("[Replica " + serverId + "] DELETE result: " + result);
        return result;
    }

    @Override
    public AcceptorSupervisor getAcceptorSupervisor() {
        return acceptorSupervisor;
    }

    @Override
    public Learner getLearner() {
        return learner;
    }

    @Override
    public String getServerID() {
        return String.valueOf(serverId);
    }

    @Override
    public void setAllReplicas(List<IKeyValueServer> replicas) {
      this.proposer.setAllReplicas(replicas);
        LOGGER.info("[Replica " + serverId + "] wired with " + replicas.size() + " peer replicas.");
    }
}
