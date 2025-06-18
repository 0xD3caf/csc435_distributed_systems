package csc435.app;


import java.io.IOException;
import java.net.*;

public class Dispatcher implements Runnable {
    private ServerProcessingEngine engine;
    private boolean running;
    private int port;
    private String address;

    public Dispatcher(ServerProcessingEngine engine, int port) {
        this.engine = engine;
        this.port = port;
        this.address = "0.0.0.0";
    }

    public void kill() {
        this.running = false;
    }

    @Override
    public void run() {
        running = true;
        try {
            ServerSocket serverSocket = new ServerSocket(port, 5, InetAddress.getByName(address));
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    engine.spawnWorker(clientSocket);
                } catch (IOException e) {
                    System.out.println("Error connecting to client");;
                }
            }
            serverSocket.close();
        } catch (UnknownHostException e) {
            System.out.println("Unknown host: " + e);
        }catch (IOException e) {
            System.out.println("I/O Error: " + e);
        }
    }

    public int getPort() {
        return port;
    }
}

