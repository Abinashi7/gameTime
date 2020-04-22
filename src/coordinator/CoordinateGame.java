package coordinator;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CoordinateGame extends Remote {

    void registerServer(int i) throws RemoteException;

    Boolean registerClient(int i) throws RemoteException;

    void populate() throws RemoteException;

    void submitResponse(String response, int id) throws RemoteException;

    void vote(int i) throws RemoteException;

}
