package server.roles;

import server.messages.*;
import server.actions.Action;
import util.LoggerUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * The Acceptor role in Paxos. Acceptors respond to Prepare and Accept requests,
 * maintain per-key Paxos state, and simulate crash-recovery by periodically restarting.
 */
public class Acceptor extends Thread {
  private static final Logger LOGGER = Logger.getLogger(Acceptor.class.getName());

  static {
    LoggerUtil.setupCustomLogger(LOGGER);
  }

  private final int id;

  // Paxos state per key
  private final Map<String, Long> promisedProposals = new ConcurrentHashMap<>();
  private final Map<String, Long> acceptedProposals = new ConcurrentHashMap<>();
  private final Map<String, Action> acceptedActions = new ConcurrentHashMap<>();

  /**
   * Constructs an Acceptor with a unique ID.
   *
   * @param id The ID of this Acceptor (usually the replica/server ID).
   */
  public Acceptor(int id) {
    this.id = id;
    setName("Acceptor-" + id);
  }

  @Override
  public void run() {
    LOGGER.info("Acceptor " + id + " is now active.");
  }

  /**
   * Handles a Prepare request from a proposer.
   * Returns a Promise if the proposal number is valid.
   *
   * @param req The Prepare request.
   * @return A PromiseResponse or null if rejected.
   */
  public synchronized PromiseResponse receivePrepare(PrepareRequest req) {
    String key = req.getKey();
    long proposalNumber = req.getProposalNumber();

    long promised = promisedProposals.getOrDefault(key, -1L);
    if (proposalNumber > promised) {
      promisedProposals.put(key, proposalNumber);
      LOGGER.info("Acceptor " + id + " promised proposal " + proposalNumber + " for key [" + key + "]");
      return new PromiseResponse(proposalNumber,
              acceptedProposals.getOrDefault(key, -1L),
              acceptedActions.get(key));
    }
    return null;
  }

  /**
   * Handles an Accept request from a proposer.
   * Returns an AcceptResponse if the proposal is accepted.
   *
   * @param req The Accept request.
   * @return An AcceptResponse or null if rejected.
   */
  public synchronized AcceptResponse receiveAccept(AcceptRequest req) {
    String key = req.getAction().getKey();
    long proposalNumber = req.getProposalNumber();

    long promised = promisedProposals.getOrDefault(key, -1L);
    if (proposalNumber >= promised) {
      acceptedProposals.put(key, proposalNumber);
      acceptedActions.put(key, req.getAction());
      LOGGER.info("Acceptor " + id + " accepted proposal " + proposalNumber + " for key [" + key + "]");
      return new AcceptResponse(proposalNumber, req.getAction());
    }
    return null;
  }

  /**
   * Clear state for a key once a value is learned.
   * @param key key state to be cleared.
   */
  public synchronized void clearStateForKey(String key) {
    promisedProposals.remove(key);
    acceptedProposals.remove(key);
    acceptedActions.remove(key);
    LOGGER.info("Acceptor " + id + " cleared Paxos state for key: " + key);
  }
}
