package client;


import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.concurrent.*;

public class TimedScanner
{
    public TimedScanner(InputStream input)
    {
        in = new Scanner(input);
    }

    private Scanner in;
    private ExecutorService ex = Executors.newSingleThreadExecutor(new ThreadFactory()
    {
        @Override
        public Thread newThread(Runnable r)
        {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        }
    });

//    public static void main(String[] args)
//    {
//        TimedScanner in = new TimedScanner(System.in);
//        System.out.print("Enter your name: ");
//        try
//        {
//            String name = null;
//            if ((name = in.nextLine(5000)) == null)
//            {
//                System.out.println("Too slow!");
//            }
//            else
//            {
//                System.out.println("Hello, " + name);
//            }
//        }
//        catch (InterruptedException e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        catch (ExecutionException e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

    public String nextLine(int timeout) throws InterruptedException, ExecutionException
    {
        Future<String> result = ex.submit(new Worker());
        try
        {
            return result.get(timeout, TimeUnit.MILLISECONDS);
        }
        catch (TimeoutException e)
        {
            return null;
        }
    }

    private class Worker implements Callable<String>
    {
        @Override
        public String call() throws Exception
        {
            return in.nextLine();
        }
    }
}

//    /** start game by broadcasting start to clients */
//    private static void startGame() throws RemoteException {
//        roundNumber++;
//        System.out.println("Starting round " + roundNumber);
//
//        try {
//            Thread.sleep(2000); //this is here to improve the flow of the game
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        if(roundNumber == 1){
//            for (ClientImpl allClient : allClients) {
//                allClient.notifyGameFull();
//            }
//        }
//        for (int i = 0; i < allClients.size(); i++){
//            allClients.get(i).startRound(roundNumber);
//        }
//
//        getNewCard();
//        for (int i = 0; i < allClients.size(); i++) {
//            new ResponseThread(i).start();
//        }
//    }