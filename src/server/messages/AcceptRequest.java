package server.messages;

import server.actions.Action;

import java.io.Serializable;

/**
 * Represents an Accept request message sent by a Proposer to Acceptors in Paxos.
 * Contains the proposal number, proposer ID, and the action to be accepted.
 */
public class AcceptRequest implements Serializable {
  private final long proposalNumber;
  private final int proposerId;
  private final Action action;

  /**
   * Constructs an AcceptRequest with the specified proposal number, proposer ID, and action.
   *
   * @param proposalNumber The unique proposal number for this round.
   * @param proposerId     The ID of the proposer (replica server).
   * @param action         The action (PUT/DELETE) to be accepted by the acceptors.
   */
  public AcceptRequest(long proposalNumber, int proposerId, Action action) {
    this.proposalNumber = proposalNumber;
    this.proposerId = proposerId;
    this.action = action;
  }

  /**
   * Returns the proposal number for this accept request.
   *
   * @return The proposal number.
   */
  public long getProposalNumber() {
    return proposalNumber;
  }

  /**
   * Returns the ID of the proposer.
   *
   * @return The proposer (replica server) ID.
   */
  public int getProposerId() {
    return proposerId;
  }

  /**
   * Returns the action to be proposed.
   *
   * @return The action to accept (PUT/DELETE).
   */
  public Action getAction() {
    return action;
  }

  @Override
  public String toString() {
    return "AcceptRequest{" +
            "proposalNumber=" + proposalNumber +
            ", proposerId=" + proposerId +
            ", action=" + action +
            '}';
  }
}
