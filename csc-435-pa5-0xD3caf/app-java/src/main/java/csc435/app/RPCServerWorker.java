package csc435.app;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class RPCServerWorker implements Runnable {
    private IndexStore store;
    Server server;
    int serverPort;
    // TO-DO keep track of the gRPC Server object

    public RPCServerWorker(IndexStore store, int serverPort) {
        this.store = store;
        this.serverPort = serverPort;

    }

    @Override
    public void run() {
        try {
            server = ServerBuilder.forPort(serverPort).addService(new FileRetrievalEngineService(store)).build();
            server.start();
        } catch (IOException e) {
            System.out.println("Error starting RPC server");
        }
        // TO-DO build the gRPC Server
        // TO-DO register the FileRetrievalEngineService service with the gRPC Server
        // TO-DO start the gRPC Server
    }

    public void shutdown() throws InterruptedException {
        server.shutdown();
        server.awaitTermination();
        // TO-DO shutdown the gRPC server
        // TO-DO wait for the gRPC server to shutdown
    }
}
