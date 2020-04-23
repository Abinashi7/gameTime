package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BackupGame extends Remote {

    void write(String line) throws RemoteException;

    Integer getDeckSize() throws RemoteException;

    void removeCard(int randomCard) throws RemoteException;

    String getCard(int randomCard) throws RemoteException;

    void registerClient(int id) throws RemoteException;

    void vote(int i) throws RemoteException;
}
