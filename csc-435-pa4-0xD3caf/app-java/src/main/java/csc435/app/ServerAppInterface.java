package csc435.app;

import java.lang.System;
import java.util.ArrayList;
import java.util.Scanner;

public class ServerAppInterface {
    private ServerProcessingEngine engine;

    public ServerAppInterface(ServerProcessingEngine engine) {
        this.engine = engine;
    }

    public void readCommands() {
        // TO-DO implement the read commands method
        Scanner sc = new Scanner(System.in);
        String command;
        System.out.println("<quit | list>");
        while (true) {
            System.out.print("> ");

            // read from command line
            command = sc.nextLine();

            // if the command is quit, terminate the program
            if (command.compareTo("quit") == 0) {
                engine.shutdown();
                break;
            }

            // if the command begins with list, list all the connected clients
            if (command.length() >= 4 && command.substring(0, 4).compareTo("list") == 0) {
                // TO-DO call the getConnectedClients method from the server to retrieve the clients information
                ArrayList<String> clients = engine.getConnectedClients();
                if (clients.size() == 0) {
                    System.out.println("No clients connected");
                }
                for (String client: clients) {
                    System.out.println("Client " + client);
                }

                // TO-DO print the clients information
                continue;
            }

            System.out.println("unrecognized command!");
        }

        sc.close();
    }
}
