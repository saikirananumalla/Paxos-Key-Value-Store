package server.messages;

import java.io.Serializable;

/**
 * Represents a Prepare request message sent from a Proposer to Acceptors in Paxos.
 * Contains the proposal number, the key being proposed on, and the ID of the proposer.
 */
public class PrepareRequest implements Serializable {
  private final long proposalNumber;
  private final String key;
  private final int proposerId;

  /**
   * Constructs a PrepareRequest with the specified proposal number, key, and proposer ID.
   *
   * @param proposalNumber The unique proposal number for this Paxos round.
   * @param key            The key this proposal is attempting to coordinate consensus for.
   * @param proposerId     The ID of the proposer (replica server).
   */
  public PrepareRequest(long proposalNumber, String key, int proposerId) {
    this.proposalNumber = proposalNumber;
    this.key = key;
    this.proposerId = proposerId;
  }

  /**
   * Returns the proposal number of this request.
   *
   * @return The proposal number.
   */
  public long getProposalNumber() {
    return proposalNumber;
  }

  /**
   * Returns the key associated with this proposal.
   *
   * @return The key for this Paxos round.
   */
  public String getKey() {
    return key;
  }

  /**
   * Returns the ID of the proposer who initiated this request.
   *
   * @return The proposer (replica server) ID.
   */
  public int getProposerId() {
    return proposerId;
  }

  @Override
  public String toString() {
    return "PrepareRequest{" +
            "proposalNumber=" + proposalNumber +
            ", key='" + key + '\'' +
            ", proposerId=" + proposerId +
            '}';
  }
}
