package server;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;

import util.ValidationUtil;

/**
 * Main class to start the RMI registry and register the Coordinator.
 */
public class Main {
  public static void main(String[] args) {
    int port = ValidationUtil.validateServerArgs(args);
    try {
      LocateRegistry.createRegistry(port);

      // Create replicas
      List<ReplicaServer> replicas = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        replicas.add(new ReplicaServer(String.valueOf(i+1)));
      }

      // Set replicas for each server
      for (ReplicaServer replica : replicas) {
        replica.setReplicas(new ArrayList<>(replicas));
      }

      // Create and register Load balancer
      LoadBalancer loadBalancer = new LoadBalancer(replicas);
      Naming.rebind("KeyValueService", loadBalancer);

      System.out.println("Load Balancer registered with RMI and ready.");
    } catch (RemoteException | java.net.MalformedURLException e) {
      System.out.println("Remote exception: " + e.getMessage());
      System.exit(1);
    }
  }
}
