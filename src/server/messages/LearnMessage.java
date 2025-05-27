package server.messages;

import server.actions.Action;

import java.io.Serializable;

/**
 * Represents a Learn message sent by a Proposer to Learners in Paxos.
 * Contains the final chosen action and the associated proposal metadata.
 */
public class LearnMessage implements Serializable {
  private final long proposalNumber;
  private final int proposerId;
  private final Action action;

  /**
   * Constructs a LearnMessage with the final agreed-upon action.
   *
   * @param proposalNumber The proposal number associated with the accepted value.
   * @param proposerId     The ID of the proposer sending the learn message.
   * @param action         The action (PUT or DELETE) that has reached consensus.
   */
  public LearnMessage(long proposalNumber, int proposerId, Action action) {
    this.proposalNumber = proposalNumber;
    this.proposerId = proposerId;
    this.action = action;
  }

  /**
   * Returns the proposal number of the chosen value.
   *
   * @return The proposal number.
   */
  public long getProposalNumber() {
    return proposalNumber;
  }

  /**
   * Returns the ID of the proposer sending this learn message.
   *
   * @return The proposer ID.
   */
  public int getProposerId() {
    return proposerId;
  }

  /**
   * Returns the final chosen action.
   *
   * @return The action to apply in the key-value store.
   */
  public Action getAction() {
    return action;
  }

  @Override
  public String toString() {
    return "LearnMessage{" +
            "proposalNumber=" + proposalNumber +
            ", proposerId=" + proposerId +
            ", action=" + action +
            '}';
  }
}
