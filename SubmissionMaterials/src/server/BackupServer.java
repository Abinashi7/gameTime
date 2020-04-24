package server;

import coordinator.CoordinateGame;
import coordinator.CoordinateGameTasks;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Backup server class. Spun up by the coordinator.
 * Keeps track of the game state and cards for reference by coordinator
 */
public class BackupServer {

    static CoordinateGame g = null;

    /**
     * Constructor that binds the server to an RMI Registry
     * @param id int Server ID
     */
    public BackupServer(int id)  {
        try {
            BackupGame c = new BackupGameTasks();
            Naming.rebind("rmi://localhost:1099/BackupServer" + id, c);
        } catch (Exception e) {
            System.out.println("Trouble: " + e);
        }
        startBackup();
    }

    /**
     * Binds back to the coordinator to prepare for backing up
     */
    private static void startBackup(){
        try {
            g = (CoordinateGame) Naming.lookup(
                    "rmi://localhost:1099/GameServer");
            System.out.println("Connected to game server");
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
