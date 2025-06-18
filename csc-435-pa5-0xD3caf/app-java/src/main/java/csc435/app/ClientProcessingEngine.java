package csc435.app;

import com.google.protobuf.Empty;
import csc435.app.FileRetrievalEngineGrpc.FileRetrievalEngineBlockingStub;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    //custom compare class to allow easy sorting by frequency
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
    ManagedChannel channel;
    FileRetrievalEngineBlockingStub stub;
    int clientId;

    public ClientProcessingEngine() { }


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
                    IndexRep reply = stub.computeIndex(IndexReq.newBuilder()
                            .setClientId(clientId)
                            .setDocumentPath(file.toString())
                            .putAllWordFrequencies(uniqueWords)
                            .build());
                    if (!reply.getAck().equals("OK")) {
                        System.out.println("Error indexing files");
                    }
                    in.close();
                } catch (IOException e) {
                    System.out.println(e);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        result.executionTime = System.currentTimeMillis() - indexStartTime;
        return result;
    }
    
    public SearchResult search(ArrayList<String> terms) {
        SearchResult result = new SearchResult(0.0, new ArrayList<DocPathFreqPair>());
        long searchStartTime = System.currentTimeMillis();

        SearchRep reply = stub.computeSearch(SearchReq.newBuilder().addAllTerms(terms).build());
        Map<String, Long> topWords = reply.getSearchResultsMap();
        for (Map.Entry<String, Long> entry : topWords.entrySet()) {
            result.documentFrequencies.add(new DocPathFreqPair(entry.getKey(), entry.getValue()));
        }
        result.excutionTime = System.currentTimeMillis() - searchStartTime;
        result.documentFrequencies.sort(DocPathFreqPair::compareTo);
        return result;
    }

    public long getInfo() {
        return clientId;
    }

    public void connect(String serverIP, String serverPort) {
        channel = ManagedChannelBuilder.forAddress(serverIP, Integer.parseInt(serverPort)).usePlaintext().build();
        stub = FileRetrievalEngineGrpc.newBlockingStub(channel);
        RegisterRep reply = stub.register(Empty.newBuilder().build());
        System.out.println("Client Connected with ID: " + reply.getClientId());
        clientId = reply.getClientId();
    }

    public void disconnect () {
        channel.shutdownNow();
    }
}
