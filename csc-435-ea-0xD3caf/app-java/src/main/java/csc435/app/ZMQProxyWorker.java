package csc435.app;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ZMQProxyWorker implements Runnable {
    private ZContext context;
    private int serverPort;

    public ZMQProxyWorker(ZContext context, int serverPort) {
        this.context = context;
        this.serverPort = serverPort;
    }
    
    @Override
    public void run() {
        // TO-DO create and bind router and dealer sockets
        ZMQ.Socket clients = context.createSocket(SocketType.ROUTER);
        clients.bind("tcp://*:" + serverPort);
        ZMQ.Socket workers = context.createSocket(SocketType.DEALER);
        workers.bind("inproc://workers");
        // TO-DO create and start the ZMQ Proxy that will forward messages between the router and dealer sockets
        ZMQ.proxy(clients, workers, null);
        context.close();
        clients.close();
        workers.close();
        // TO-DO close the router and dealer sockets
        // TO-DO close the context
    }
}
