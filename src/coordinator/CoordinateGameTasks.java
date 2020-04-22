package coordinator;

import client.Client;
import server.BackupGame;
import server.BackupGameTasks;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;

public class CoordinateGameTasks implements CoordinateGame {

    private static LinkedList<BackupGame> allServers = new LinkedList<>();
    private static LinkedList<Client> allClients = new LinkedList<>();
    private static Integer totalResponses = 0;
    private static HashMap<Integer, String> responseList = new HashMap<>();
    private static String currentCard = null;
    private static Integer winCondition = 10;
    private static Integer numVotes = 0;


    /** register a new server with the coordinator */
    public void registerServer(int id) {
        try {
            BackupGame s = (BackupGameTasks) Naming.lookup("rmi://localhost/BackupServer" + id);
            allServers.add(s);
        } catch (MalformedURLException | NotBoundException | RemoteException e) {
            e.printStackTrace();
        }
    }

    /** register a new client with the coordinator. accepts up to 5 clients
     * for the game, then denies further participants */
    public Boolean registerClient(int id) {
        if (allClients.size() < 5) {
            //TODO:
            // register client, successful
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
        try (BufferedReader br = new BufferedReader(new FileReader("questions.txt"))) {
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
    public void getCard() {
        int deckSize = allServers.getFirst().getDeckSize();
        int randomCard = (int) Math.floor(Math.random() * Math.floor(deckSize));
        currentCard = allServers.getFirst().getCard(randomCard);

        for (int i = 0; i < allServers.size(); i++) {
            allServers.get(i).removeCard(randomCard);
        }
        broadcastCard(currentCard);
    }

    /** broadcast the selected card to all clients */
    public void broadcastCard(String card){
        for (int i = 0; i < allClients.size(); i++) {
            //TODO:
            // broadcast card to all clients
        }
    }

    /** collect the submitted responses from the players */
    public void submitResponse(String response, int id) {
        String formattedResponse = currentCard.replaceFirst("_", response);
        responseList.put(id, formattedResponse);
        totalResponses++;
        checkResponseSize();
    }

    /** register the vote - i should always match the id */
    public void vote(int i) {
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
    private static void checkGameSize(){
        if (allClients.size() == 5){
            startGame();
        }
    }

    /** start game by broadcasting start to clients */
    private static void startGame(){
        for (int i = 0; i < allClients.size(); i++){
            //TODO:
            // broadcast to all clients that game is starting
            // set game to start in clientDriver
        }
    }
}
