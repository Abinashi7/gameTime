package server;

import java.rmi.RemoteException;

public class Server implements gameInterface {
    private int curServer;
    private int[] participants = new int[4];

    @Override
    public void setCurrentServer(int[] peerPorts, int currentPort) throws RemoteException {
        this.curServer = currentPort;
        this.participants = peerPorts;
    }
}
