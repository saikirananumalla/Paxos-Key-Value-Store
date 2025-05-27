package server.messages;

import server.actions.Action;

import java.io.Serializable;

/**
 * Represents an Accept response message sent by an Acceptor to a Proposer in Paxos.
 * Acknowledges acceptance of a proposal number and its associated action.
 */
public class AcceptResponse implements Serializable {
  private final long proposalNumber;
  private final Action action;

  /**
   * Constructs an AcceptResponse with the accepted proposal number and action.
   *
   * @param proposalNumber The proposal number that was accepted.
   * @param action         The action that was accepted (PUT or DELETE).
   */
  public AcceptResponse(long proposalNumber, Action action) {
    this.proposalNumber = proposalNumber;
    this.action = action;
  }

  /**
   * Returns the accepted proposal number.
   *
   * @return The proposal number.
   */
  public long getProposalNumber() {
    return proposalNumber;
  }

  /**
   * Returns the accepted action.
   *
   * @return The action associated with the proposal.
   */
  public Action getAction() {
    return action;
  }

  @Override
  public String toString() {
    return "AcceptResponse{" +
            "proposalNumber=" + proposalNumber +
            ", action=" + action +
            '}';
  }
}
