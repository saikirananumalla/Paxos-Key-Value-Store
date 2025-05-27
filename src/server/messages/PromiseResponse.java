package server.messages;

import server.actions.Action;

import java.io.Serializable;

/**
 * Represents a Promise response sent by an Acceptor in response to a Prepare request.
 * Contains information about the highest accepted proposal and the associated action (if any).
 */
public class PromiseResponse implements Serializable {
  private final long promisedProposalNumber;
  private final long previouslyAcceptedProposalNumber;
  private final Action previouslyAcceptedAction;

  /**
   * Constructs a PromiseResponse with the promised proposal number,
   * previously accepted proposal number, and its associated action (if any).
   *
   * @param promisedProposalNumber         The proposal number the acceptor promises not to accept lower than.
   * @param previouslyAcceptedProposalNumber The proposal number previously accepted by the acceptor (if any).
   * @param previouslyAcceptedAction       The action associated with the previously accepted proposal (if any).
   */
  public PromiseResponse(long promisedProposalNumber,
                         long previouslyAcceptedProposalNumber,
                         Action previouslyAcceptedAction) {
    this.promisedProposalNumber = promisedProposalNumber;
    this.previouslyAcceptedProposalNumber = previouslyAcceptedProposalNumber;
    this.previouslyAcceptedAction = previouslyAcceptedAction;
  }

  /**
   * Returns the promised proposal number.
   *
   * @return The promised proposal number.
   */
  public long getPromisedProposalNumber() {
    return promisedProposalNumber;
  }

  /**
   * Returns the previously accepted proposal number.
   *
   * @return The previously accepted proposal number.
   */
  public long getPreviouslyAcceptedProposalNumber() {
    return previouslyAcceptedProposalNumber;
  }

  /**
   * Returns the previously accepted action.
   *
   * @return The previously accepted action (may be null).
   */
  public Action getPreviouslyAcceptedAction() {
    return previouslyAcceptedAction;
  }

  @Override
  public String toString() {
    return "PromiseResponse{" +
            "promisedProposalNumber=" + promisedProposalNumber +
            ", previouslyAcceptedProposalNumber=" + previouslyAcceptedProposalNumber +
            ", previouslyAcceptedAction=" + previouslyAcceptedAction +
            '}';
  }
}