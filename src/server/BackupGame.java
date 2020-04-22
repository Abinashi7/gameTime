package server;

import java.rmi.Remote;

public interface BackupGame extends Remote {

    void write(String line);

    Integer getDeckSize();

    void removeCard(int randomCard);

    String getCard(int randomCard);

    void registerClient(int id);

    void vote(int i);
}
