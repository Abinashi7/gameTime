package coordinator;

import java.rmi.Remote;

public interface CoordinateGame extends Remote {

    void registerServer(int i);

    Boolean registerClient(int i);

    void populate();

    void getCard();

    void submitResponse(String response, int id);

    void vote(int i);

}
