package csc435.app;

import org.zeromq.ZContext;

import java.util.ArrayList;

public class ServerProcessingEngine {
    private IndexStore store;
    // TO-DO keep track of the ZMQ context

    static ZContext context;
    ZMQProxyWorker proxy;
    Thread proxyThread;
    static ArrayList<Thread> workerThreads = new ArrayList<>();

    // TO-DO keep track of the ZMQ Proxy thread and worker threads

    public ServerProcessingEngine(IndexStore store) {
        this.store = store;
    }

    public void initialize(int serverPort, int numWorkerThreads) {
        // TO-DO initialize the ZMQ context
        try {
            context = new ZContext();
            proxy = new ZMQProxyWorker(context, serverPort);
            proxyThread = new Thread(proxy);
            proxyThread.start();
            for (int i = 0; i < numWorkerThreads; i++) {
                ServerWorker worker = new ServerWorker(store, context);
                Thread workerThread = new Thread(worker);
                workerThreads.add(workerThread);
                workerThread.start();
            }
        } catch (Exception e) {
            System.out.println("Error initializing server processing engine");
        }
        // TO-DO create a ZMQ Proxy object
        // TO-DO create and start the ZMQ Proxy thread
        // TO-DO create Server Worker objects
        // TO-DO create and start the worker threads
    }

    public void shutdown() {
        // TO-DO destroy the ZMQ context
        context.destroy();
        try {
            for (Thread thread : workerThreads) {
                thread.join();
            }
            proxyThread.join();
        } catch (Exception e) {
        }

        // TO-DO join the ZMQ Proxy and worker threads
    }
}
