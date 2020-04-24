package client;

import coordinator.CoordinateGame;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Time;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

// responsible to make calls to the coordinator that will connect the player with a server

/**
 * RMI client logic that makes calls to the coordinator and handles client side
 * game execution
 */
public class ClientDriver extends UnicastRemoteObject implements ClientImpl {

    static CoordinateGame coord;
    private Integer clientID;
    private String response;

    /** RMI constructor */
    protected ClientDriver() throws RemoteException {
    }

    /**
     * Binds to the Coordinator/game server's RMI registry then
     * asks the coordinator to bind back by calling its registerClient method
     */
    public void connectToCoord() {
        try {  // First bind to the coordinator
            coord = (CoordinateGame) Naming.lookup(
                    "rmi://localhost:1099/GameServer");
            System.out.println("Connected to game server!");
        } catch (MalformedURLException m) {
            System.out.println("MalformedURLException: " + m);
        } catch (RemoteException r) {
            System.out.println("RemoteException: Game server not running. Client shut down.");
            System.exit(0);
        } catch (NotBoundException n) {
            System.out.println("Unable to bind to game server: " + n);
        }

        try {
            // Then ask the coordinator to register the client.
            int result = coord.registerClient(clientID);
            if (result == 0) {
                System.out.println("Registration successful. Waiting for game start...");
            } else if (result == 1) {
                System.out.println("Registration successful. Game is full and has started.");
            } else {
                System.out.println("Server full. Please wait for next game.");
                System.exit(0);
            }
        } catch (RemoteException e) {
            System.out.println("Remote Exception: " + e.getMessage());
        }
    }

    /**
     * Prints the current scores for all the players
     * @param scores Hashmap<Id, score> passed from coordinator
     * @throws RemoteException if RMI communication fails
     */
    public void printScore( HashMap<Integer, Integer> scores) throws RemoteException{
        System.out.println();
        System.out.println("________SCORES________");
        for( int key: scores.keySet()){
            if (key==clientID) {
                System.out.println("YOU have " + scores.get(key) + " points");
            } else {
                System.out.println("Player " + key + ": " + scores.get(key) + " points");
            }
        }
        System.out.println("______________________");
        System.out.println();
    }

    /**
     * Prints the winning client Id number with a message
     * @param key int client ID
     * @throws RemoteException if RMI communication fails
     */
    public void printWinner(int key) throws RemoteException {
        System.out.println();
        System.out.println("*******************************");
        System.out.println("Player " + key  + " is the Winner!!!!!");
        System.out.println("*******************************");
        System.out.println();
        System.out.println("Game is over! Shutting down client..");

        // Unbind the RMI registry and remove the remote object
        try {
            Naming.unbind("rmi://localhost:1099/GameServer");
            UnicastRemoteObject.unexportObject(this, true);
        } catch (Exception e) {
            System.out.println("Closing...");
        }

        // Thread to wait and allow other clients to close
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Sleep interrupted");
            }
            System.exit(0);
        }).start();
    }

    /**
     * Displays all responses for the round for the player to vote on
     * @param responses Hashmap of all recorded responses for the round
     */
    public void displayResponses(HashMap<Integer, String>  responses){
        // the returned list of all player responses from the coordinator
        // print to client with some sort of label formatting (1: response)
        int responseCount = 0;
        for (int key : responses.keySet()){
            responseCount++;
            System.out.println(responseCount + ":  " + responses.get(key));
        }
    }

    /**
     * Asks the client to vote on the best response. Takes keyboard input for the number of the vote
     * and prints the voted for message
     * @param responses Hashmap of all recorded reponses for the round
     * @return
     */
    public int gatherVote( HashMap<Integer, String> responses) {
        System.out.println("Type in the number next to the response you think is the best!");
        Scanner keyboard = new Scanner(System.in);
        String response =  keyboard.nextLine();
        int responseNumber = -1;
        try {
            responseNumber = Integer.parseInt(response);
        } catch (NumberFormatException e) {
            System.out.println("Invalid vote. Vote not counted");
        }
        int responseCounter = 0;
        int winnerKey = -1;
        String word = "";
        for (int key : responses.keySet()) {
            responseCounter++;
            if (responseCounter==responseNumber){
                word = responses.get(key);
                winnerKey = key;
                break;
            }
        }
        System.out.println("You chose player " + winnerKey + "'s response: " + word);
        return winnerKey;
    }

    /**
     * Saves client ID
     * @param id int client ID
     */
    public void setId(int id) {
        clientID = id;
    }

    /**
     * Prints the card on the next line
     * @param card String game card
     */
    public void displayCard(String card) {
        System.out.println(card);
    }

    /**
     * Takes keyboard input for a response to the displayed card
     * @throws RemoteException if RMI communication fails
     */
    public void getResponse() throws RemoteException {
        response = "???";
        final BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
         class Task implements Callable<String> {
             @Override
             public String call() throws Exception {
                 System.out.print("15 seconds to answer. Response: ");
                 try {
                     while (!keyboard.ready()) {
                         Thread.sleep(100);
                     }
                     response = keyboard.readLine();
                 } catch (InterruptedException e) {
                     System.out.println("Interrupted while sleeping");
                     return "...";
                 }
                 return response;
             }
         }

         Task task = new Task();
        try {

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> result = executor.submit(task);
            result.get(15, TimeUnit.SECONDS);
            executor.shutdown();
        } catch (Exception e) {
            System.out.println("Exception caught " + e.getMessage());
            if (response.equals("???")) {
                coord.submitResponse(response, clientID);
            }
        }
        if (!response.equals("???")) {
            coord.submitResponse(response, clientID);
        }
    }

    /**
     * Prints the round number at the beginning of each round
     * @param round int round number
     */
    public void startRound(int round) {
        System.out.println("Starting round " + round);

    }

}
