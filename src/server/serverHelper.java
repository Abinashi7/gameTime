package server;


/**
 * This class handles and append the server info into the servers array
 */
public class serverHelper {
    public int[] servers = new int[5];

    public void loadServers(String[] args) {
        if (args.length < 5) {

            throw new IllegalArgumentException("Please input at least 5 different servers");
        }
        for (int i = 0 ; i < args.length ; i++)
        {
            servers[i] = Integer.parseInt(args[i]);
        }

    }

}
