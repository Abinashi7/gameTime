package client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

/**
 * Remote Interface for ClientDriver
 */
public interface ClientImpl extends Remote {
    void setId(int id) throws RemoteException;

    void displayCard(String card) throws RemoteException;

    void getResponse() throws RemoteException;

    int gatherVote(HashMap<Integer, String> responses) throws RemoteException;

    void connectToCoord() throws RemoteException;

    void startRound(int round) throws RemoteException;

    void displayResponses( HashMap<Integer, String>  responses) throws RemoteException;

    void printScore(HashMap<Integer, Integer> scores) throws RemoteException;

    void printWinner(int key) throws RemoteException;
}
