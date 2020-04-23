package client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ClientImpl extends Remote {
    void setId(int id) throws RemoteException;

    void displayCard(String card) throws RemoteException;

    void getResponse() throws RemoteException;

    int gatherVote(String card, ArrayList<String> responses) throws RemoteException; // Display the options and get back the vote for the current round

    void connectToCoord() throws RemoteException;

    void startRound(int round) throws RemoteException;
}