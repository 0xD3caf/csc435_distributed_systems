package csc435.app;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerProcessingEngine {
    private static int nextID = 0;
    private IndexStore store;
    private Dispatcher dispatcher;
    private Thread dispatcherThread;
    private ArrayList<ServerWorker> workers;
    private static ArrayList<Thread> workerThreads = new ArrayList<>();
    private static HashMap<Integer, String> clientInfo = new HashMap<>();
    // TO-DO keep track of the Dispatcher thread
    // TO-DO keep track of the server worker threads
    // TO-DO keep track of the clients information

    public ServerProcessingEngine(IndexStore store) {
        this.store = store;
    }

    public void initialize(int serverPort) {
        // TO-DO create and start the Dispatcher thread
        dispatcher = new Dispatcher(this, serverPort);
        dispatcherThread = new Thread(dispatcher);
        dispatcherThread.start();
    }

    public void spawnWorker(Socket clientSocket) {
        // TO-DO create and start a new Index Worker thread
        ServerWorker serverWorker = new ServerWorker(store, clientSocket, this);
        Thread workerThread = new Thread(serverWorker);
        workerThread.start();
        workerThreads.add(workerThread);
    }


    public void shutdown()   {
        // TO-DO signal the Dispatcher thread to shutdown
        try {
            dispatcher.kill();
            Socket tempSocket  = new Socket(InetAddress.getByName("0.0.0.0"), dispatcher.getPort());
            tempSocket.close();
            dispatcherThread.join();
            for (Thread t: workerThreads) {
                t.join();
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // TO-DO join the Dispatcher and Index Worker threads
    }

    public ArrayList<String> getConnectedClients() {
        // TO-DO return the connected clients information
        ArrayList<String> clients = new ArrayList<>();
        for (int id: clientInfo.keySet()) {
            clients.add(String.valueOf(id) + ":" + clientInfo.get(id));
        }
        return clients;
    }

    public static synchronized int getNextID() {
        nextID++;
        return nextID;
    }

    public static void addClient (int id, String info) {
        clientInfo.put(id, info);
    }

    public static void removeClient (int id) {
        clientInfo.remove(id);
    }
}
