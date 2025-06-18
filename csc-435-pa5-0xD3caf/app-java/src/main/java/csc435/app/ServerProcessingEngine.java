package csc435.app;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.util.ArrayList;
import java.util.HashMap;

public class ServerProcessingEngine {
    private IndexStore store;
    private RPCServerWorker worker;
    Thread serverThread;
    // TO-DO keep track of the RPCServerWorker object
    // TO-DO keep track of the gRPC server thread

    public ServerProcessingEngine(IndexStore store) {
        this.store = store;
    }

    // TO-DO create and start the gRPC Server
    public void initialize(int serverPort) {
        // TO-DO create the RPCServerWorker object
        System.out.println("Initializing server processing engine...");
        worker = new RPCServerWorker(store, serverPort);
        serverThread = new Thread(worker);
        serverThread.start();
        // TO-DO create and start the gRPC server thread that runs in the context of the RPCServerWorker object
    }

    // TO-DO shutdown the gRPC Server
    public void shutdown() {
        try {
            worker.shutdown();
            serverThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // TO-DO call shutdown on the RPCServerWorker object
        // TO-DO join the gRPC server thread
    }
}
