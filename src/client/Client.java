package client;

import coordinator.CoordinateGame;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Random;

public class Client {
    static CoordinateGame coord = null;
    ClientImpl c = null;
    int id;

    public Client(int id) {
        try {
            ClientImpl c = new ClientDriver();
            Naming.rebind("rmi://localhost:1099/ClientSession" + id, c);
            c.setId(id);
            c.connectToCoord();
        } catch (RemoteException e) {
            System.out.println("Trouble binding client: " + e.getMessage());
        } catch (MalformedURLException e) {
            System.out.println("MalformedURL while binding: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Client(new Random().nextInt(1000));
        new Client(new Random().nextInt(1000));
        new Client(new Random().nextInt(1000));
        new Client(new Random().nextInt(1000));
        new Client(new Random().nextInt(1000));
    }
}