package csc435.app;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
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
    // TO-DO keep track of the connection (socket)
    Socket socket;
    PrintWriter sockOut;
    BufferedReader sockIn;
    IndexExchangeEncoder decoder = new IndexExchangeEncoder();
    int clientID;
    static int bytesRead = 0;


    public ClientProcessingEngine() {
    }

    public IndexResult indexFiles(String folderPath) throws IOException {
        IndexResult result = new IndexResult(0.0, 0);
        Pattern compiledPattern = Pattern.compile("\\b[a-zA-Z0-9_-]{4,}\\b");
        //start timer
        long indexStartTime = System.currentTimeMillis();
        //long indexStartTimeNano = System.nanoTime();
        // uses walk to traverse tree with custom visitFile for processing
        Path file =  Paths.get(folderPath);
        Files.walkFileTree(Paths.get(folderPath), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                HashMap<String, Long> uniqueWords = new HashMap<String, Long>();
                String line = "";
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file.toFile()), Charset.forName("UTF-8")));
                    while ((line = in.readLine()) != null) {
                        Matcher matcher = compiledPattern.matcher(line.toLowerCase());
                        bytesRead += line.getBytes(Charset.forName("UTF-8")).length;
                        while (matcher.find()) {
                            uniqueWords.putIfAbsent(matcher.group(), 0L);
                            uniqueWords.put(matcher.group(), uniqueWords.get(matcher.group()) + 1);
                        }
                    }
                    in.close();
                    String msg = decoder.encodeMsg("INDX_REQ", clientID, uniqueWords, file.toString());
                    sockOut.println(msg);
                    if (!decoder.decodeMsg(sockIn.readLine())[2].equals("OK")) System.out.println("Error indexing");
                } catch (IOException e) {
                    System.out.println(e);
                }

                return FileVisitResult.CONTINUE;
            }
        });
        result.totalBytesRead = bytesRead;

//        long executionTimeNano = System.nanoTime() - indexStartTimeNano;
//        System.out.println("Nano execution time: " + executionTimeNano);
        result.executionTime = System.currentTimeMillis() - indexStartTime;
        return result;
    }
    // TO-DO get the start time
    // TO-DO crawl the folder path and extrac all file paths
    // TO-DO for each file extract all words/terms and count their frequencies
    // TO-DO increment the total number of bytes read
    // TO-DO for each file prepare an INDEX REQUEST message and send to the server
    //       the document path, the client ID and the word frequencies
    // TO-DO receive for each INDEX REQUEST message an INDEX REPLY message
    // TO-DO get the stop time and calculate the execution time
    // TO-DO return the execution time and the total number of bytes read

    public SearchResult search(ArrayList<String> terms) {
        SearchResult result = new SearchResult(0.0, new ArrayList<DocPathFreqPair>());
        String searchStr = "";
        for (String term: terms) {
            searchStr += term + ",";
        }
        String outMsg = decoder.encodeMsg("SRCH_REQ", clientID, searchStr);
        try {
            sockOut.println(outMsg);
            String[] commandStrings = decoder.decodeMsg(sockIn.readLine());
            if (commandStrings[0].equals("SRCH_RPL")) {
                for (String str : commandStrings[2].split(";")) {
                    result.documentFrequencies.add(new DocPathFreqPair(str.split(",")[0], Long.parseLong(str.split(",")[1])));
                }
            }
            result.documentFrequencies.sort(DocPathFreqPair::compareTo);
        } catch (Exception e) {
            System.out.println("Error searching");
        }
        // TO-DO prepare a SEARCH REQUEST message that includes the search terms and send it to the server
        // TO-DO receive one or more SEARCH REPLY messages with the results of the search query
        // TO-DO get the stop time and calculate the execution time
        // TO-DO return the execution time and the top 10 documents and frequencies

        return result;
    }

    public long getInfo() {
        // TO-DO return the client ID

        return 0;
    }

    public void connect(String serverIP, String serverPort) {
        // TO-DO implement connect to server
        try {
            socket = new Socket(InetAddress.getByName(serverIP), Integer.parseInt(serverPort));
            sockOut = new PrintWriter(socket.getOutputStream(), true);
            sockIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String outputLine;
            String[] response;
            outputLine = "REGI_REQ-1";
            sockOut.println(outputLine);

            response = decoder.decodeMsg(sockIn.readLine());
            //!TODO assign client ID to local var
            clientID = Integer.parseInt(response[2]);
            System.out.println("Client ID: " + response[2]);
        } catch (IOException e) {
            System.out.println("Could not connect to the server");
        }
        // TO-DO create a new TCP/IP socket and connect to the server
        // TO-DO send a REGISTER REQUEST message and receive a REGISTER REPLY message with the client ID
    }


    public void disconnect() {
        // TO-DO implement disconnect from server
        sockOut.println(decoder.encodeMsg("QUIT_REQ", clientID, ""));
        try {
            sockOut.close();
            sockIn.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Error disconnecting from socket");
        }
        // TO-DO send a QUIT message to the server
        // TO-DO close the TCP/IP socket
    }
}