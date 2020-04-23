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
import java.util.*;

public class CoordinateGameTasks extends UnicastRemoteObject implements CoordinateGame {

    private static LinkedList<BackupGame> allServers = new LinkedList<>();
    public static LinkedList<ClientImpl> allClients = new LinkedList<>();
    private static Integer totalResponses = 0;
    private static HashMap<Integer, String> responseList = new HashMap<>();
    private static HashMap<Integer, Integer> scores = new HashMap<>();   //id, score
    private static String currentCard = null;
    private static Integer winCondition = 3; //change wincondition here
    private static Integer minPlayers = 2; // change minimum players to start game here
    private static Integer playerCount = 0; // count of players at start, used for failure checking
    private static Integer numVotes = 0;
    private static int roundNumber = 0;

    /**
     *  Rule Sets:
     * 1- Standard: 5 players, 30 seconds to send response.
     *              Client gets list of responses, accepts response for best one
     *              Coordinator collects voting responses and increments the players score by 1, and announces scores
     *              if  score==10 there is a winner and game is over
     *
     *
     * 2- Duel Mode: Tournament of 1v1s with all players voting, 30 sec to send response, lose a duel and you are out
     */

    /**
     * Rule Set Functions - Ignore this is a potential improvement but not high in priority
     */
    private static int ruleset = 0;
    public String getRuleSetString(int rules) {
        if (rules==1) {
            return "Standard Rule Set";
        } else if (rules==2) {
            return "Duel Rule Set";
        } else {
            return "Rules Not Set";
        }
    }
    public int getRuleSet() {
        return ruleset;
    }
    public void setRuleSet(int rules){
        if (getRuleSet()==0) {
            System.out.println("Setting rule set to " + getRuleSetString(rules));
            ruleset=rules;
        } else {
            System.out.println("Changing rule set from " + getRuleSetString(getRuleSet()) + " to " + getRuleSetString(rules));
            ruleset=rules;
        }
    }



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
        if (allClients.size() < minPlayers) {
            try {
                ClientImpl client = (ClientImpl) Naming.lookup("rmi://localhost:1099/ClientSession" + id);
                allClients.add(client);
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
    //TODO handle cards with two blanks
    public void submitResponse(String response, int id) {
        String formattedResponse = currentCard.replaceFirst("_", response);
        responseList.put(id, formattedResponse);
        // checking if id registered in scoring
        if (!scores.containsKey(id)){
            scores.put(id, 0);
        }
        totalResponses++;
        System.out.println("Player " + id + ": " + formattedResponse);
        checkResponseSize();
    }

    /** register the vote - it should always match the id */
    public void vote(int i) throws RemoteException {
        for (int j = 0; j < allServers.size(); j++) {
            allServers.get(i).vote(i);
        }
        checkVoteTally();
    }

    /** waits to broadcast results until all votes are in */
    private void checkVoteTally() {
        if (numVotes == playerCount) {
            displayScore();
        }
    }

    /** checks for a winner and displays the score on server and every client */
    private void displayScore() {
        for( int key: scores.keySet()){
            System.out.println("Player " + key + ": " + scores.get(key) + " points");
        }
        for (int i = 0; i < allClients.size(); i++){
            try {
                allClients.get(i).printScore(scores);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        int isWinner = checkWinner();
        if (isWinner!=-1){
            for (int i = 0; i < allClients.size(); i++){
                try {
                    allClients.get(i).printWinner(isWinner);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            //when no more clients, kill coordinator
            while(allClients.size()>0){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Game is over! Shutting down coordinator..");
            //TODO: EXIT GRACEFULLY
        }
    }

    /** checks for a winner and displays the score, returns -1 if no winner
     * and returns the id of the winning player if a player is found
     */
    private int checkWinner() {
        for( int key: scores.keySet()){
            if (scores.get(key)>=winCondition){
                System.out.println();
                System.out.println("*******************************");
                System.out.println("Player " + key  + " is the Winner!!!!!");
                System.out.println("*******************************");
                System.out.println();
                return key;
            }
        }
        return -1;
    }

    /** when response size = number of players, continue */
    private void checkResponseSize() {
        if (totalResponses == playerCount) {
            broadcastResponses();
        }
    }

    /** broadcast the response list ot all players */
    private void broadcastResponses() {
        for (int i = 0; i < allClients.size(); i++) {
            try {
                allClients.get(i).displayResponses(responseList);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        getVotes();
    }
    /** collect votes from players */
    private void getVotes() {
        numVotes=0;
        for (int i = 0; i < allClients.size(); i++) {
            new VoteThread(i).start();
        }
        while(numVotes<allClients.size()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        displayScore();

        try {
            totalResponses=0;
            startGame();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /** only start game when minPlayers(5 as default) clients have registered */
    private static void checkGameSize() throws RemoteException{
        if (allClients.size() >= minPlayers){
            playerCount = allClients.size();
            System.out.println("Game full with " + playerCount + " players. Starting Round 1");
            startGame();
        }
    }

    /** start game by broadcasting start to clients */
    private static void startGame() throws RemoteException {
        roundNumber++;
        System.out.println("Starting round " + roundNumber);

        try {
            Thread.sleep(2000); //this is here to improve the flow of the game
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < allClients.size(); i++){
            allClients.get(i).startRound(roundNumber);
        }

        getNewCard();
        for (int i = 0; i < allClients.size(); i++) {
            new ResponseThread(i).start();
        }
    }

    static class VoteThread extends Thread {
        int clientId;

        public VoteThread(int id) {
            clientId = id;
        }

        @Override
        public void run() {
            try {
                int winner = CoordinateGameTasks.allClients.get(clientId).gatherVote(responseList);
                int score = scores.get(winner);
                score++;
                scores.put(winner,score);
                numVotes++;
            } catch (RemoteException e) {
                System.out.println("Remote Exception getting response");
            }
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

