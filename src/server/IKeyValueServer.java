package server;

import java.util.List;

import remote.KeyValueService;
import server.roles.AcceptorSupervisor;
import server.roles.Learner;

/**
 * Represents a key-value store server that supports distributed transactions
 * using the Two-Phase Commit (2PC) protocol.
 */
public interface IKeyValueServer extends KeyValueService {

  /**
   * Get the server id of the replica server.
   * @return the server id.
   */
  String getServerID();

  /**
   * Get the acceptor supervisor for the server.
   * @return AcceptorSupervisor
   */
  AcceptorSupervisor getAcceptorSupervisor();

  /**
   * Get the learner for the server.
   * @return Learner
   */
  Learner getLearner();

  /**
   * Sets the list of all other replicas in the cluster.
   */
  void setAllReplicas(List<IKeyValueServer> replicas);
}
