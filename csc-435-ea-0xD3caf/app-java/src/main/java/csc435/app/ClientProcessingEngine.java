package csc435.app;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

class IndexResult {
    public double executionTime;
    public long totalBytesRead;

    public IndexResult(double executionTime, long totalBytesRead) {
        this.executionTime = executionTime;
        this.totalBytesRead = totalBytesRead;
    }
}

class DocPathFreqPair implements Comparable<DocPathFreqPair> {
    public String documentPath;
    public long wordFrequency;

    public DocPathFreqPair(String documentPath, long wordFrequency) {
        this.documentPath = documentPath;
        this.wordFrequency = wordFrequency;
    }
    @Override
    public int compareTo(DocPathFreqPair other) {
        return Long.compare(other.wordFrequency, this.wordFrequency);
    }
}

class SearchResult {
    public double excutionTime;
    public ArrayList<DocPathFreqPair> documentFrequencies;

    public SearchResult(double executionTime, ArrayList<DocPathFreqPair> documentFrequencies) {
        this.excutionTime = executionTime;
        this.documentFrequencies = documentFrequencies;
    }
}

public class ClientProcessingEngine {
    private ZContext context;
    private ZMQ.Socket socket;
    private int clientId;
    // TO-DO keep track of the ZMQ context
    // TO-DO keep track of the request socket

    public ClientProcessingEngine() {
    }

    public IndexResult indexFolder(String folderPath) throws IOException {
        IndexResult result = new IndexResult(0.0, 0);
        Pattern compiledPattern = Pattern.compile("\\b[a-zA-Z0-9_-]{4,}\\b");
        long indexStartTime = System.currentTimeMillis();
        Files.walkFileTree(Paths.get(folderPath), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                HashMap<String, Long> uniqueWords = new HashMap<String, Long>();
                String line;
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file.toFile()), Charset.forName("UTF-8")));
                    while ((line = in.readLine()) != null) {
                        Matcher matcher = compiledPattern.matcher(line.toLowerCase());
                        result.totalBytesRead += line.getBytes(Charset.forName("UTF-8")).length;
                        while (matcher.find()) {
                            uniqueWords.putIfAbsent(matcher.group(), 0L);
                            uniqueWords.put(matcher.group(), uniqueWords.get(matcher.group()) + 1);
                        }
                    }
                    HashMap<String, Long> temp = new HashMap<>();
                    int count = 0;
                    for (String word : uniqueWords.keySet()) {
                        if (count == 500) {
                            temp.put(word, uniqueWords.get(word));
                            IndexMsg indexRequest = new IndexMsg.msgBuilder()
                                    .command(MessageTypes.INDEX_REQUEST)
                                    .clientId(String.valueOf(clientId))
                                    .freqPairs(temp)
                                    .documentPath(file.toString())
                                    .build();
                            socket.send(indexRequest.serializeMsg());
                            IndexMsg responseMsg = new IndexMsg.msgBuilder().build();
                            String response = socket.recvStr();
                            responseMsg.deserializeMsg(response);
                            count = 0;
                            temp.clear();
                        } else {
                            temp.put(word, uniqueWords.get(word));
                            count ++;
                        }
                    }
                    IndexMsg indexRequest = new IndexMsg.msgBuilder()
                            .command(MessageTypes.INDEX_REQUEST)
                            .clientId(String.valueOf(clientId))
                            .freqPairs(temp)
                            .documentPath(file.toString())
                            .build();
                    socket.send(indexRequest.serializeMsg());
                    IndexMsg responseMsg = new IndexMsg.msgBuilder().build();
                    String response = socket.recvStr();
                    responseMsg.deserializeMsg(response);
                    in.close();
                } catch (IOException e) {
                    System.out.println("Error indexing files: " + e.getMessage());
                }
                return FileVisitResult.CONTINUE;
            }
        });
        result.executionTime = System.currentTimeMillis() - indexStartTime;
        return result;
    }
    
    public SearchResult search(ArrayList<String> terms) {
        long indexStartTime = System.currentTimeMillis();
        SearchResult result = new SearchResult(0.0, new ArrayList<DocPathFreqPair>());
        SearchMsg request = new SearchMsg.msgBuilder()
                .command(MessageTypes.SEARCH_REQUEST)
                .clientId(String.valueOf(clientId))
                .termCount(terms.size())
                .searchTerms(terms)
                .searchResults(result)
                .build();
        socket.send(request.serializeMsg());
        SearchMsg responseMsg = new SearchMsg.msgBuilder().build();
        String response = socket.recvStr();
        responseMsg.deserializeMsg(response);
        result = responseMsg.getSearchResults();
        result.excutionTime = System.currentTimeMillis() - indexStartTime;
        // TO-DO get the start time
        // TO-DO prepare a SEARCH REQUEST message that includes the search terms and send it to the server
        // TO-DO receive one or more SEARCH REPLY messages with the results of the search query
        // TO-DO get the stop time and calculate the execution time
        // TO-DO return the execution time and the top 10 documents and frequencies
        return result;
    }

    public long getInfo() {
        // TO-DO return the client ID
        return clientId;
    }

    public void connect(String serverIP, String serverPort) {
        // TO-DO initialize the ZMQ context
        context = new ZContext();
        // TO-DO create the request socket and connect it to the server
        socket = context.createSocket(SocketType.REQ);
        socket.connect("tcp://" + serverIP + ":" + serverPort);
        RegisterMsg registerMsg = new RegisterMsg.msgBuilder()
                .command(MessageTypes.REGISTER_REQUEST)
                .clientId("-1")
                .build();
        socket.send(registerMsg.serializeMsg());
        RegisterMsg replyMsg = new RegisterMsg.msgBuilder().build();
        String reply = socket.recvStr();
        replyMsg.deserializeMsg(reply);
        clientId = Integer.parseInt(replyMsg.getMsg());
        System.out.println("Received client id: " + clientId);
        // send a REGISTER REQUEST message and receive a REGISTER reply message with the client ID
    }

    public void disconnect() {
        socket.send("QUIT_REQUEST");
        socket.close();
        context.close();
        // TO-DO implement disconnect from server
        // TO-DO send a QUIT message to the server
        // close the request socket and the context
    }
}
