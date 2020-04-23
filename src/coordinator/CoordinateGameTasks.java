package coordinator;

import client.ClientImpl;
import server.BackupGame;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CoordinateGameTasks extends UnicastRemoteObject implements CoordinateGame {

    private static LinkedList<BackupGame> allServers = new LinkedList<>();
    public static LinkedList<ClientImpl> allClients = new LinkedList<>();
//    private static List<ClientThread> clientThreads = new ArrayList<>(5);
    private static Integer totalResponses = 0;
    private static HashMap<Integer, String> responseList = new HashMap<>();
    private static String currentCard = null;
    private static Integer winCondition = 10;
    private static Integer numVotes = 0;
    private static int roundNumber = 0;

    protected CoordinateGameTasks() throws RemoteException {
    }

    /** register a new server with the coordinator */
    public void registerServer(int id) {
        try {
            BackupGame s = (BackupGame) Naming.lookup("rmi://localhost:1099/BackupServer" + id);
            allServers.add(s);
        } catch (MalformedURLException | NotBoundException | RemoteException e) {
            e.printStackTrace();
        }
    }

    /** register a new client with the coordinator. accepts up to 5 clients
     * for the game, then denies further participants */
    public Boolean registerClient(int id) throws RemoteException{
        if (allClients.size() < 5) {
            try {
                ClientImpl client = (ClientImpl) Naming.lookup("rmi://localhost:1099/ClientSession" + id);
//                ClientThread thread = new ClientThread(this, id);
                allClients.add(client);
//                clientThreads.add(thread);
//                thread.start();
                System.out.println("Successfully bound back to client #" + id);
            } catch (NotBoundException e) {
                System.out.println("Nothing bound to id: " + id + ". Message " + e.getMessage());
            } catch (RemoteException e) {
                System.out.println("Remote exception while binding back to client" + e.getMessage());
            } catch (MalformedURLException e) {
                System.out.println("MalformedURL binding back to client" + e.getMessage());
            }

            checkGameSize();
            responseList.put(id, null);
            for (int i = 0; i < allServers.size(); i++) {
                allServers.get(i).registerClient(id);
            }
            return true;
        }
        // else fails
        return false;
    }

    /** populate each backup server with the same list of cards */
    public void populate() {
        try (BufferedReader br = new BufferedReader(new FileReader("./src/coordinator/questions.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                for (int i = 0; i < allServers.size(); i++) {
                    allServers.get(i).write(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** get a random card - remove it from all servers */
    public static void getNewCard() throws RemoteException {
        int deckSize = allServers.getFirst().getDeckSize();
        int randomCard = (int) Math.floor(Math.random() * Math.floor(deckSize));
        currentCard = allServers.getFirst().getCard(randomCard);

        for (int i = 0; i < allServers.size(); i++) {
            allServers.get(i).removeCard(randomCard);
        }
        broadcastCard(currentCard);
    }

    /** broadcast the selected card to all clients */
    private static void broadcastCard(String card) throws RemoteException{
        for (int i = 0; i < allClients.size(); i++) {
            allClients.get(i).displayCard(card);
        }
    }

    /** collect the submitted responses from the players */
    public void submitResponse(String response, int id) {
        String formattedResponse = currentCard.replaceFirst("_", response);
        responseList.put(id, formattedResponse);
        totalResponses++;
        System.out.println(formattedResponse);
        checkResponseSize();
    }

    /** register the vote - i should always match the id */
    public void vote(int i) throws RemoteException {
        for (int j = 0; j < allServers.size(); j++) {
            allServers.get(i).vote(i);
        }
        checkVoteTally();
    }

    /** waits to broadcast results until all votes are in */
    private void checkVoteTally() {
        if (numVotes == 5) {
            displayScore();
        }
    }

    /** checks for a winner and displays the score */
    private void displayScore() {
        //TODO:
        // broadcast score for each player and check winner
        checkWinner();
    }

    /** checks for a winner and displays the score */
    private void checkWinner() {
        //TODO: 
    }

    /** when response size = number of players, continue */
    private void checkResponseSize() {
        if (totalResponses == 5) {
            broadcastResponses();
        }
    }

    /** broadcast the response list ot all players */
    private void broadcastResponses() {
        for (int i = 0; i < allClients.size(); i++) {
            //TODO:
            // broadcast responses back to all clients
        }
    }

    /** only start game when 5 clients have registered */
    private static void checkGameSize() throws RemoteException{
        if (allClients.size() == 5){
            System.out.println("Game full. Starting Round 1");
            startGame();
        }
    }

    /** start game by broadcasting start to clients */
    private static void startGame() throws RemoteException {
        roundNumber++;
        for (int i = 0; i < allClients.size(); i++){
            allClients.get(i).startRound(roundNumber);
        }
        getNewCard();
        for (int i = 0; i < allClients.size(); i++) {
            ResponseThread thread = new ResponseThread(i);
            thread.start();
        }
    }
}

class ResponseThread extends Thread {
    int clientId;

    public ResponseThread(int id) {
        clientId = id;
    }

    @Override
    public void run() {
        try {
            CoordinateGameTasks.allClients.get(clientId).getResponse();
        } catch (RemoteException e) {
            System.out.println("Remote Exception getting response");
        }
    }

}
