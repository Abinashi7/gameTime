package client;

import coordinator.CoordinateGame;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {
    static CoordinateGame c = null;

    public static void main(String[] args) {

        try {
            c = (CoordinateGame) Naming.lookup(
                    "rmi://localhost/GameServer");
            System.out.println("Connected to game server!");
        } catch (MalformedURLException m) {
            System.out.println("MalformedURLException: " + m);
        } catch (RemoteException r) {
            System.out.println("RemoteException: Game server not running. Client shut down.");
            System.exit(0);
        } catch (NotBoundException n) {
            System.out.println("Unable to bind to game server: " + n);
        }

        /** placeholder to register individual clients with the coordinator **/
        if (c.registerClient(0)) {
            System.out.println("Registration successful. Waiting for game start...");
        }
        else System.out.println("Server full. Please wait for next game.");
        System.exit(0);
    }
}