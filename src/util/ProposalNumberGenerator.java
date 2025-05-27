package util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates unique and monotonically increasing Paxos proposal numbers.
 * Each proposer (replica server) gets its own instance of this generator.
 * The proposal number format combines a logical counter with the replica's unique ID
 * to ensure total ordering and proposer uniqueness.
 * Supports up to 16 replicas.
 */
public class ProposalNumberGenerator {
  private static final int ID_SHIFT = 4; // allows up to 16 replicas (0-15)
  private final int serverId;
  private final AtomicLong counter;

  /**
   * Constructs a ProposalNumberGenerator for the given replica/server ID.
   *
   * @param serverId The unique ID of the replica server acting as proposer.
   */
  public ProposalNumberGenerator(int serverId) {
    this.serverId = serverId;
    this.counter = new AtomicLong(System.currentTimeMillis());
  }

  /**
   * Returns the next unique and increasing proposal number.
   *
   * @return A Paxos-compliant proposal number that is unique and increasing.
   */
  public long next() {
    long logical = counter.incrementAndGet();
    return (logical << ID_SHIFT) | serverId;
  }
}
