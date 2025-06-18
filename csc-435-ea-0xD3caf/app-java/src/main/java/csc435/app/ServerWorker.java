package csc435.app;

import org.zeromq.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ServerWorker implements Runnable {
    private IndexStore store;
    private ZContext context;
    private static int nextId = 1;
    private static int serverID = 0;

    public ServerWorker(IndexStore store, ZContext context) {
        this.store = store;
        this.context = context;
    }

    @Override
    public void run() {
        ZMQ.Socket socket;
        try {
            socket = context.createSocket(SocketType.REP);
            socket.connect("inproc://workers");
            // TO-DO create a reply socket and connect it to the dealer
            // TO-DO receive a message from the client
            while (!Thread.currentThread().isInterrupted()) {
                String request = socket.recvStr(0);
                MessageTypes type = MessageInterface.getType(request);
                if (type.equals(MessageTypes.REGISTER_REQUEST)) {
                    RegisterMsg message = new RegisterMsg.msgBuilder().build();
                    message.deserializeMsg(request);
                    int offeredId = nextId;
                    nextId++;
                    RegisterMsg registerMsg = new RegisterMsg.msgBuilder()
                            .clientId(String.valueOf(serverID))
                            .command(MessageTypes.REGISTER_RESPONSE)
                            .offeredId(String.valueOf(offeredId))
                            .build();
                    socket.send(registerMsg.serializeMsg());
                }

                if (type.equals(MessageTypes.INDEX_REQUEST)) {
                    IndexMsg clientRequest = new IndexMsg.msgBuilder().build();
                    clientRequest.deserializeMsg(request);
                    long docId = store.putDocument(clientRequest.getClientId() + clientRequest.getDocumentPath());
                    store.updateIndex(docId, clientRequest.getFreqPairs());
                    socket.send(new IndexMsg.msgBuilder().clientId("00").command(MessageTypes.INDEX_RESPONSE).build().serializeMsg());
                }
                if (type.equals(MessageTypes.SEARCH_REQUEST)) {
                    SearchResult result = new SearchResult(0.0, new ArrayList<DocPathFreqPair>());
                    HashMap<Long, ArrayList<Long>> pairsIntersection = new HashMap<Long, ArrayList<Long>>();
                    SearchMsg clientRequest = new SearchMsg.msgBuilder().build();
                    clientRequest.deserializeMsg(request);
                    for (String term : clientRequest.getSearchTerms()) {
                        for (DocFreqPair p : store.lookupIndex(term)) {
                            pairsIntersection.putIfAbsent(p.documentNumber, new ArrayList<Long>());
                            pairsIntersection.get(p.documentNumber).add(p.wordFrequency);
                        }
                    }
                    for (long key : pairsIntersection.keySet()) {
                        if (pairsIntersection.get(key).size() == clientRequest.getSearchTerms().size()) {
                            long sum = 0L;
                            for (Long l : pairsIntersection.get(key)) {
                                sum += l;
                            }
                            result.documentFrequencies.add(new DocPathFreqPair(store.getDocument(key), sum));
                        }

                    }
                    int totalResults = result.documentFrequencies.size();

                    result.documentFrequencies.sort(DocPathFreqPair::compareTo);
                    if (result.documentFrequencies.size() > 10) {
                        result.documentFrequencies = new ArrayList<DocPathFreqPair>(result.documentFrequencies.subList(0, 10));
                    }
                    SearchMsg reply = new SearchMsg.msgBuilder()
                            .clientId("00")
                            .command(MessageTypes.SEARCH_RESPONSE)
                            .searchTerms(clientRequest.getSearchTerms())
                            .resultCount(totalResults)
                            .searchResults(result)
                            .build();
                    socket.send(reply.serializeMsg());
                }
                if (type.equals(MessageTypes.QUIT_REQUEST)) {
                    Thread.currentThread().interrupt();
                }
            }
            socket.close();
        //catches context error created by passing context between threads
        } catch (Exception e) {}
    }
}
