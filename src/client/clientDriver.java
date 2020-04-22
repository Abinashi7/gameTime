package client;

import java.util.ArrayList;
import java.util.HashMap;

// responsible to make calls to the coordinator that will connect the player with a server
public class clientDriver {

    private Integer clientID;

    public void startGame(){
        // announce to client that game is starting
    }

    public void drawCard(){
        // tell coordinator to draw card
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
}
