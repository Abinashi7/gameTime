package client;

import coordinator.CoordinateGame;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
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

    public void drawCard(){

    }

    public void displayCard(){
        // receive the broadcast from the coordinator and display the card
    }

    public void submitResponse(String response){
        // submit input response to coordinator with the client ID for tracking
    }

    public void displayResponses(HashMap responses){
        //the returned list of all player responses from the coordinator
        // print to client with some sort of label formatting (1: response)
    }

    public void vote(int i){
        // submit the vote choice to the coordinator
    }

    public int gatherVote(String card, ArrayList<String> Responses) {
        //TODO: game logic displaying options and getting votes
        return 1;
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
