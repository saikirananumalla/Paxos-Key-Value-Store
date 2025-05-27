package server.roles;

import server.IKeyValueServer;
import server.actions.Action;
import server.messages.*;
import util.ProposalNumberGenerator;
import util.LoggerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * The Proposer role in Paxos. It initiates consensus by communicating with active Acceptors.
 */
public class Proposer extends Thread {
  private static final Logger LOGGER = Logger.getLogger(Proposer.class.getName());

  static {
    LoggerUtil.setupCustomLogger(LOGGER);
  }

  private final int id;
  private final ProposalNumberGenerator proposalNumberGenerator;
  private List<IKeyValueServer> allReplicas;

  private static final int MAJORITY = 3; // For 5 replicas

  public Proposer(int id) {
    this.id = id;
    this.proposalNumberGenerator = new ProposalNumberGenerator(id);
    setName("Proposer-" + id);
  }

  /**
   * Set all replicas for our proposer to call out to all acceptors
   * @param allReplicas Replica Servers.
   */
  public void setAllReplicas(List<IKeyValueServer> allReplicas) {
    this.allReplicas = allReplicas;
  }

  /**
   * Propose a new action to all acceptors
   * @param action PUT/DELETE of a certain key value pair
   * @return true or false
   */
  public boolean propose(Action action) {
    return propose(action, false);
  }

  /**
   * Propose a new action to all acceptors
   * @param action PUT/DELETE of a certain key value pair
   * @return true or false
   */
  public boolean propose(Action action, boolean hasRetried) {
    String key = action.getKey();
    long proposalNumber = proposalNumberGenerator.next();

    LOGGER.info("Proposer " + id + " proposing for key [" + key + "] with proposal #" + proposalNumber);

    PrepareRequest prepareRequest = new PrepareRequest(proposalNumber, key, id);
    List<PromiseResponse> promises = new ArrayList<>();

    // Phase 1: Prepare
    for (IKeyValueServer replica : allReplicas) {
      Acceptor acceptor = replica.getAcceptorSupervisor().getLiveAcceptor();
      if (acceptor == null) continue;

      PromiseResponse response = acceptor.receivePrepare(prepareRequest);
      if (response != null) {
        promises.add(response);
      }
    }

    if (promises.size() < MAJORITY) {
      LOGGER.warning("Not enough promises + " + promises.size() + "/" + 5 + ". Aborting proposal #" + proposalNumber);
      return false;
    }

    // Use highest previously accepted value if present - Piggybacking
    Action toPropose = action;
    long highestAccepted = -1;
    for (PromiseResponse p : promises) {
      if (p.getPreviouslyAcceptedAction() != null &&
              p.getPreviouslyAcceptedProposalNumber() > highestAccepted) {
        toPropose = p.getPreviouslyAcceptedAction();
        highestAccepted = p.getPreviouslyAcceptedProposalNumber();
      }
    }

    // Phase 2: Accept
    AcceptRequest acceptRequest = new AcceptRequest(proposalNumber, id, toPropose);
    int acceptedCount = 0;

    for (IKeyValueServer replica : allReplicas) {
      Acceptor acceptor = replica.getAcceptorSupervisor().getLiveAcceptor();
      if (acceptor == null) continue;

      AcceptResponse response = acceptor.receiveAccept(acceptRequest);
      if (response != null) {
        acceptedCount++;
      }
    }

    if (acceptedCount < MAJORITY) {
      LOGGER.warning("Accept phase failed " + acceptedCount + "/" + 5 + " acceptors. Proposal #" + proposalNumber + " was rejected.");
      if (!hasRetried) {
        LOGGER.info("Retrying proposal once Proposal #" + proposalNumber + " for key [" + key + "]");
        return propose(action, true);  // Retry only once
      }
    }

    LOGGER.info("Accept phase succeeded " + acceptedCount + "/" + 5 + ". Proposal #" + proposalNumber);
    // Phase 3: Notify all learners
    LearnMessage learnMessage = new LearnMessage(proposalNumber, id, toPropose);
    for (IKeyValueServer replica : allReplicas) {
      Learner learner = replica.getLearner();
      learner.learn(learnMessage);
    }

    LOGGER.info("Learn phase completed. Proposal #" + proposalNumber);

    if (!toPropose.equals(action)) {
      LOGGER.warning("Client's action was overridden. Chosen action: " + toPropose);
      return propose(action);
    }

    LOGGER.info("Consensus achieved for key [" + key + "] — Action applied: " + toPropose);
    return true;
  }

  @Override
  public void run() {
    LOGGER.info("Proposer " + id + " is running.");
  }
}
