package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class BackupGameTasks extends UnicastRemoteObject implements BackupGame {

    private ArrayList<String> cards = new ArrayList<>();
    private HashMap<Integer, Integer> players = new HashMap<>();

    /** RMI Constructor */
    protected BackupGameTasks() throws RemoteException {
    }

    /** Writes card to server */
    public void write(String line) {
        cards.add(line);
    }

    /** Returns the size of the card deck */
    @Override
    public Integer getDeckSize() {
        return cards.size();
    }

    /** Removes a card from the deck after it is used */
    @Override
    public void removeCard(int randomCard) {
        cards.remove(randomCard);
    }

    /** Returns the card requested by the coordinator */
    @Override
    public String getCard(int randomCard) {
        return cards.get(randomCard);
    }

    /** Saves the client ID */
    @Override
    public void registerClient(int id) {
        players.put(id, 0);
    }

    /** Increments and saves the current score for the client */
    @Override
    public void vote(int i) {
        int currentScore = players.get(i);
        players.put(i, currentScore++);
    }
}
