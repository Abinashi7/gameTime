package client;

import coordinator.CoordinateGame;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Scanner;

// responsible to make calls to the coordinator that will connect the player with a server
public class ClientDriver extends UnicastRemoteObject implements ClientImpl {

    static CoordinateGame coord;
    private Integer clientID;

    protected ClientDriver() throws RemoteException {
    }

    public void startGame(){
        // announce to client that game is starting
    }

    public void connectToCoord() {
        try {
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
            /** placeholder to register individual clients with the coordinator **/
            if (coord.registerClient(clientID)) {
                System.out.println("Registration successful. Waiting for game start...");
            } else {
                System.out.println("Server full. Please wait for next game.");
                System.exit(0);
            }
        } catch (RemoteException e) {
            System.out.println("Remote Exception: " + e.getMessage());
        }
    }

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

    public void printWinner(int key) throws RemoteException{
        System.out.println();
        System.out.println("*******************************");
        System.out.println("Player " + key  + " is the Winner!!!!!");
        System.out.println("*******************************");
        System.out.println();
        System.out.println("Game is over! Shutting down client..");
        System.exit(0);
    }
    public void displayCard(){
        // receive the broadcast from the coordinator and display the card
    }

    public void submitResponse(String response){
        // submit input response to coordinator with the client ID for tracking
    }

    public void displayResponses(HashMap<Integer, String>  responses){
        //the returned list of all player responses from the coordinator
        // print to client with some sort of label formatting (1: response)
        int responseCount = 0;
        for (int key : responses.keySet()){
            responseCount++;
            System.out.println(responseCount + ":  " + responses.get(key));
        }
    }

    public void vote(int i){
        // submit the vote choice to the coordinator
    }

    public int gatherVote( HashMap<Integer, String> responses) {
        System.out.println("Type in the number next to the response you think is the best!");
        Scanner keyboard = new Scanner(System.in);
        String response =  keyboard.nextLine();
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

    public void setId(int id) {
        clientID = id;
    }

    public void displayCard(String card) {
        System.out.println(card);
    }

    /** Added this method to open up Scanner for keyboard input. Might not be the best solution */
    public void getResponse() throws RemoteException {
        Scanner keyboard = new Scanner(System.in);
        System.out.print("Response: ");
        String response =  keyboard.nextLine();
        System.out.println();
        coord.submitResponse(response, clientID);
    }

    public void startRound(int round) {
        System.out.println("Starting round " + round);

    }

}
