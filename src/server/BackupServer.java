package server;

import coordinator.CoordinateGame;
import coordinator.CoordinateGameTasks;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class BackupServer {

    static CoordinateGame g = null;

    public BackupServer(int id)  {
        try {
            BackupGame c = new BackupGameTasks();
            Naming.rebind("rmi://localhost:1099/BackupServer" + id, c);
        } catch (Exception e) {
            System.out.println("Trouble: " + e);
        }
        startBackup();
    }

    private static void startBackup(){
        try {
            g = (CoordinateGameTasks) Naming.lookup(
                    "rmi://localhost/GameServer");
            System.out.println("Connected to game server");
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
