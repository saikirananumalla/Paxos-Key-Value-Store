package server.roles;

import util.LoggerUtil;

import java.util.Random;
import java.util.logging.Logger;

/**
 * Supervises a single Acceptor instance. Simulates periodic failure and automatic recovery.
 */
public class AcceptorSupervisor {
  private static final Logger LOGGER = Logger.getLogger(AcceptorSupervisor.class.getName());

  static {
    LoggerUtil.setupCustomLogger(LOGGER);
  }

  private final int id;
  private final Random random = new Random();

  private Acceptor currentAcceptor;
  private int remainingChecks = 0;

  public AcceptorSupervisor(int id) {
    this.id = id;
    resetAcceptor();
  }

  /**
   * Returns a live acceptor if available.
   * If the acceptor has failed, returns null.
   * If cooldown has passed, restarts automatically.
   */
  public synchronized Acceptor getLiveAcceptor() {
    if (remainingChecks <= 0) {
      if (currentAcceptor != null) {
        LOGGER.warning("Acceptor " + id + " FAILED.");
        currentAcceptor = null;
      }
      resetAcceptor();
      return null;
    }

    remainingChecks--;
    LOGGER.info("Acceptor " + id + " responding (checks left: " + remainingChecks + ")");
    return currentAcceptor;
  }

  /**
   * Returns the current acceptor.
   * @return Acceptor
   */
  public synchronized Acceptor getCurrentAcceptor() {
    return currentAcceptor;
  }

  /**
   * Immediately resets the Acceptor with a new failure threshold.
   */
  public synchronized void resetAcceptor() {
    currentAcceptor = new Acceptor(id);
    remainingChecks = 1 + random.nextInt(6); // 1 to 6 checks before failure
    LOGGER.info("Acceptor " + id + " STARTED (new checks: " + remainingChecks + ")");
  }
}
