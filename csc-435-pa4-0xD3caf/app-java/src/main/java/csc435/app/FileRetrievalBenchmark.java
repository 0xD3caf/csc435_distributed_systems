package csc435.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class BenchmarkWorker implements Runnable {
    // TO-DO declare a ClientProcessingEngine
    ClientProcessingEngine engine;
    String ip,port,path;
    long totalBytes;
    IndexResult result;

    public BenchmarkWorker (String ip, String port, String path) {
        this.ip = ip;
        this.port = port;
        this.path = path;
    }

    @Override
    public void run() {
        engine = new ClientProcessingEngine();
        engine.connect(ip, port);
        try {
            result = engine.indexFiles(path);
        } catch (IOException e) {
            System.out.println("ERROR");
        }
        totalBytes = result.totalBytesRead;
        // TO-DO connect the ClientProcessingEngine to the server
        // TO-DO index the dataset
    }


    public void search(String inputStr) {
        ArrayList<String> terms = new ArrayList<>(Arrays.asList(inputStr.split(" AND ")));
        SearchResult result = engine.search(terms);
        System.out.println("SEARCH RESULT: " + inputStr);
        System.out.printf("Search completed in %.2f seconds\n", result.excutionTime/1000);
        System.out.printf("Search results (top 10 out of %s)\n", result.documentFrequencies.size());
        for (DocPathFreqPair pair: result.documentFrequencies) {
            System.out.printf("* Client %24s: %s\n", pair.documentPath, pair.wordFrequency);
        }
        // TO-DO perform search operations on the ClientProcessingEngine
        // TO-DO print the results and performance
    }

    public void disconnect() {
        engine.disconnect();
        // TO-DO disconnect the ClientProcessingEngine from the server
    }
}

public class FileRetrievalBenchmark {
    public static void main(String[] args)
    {
        double startTime =System.currentTimeMillis();
        int numberOfClients = Integer.parseInt(args[0]);
        String serverIP = args[1];
        String serverPort = args[2];
        String path = args[3] + "/";
        ArrayList<BenchmarkWorker> workers = new ArrayList<BenchmarkWorker>();
        long combinedBytes = 0;
        System.out.println("Connecting Clients...");
        ExecutorService benchPool = Executors.newFixedThreadPool(numberOfClients);
        for (int i = 1; i <= numberOfClients; i++) {
            BenchmarkWorker benchmarkWorker = new BenchmarkWorker(serverIP, serverPort, path + "client_" + i + "/");
            workers.add(benchmarkWorker);
            Thread benchThread = new Thread(benchmarkWorker);
            benchPool.execute(benchThread);

        }
        try {
            benchPool.shutdown();
            benchPool.awaitTermination(1200, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (BenchmarkWorker worker: workers) {
            combinedBytes += worker.totalBytes;
        }
        double wallTime = System.currentTimeMillis() - startTime;
        System.out.printf("Completed indexing %s bytes of data\n",combinedBytes);
        System.out.printf("Completed indexing in %.2f seconds\n",  wallTime/1000);
        System.out.printf("Indexing Speed: %.2f MB/s\n",Double.parseDouble(String.valueOf(combinedBytes))/wallTime / 1000);

        System.out.println("\nSEARCH TESTING\n");
        workers.getFirst().search("apple");
        workers.getFirst().search("there AND their");
        // TO-DO extract the arguments from args
        // TO-DO measure the execution start time
        // TO-DO create and start benchmark worker threads equal to the number of clients
        // TO-DO join the benchmark worker threads
        // TO-DO measure the execution stop time and print the performance
        // TO-DO run search queries on the first client (benchmark worker thread number 1)
        // TO-DO disconnect all clients (all benchmakr worker threads)
    }
}
