package server.roles;

import server.messages.LearnMessage;
import server.actions.Action;
import util.LoggerUtil;

import java.util.Map;
import java.util.logging.Logger;

/**
 * The Learner role in Paxos. Learners apply agreed-upon actions to their local key-value store.
 * Ensures that each Paxos decision is applied exactly once per key and proposal number.
 */
public class Learner extends Thread {
  private static final Logger LOGGER = Logger.getLogger(Learner.class.getName());

  static {
    LoggerUtil.setupCustomLogger(LOGGER);
  }

  private final int id;
  private final Map<String, String> keyValueStore;
  private final AcceptorSupervisor acceptorSupervisor;

  /**
   * Constructs a Learner with a unique ID.
   *
   * @param id The ID of the learner (usually same as the replica ID).
   */
  public Learner(int id, Map<String, String> keyValueStore, AcceptorSupervisor acceptorSupervisor) {
    this.id = id;
    this.keyValueStore = keyValueStore;
    this.acceptorSupervisor = acceptorSupervisor;
    setName("Learner-" + id);
  }

  /**
   * Applies a learned action to the key-value store, if it hasn't been applied for this proposal number.
   *
   * @param message The LearnMessage containing the action and metadata.
   */
  public synchronized void learn(LearnMessage message) {
    Action action = message.getAction();
    action.execute(keyValueStore);
    if (acceptorSupervisor.getCurrentAcceptor() != null) {
      acceptorSupervisor.getCurrentAcceptor().clearStateForKey(action.getKey());
    }
    LOGGER.info("Learner " + id + " applied proposal " + message.getProposalNumber() + ": " + action);
  }

  @Override
  public void run() {
    LOGGER.info("Learner " + id + " is now running.");
  }
}
