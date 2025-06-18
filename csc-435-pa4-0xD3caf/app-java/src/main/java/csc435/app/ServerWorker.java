package csc435.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerWorker implements Runnable {
    private IndexStore store;
    private ServerProcessingEngine engine;
    private Socket clientSocket;
    int clientID;

    public ServerWorker(IndexStore store, Socket clientSocket, ServerProcessingEngine engine) {
        this.store = store;
        this.engine = engine;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        String[] commandStrings;
        IndexExchangeEncoder decoder = new IndexExchangeEncoder();
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                commandStrings = decoder.decodeMsg(inputLine);
                String command = commandStrings[0];
                String id = commandStrings[1];
                String msg = commandStrings[2];
                if ( command.compareTo("QUIT_REQ") == 0){
                    ServerProcessingEngine.removeClient(clientID);
                    break;
                }
                if (command.compareTo("REGI_REQ") == 0) {
                    clientID = ServerProcessingEngine.getNextID();
                    ServerProcessingEngine.addClient(clientID, String.valueOf(clientSocket.getRemoteSocketAddress()));
                    out.println(decoder.encodeMsg("REGI_RPL", 0, String.valueOf(clientID)));
                }
                if (command.compareTo("INDX_REQ") == 0) {
                    boolean first = true;
                    long docID = 0;
                    HashMap<String,Long> freqPairMap = new HashMap<>();
                    for (String m: msg.split(",")) {
                        // if this is the first part, its the document path and we should update the store
                        if (first) {
                            docID = store.putDocument(id + ":" + m);
                            first = false;
                        } else {
                            //splits string into document word and count
                            freqPairMap.put(m.split(":")[0], Long.valueOf(m.split(":")[1]));
                        }
                    }
                    store.updateIndex(docID, freqPairMap);
                    out.println(decoder.encodeMsg("INDX_RPL", 0, "OK"));
                }
                if (command.compareTo("SRCH_REQ") == 0) {
                    String[] searchTerms = msg.split(",");
                    SearchResult result = new SearchResult(0.0, new ArrayList<DocPathFreqPair>());
                    HashMap<Long, ArrayList<Long>> pairsIntersection = new HashMap<Long, ArrayList<Long>>();
                    for (String term : searchTerms) {
                        for (DocFreqPair p: store.lookupIndex(term)) {
                            pairsIntersection.putIfAbsent(p.documentNumber, new ArrayList<Long>());
                            pairsIntersection.get(p.documentNumber).add(p.wordFrequency);
                        }

                    }
                    for (long key: pairsIntersection.keySet()) {
                        if (pairsIntersection.get(key).size() == searchTerms.length) {
                            long sum = 0L;
                            for (Long l: pairsIntersection.get(key)) {
                                sum += l;
                            }
                            result.documentFrequencies.add(new DocPathFreqPair(store.getDocument(key), sum));
                        }

                    }
                    result.documentFrequencies.sort(DocPathFreqPair::compareTo);
                    int totalResults = result.documentFrequencies.size();
                    if (result.documentFrequencies.size() > 10) {
                        result.documentFrequencies = new ArrayList<DocPathFreqPair>(result.documentFrequencies.subList(0,10));
                    }
                    StringBuilder sb = new StringBuilder();
                    for (DocPathFreqPair pair: result.documentFrequencies) {
                        sb.append(String.format("%s,%s;", pair.documentPath, pair.wordFrequency));
                    }
                    out.println(decoder.encodeMsg("SRCH_RPL", 0, sb.toString()));
                }

                // TO-DO receive a message from the client
                // TO-DO if the message is a REGISTER REQUEST, then
                //       generate a new client ID and return a REGISTER REPLY message containing the client ID
                // TO-DO if the message is an INDEX REQUEST, then
                //       extract the document path, client ID and word frequencies from the message(s)
                //       get the document number associated with the document path (call putDocument)
                //       update the index store with the word frequencies and the document number
                //       return an acknowledgement INDEX REPLY message
                // TO-DO if the message is a SEARCH REQUEST, then
                //       extract the terms from the message
                //       for each term get the pairs of documents and frequencies from the index store
                //       combine the returned documents and frequencies from all of the specified terms
                //       sort the document and frequency pairs and keep only the top 10
                //       for each document number get from the index store the document path
                //       return a SEARCH REPLY message containing the top 10 results
                // TO-DO if the message is a QUIT message, then finish running
            }
            out.close();
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            clientSocket.close();
        } catch (Exception e) {};
    }
}