package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class BackupGameTasks extends UnicastRemoteObject implements BackupGame {

    private ArrayList<String> cards = new ArrayList<>();
    private HashMap<Integer, Integer> players = new HashMap<>();

    protected BackupGameTasks() throws RemoteException {
    }

    public void write(String line) {
        cards.add(line);
    }

    @Override
    public Integer getDeckSize() {
        return cards.size();
    }

    @Override
    public void removeCard(int randomCard) {
        cards.remove(randomCard);
    }

    @Override
    public String getCard(int randomCard) {
        return cards.get(randomCard);
    }

    @Override
    public void registerClient(int id) {
        players.put(id, 0);
    }

    @Override
    public void vote(int i) {
        int currentScore = players.get(i);
        players.put(i, currentScore++);
    }
}
