package server;

import remote.KeyValueService;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import util.LoggerUtil;
import util.ValidationUtil;

/**
 * Starts 5 Paxos-based replica servers and registers a LoadBalancer with RMI.
 * Avoids static lists of Acceptors/Learners, as those are managed via supervisors.
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    static {
        LoggerUtil.setupCustomLogger(LOGGER);
    }

    public static void main(String[] args) {
        int port = ValidationUtil.validateServerArgs(args);
        try {
            int replicaCount = 5;

            List<IKeyValueServer> allReplicas = new ArrayList<>();

            LOGGER.info("Initializing Paxos replicas...");

            // Step 1: Initialize all replicas
            for (int i = 0; i < replicaCount; i++) {
                ReplicaServer replica = new ReplicaServer(i);
                allReplicas.add(replica);
            }

            // Step 2: set all replicas list in each replica
            for (int i = 0; i < replicaCount; i++) {
               allReplicas.get(i).setAllReplicas(allReplicas);
            }


            LOGGER.info("Starting LoadBalancer with round-robin strategy...");

            KeyValueService loadBalancer = new LoadBalancer(allReplicas);

            LocateRegistry.createRegistry(port);
            Naming.rebind("KeyValueService", loadBalancer);

            LOGGER.info("Load Balancer registered with RMI and ready.");
            LOGGER.info("System is UP! Port: " + port);
        } catch (Exception e) {
            LOGGER.severe("Error starting Paxos system: " + e.getMessage());
        }
    }
}
