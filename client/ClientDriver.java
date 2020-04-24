package client;

import coordinator.CoordinateGame;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

// responsible to make calls to the coordinator that will connect the player with a server

/**
 * RMI client logic that makes calls to the coordinator and handles client side
 * game execution
 */
public class ClientDriver extends UnicastRemoteObject implements ClientImpl {

    static CoordinateGame coord;
    private Integer clientID;
    private String response="";
    TimerTask task;
    Timer timer;
    private String responseNum="";
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
//                System.out.println("Registration successful. Game is full and has started.");
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
    public void printWinner(int key) throws RemoteException{
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
        while (!isInteger(response)){
            System.out.println("Please enter a number only");
            response = keyboard.nextLine();
        }
        int responseNumber = Integer.parseInt(response);
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
     * Ensures the integer value when taking in a vote
     * @param s the vote from client
     * @return true if the vote is an integer, false otherwise
     */
    public static boolean isInteger(String s) {
        boolean isValidInteger = false;
        try
        {
            Integer.parseInt(s);
            // s is a valid integer
            isValidInteger = true;
        }
        catch (NumberFormatException ex)
        {
            // s is not an integer
        }
        return isValidInteger;
    }


    /**
     * Saves client ID
     * @param id int client ID
     */
    public void setId(int id) {
        clientID = id;
        // can also save the name here
    }
    /**
     * Gets the client ID
     */
    public int getId(){
        return clientID;
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
//    public void getResponse() throws RemoteException {
//
//        Scanner keyboard = new Scanner(System.in);
//        System.out.print("Response: ");
//         response =  keyboard.nextLine();
//        timer.cancel();
//        timer.purge();
//        task.cancel();
//        System.out.println();
//        coord.submitResponse(response, clientID);
//        response = "";
//    }

//    public void getResponse() throws RemoteException {
////        timer = new Timer();
////        timer.schedule( task = new TimerTask() {
////            public void run()
////            {
////                if( response.equals("") )
////                {
////                    System.out.println( "took too long to answer. You volunteered to give random vote" );
////                    try {
////                        coord.submitResponse("I took to long to vote :(", clientID);
////                        task.cancel();
////                        timer.cancel();
////                        timer.purge();
////                        return;
////                    } catch (RemoteException e) {
////                        e.printStackTrace();
////                    }
////                }
////            }
////        }, 5*1000 );
////        System.out.println("you have 30 seconds to respond");
////        try {
////            getResponseWithTimer();
////        }catch (Exception e){
////            System.out.println("Something went wrong with timer");
////        }
//        TimedScanner in = new TimedScanner(System.in);
//        System.out.print("Enter your name: ");
//        try
//        {
//            String name = null;
//            if ((name = in.nextLine(5000)) == null)
//            {
//                System.out.println("Too slow!");
//                coord.submitResponse("too slow", clientID);
//
//            }
//            else
//            {
//                System.out.println("Hello, " + name);
//                coord.submitResponse(name, clientID);
//            }
//        }
//        catch (InterruptedException | ExecutionException e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

//    }
    public void getResponse() throws RemoteException {
        Scanner keyboard = new Scanner(System.in);

        System.out.print("Response: ");
        String response =  keyboard.nextLine();
        System.out.println();
        coord.submitResponse(response, clientID);
    }

    public void notifyGameFull(){
        System.out.println("Registration successful. Game is full and has started.");
    }
    /**
     * Prints the round number at the beginning of each round
     * @param round int round number
     */
    public void startRound(int round) {
        System.out.println("Starting round " + round);

    }

}

