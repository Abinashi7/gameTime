package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface gameInterface extends Remote {

    void setCurrentServer(int[] peerPorts, int currentPort ) throws RemoteException;

}
