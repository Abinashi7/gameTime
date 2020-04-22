package coordinator;

import server.BackupServer;

import java.io.IOException;
import java.rmi.Naming;


/**
 * This class carries the logic for a coordinator. It appends and creates the registery for all the participant servers
 * and bind it to the map Interface that connects a client to all the services available.
 */
public class Coordinator extends Thread {

    private static CoordinateGame c = null;

    public Coordinator() {
        try {
            c = new CoordinateGameTasks();
            Naming.rebind("rmi://localhost:1099/GameServer", c);
        } catch (Exception e) {
            System.out.println("Trouble: " + e);
        }
    }

    public static void main(String args[]) throws IOException {
        new Coordinator();

        /** Make 5 replica servers for the Coordinator to store the game database & scores on. */
        for (int i = 0; i < 5; i++) {
            new BackupServer(i);
            c.registerServer(i);

            // thread per participant
            Thread serverThread = new Thread();
            serverThread.start();
        }

        c.populate();
    }
}
