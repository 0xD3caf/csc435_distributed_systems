package csc435.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class BenchmarkWorker implements Runnable {
    ClientProcessingEngine engine;
    String serverIp;
    String serverPort;
    String datasetPath;
    IndexResult result;


    BenchmarkWorker (String host, String port, String path) {
        serverIp = host;
        serverPort = port;
        datasetPath = path;
    }


    @Override
    public void run() {
        engine = new ClientProcessingEngine();
        engine.connect(serverIp, serverPort);
        try {
            result = engine.indexFolder(datasetPath);
        } catch (IOException e) {
            System.out.println("Error indexing folder: " + e.getMessage());
        }
    }

    public void search(String query) {
        ArrayList<String> terms = new ArrayList<>(Arrays.asList(query.split(" AND ")));
        SearchResult result = engine.search(terms);
        System.out.println("\nSEARCH RESULT: " + query);
        System.out.printf("Search completed in %.2f seconds\n", result.excutionTime/1000);
        System.out.printf("Search results (top 10 out of %s)\n", result.documentFrequencies.size());
        for (DocPathFreqPair pair: result.documentFrequencies) {
            System.out.printf("* Client %24s: %s\n", pair.documentPath, pair.wordFrequency);
        }
    }

    public void disconnect() {
        engine.disconnect();
    }
}

public class FileRetrievalBenchmark {
    public static void main(String[] args)
    {
        double startTime =System.currentTimeMillis();
        int numberOfClients = Integer.parseInt(args[0]);
        String serverIP = args[1];
        String serverPort = args[2];
        String datasetPath = args[3] + "/";
        ArrayList<BenchmarkWorker> workers = new ArrayList<BenchmarkWorker>();
        ExecutorService benchPool = Executors.newFixedThreadPool(numberOfClients);
        System.out.println("Starting benchmark...");
        System.out.println("Connecting Clients");
        for (int i = 1; i <= numberOfClients; i++) {
            BenchmarkWorker benchmarkWorker = new BenchmarkWorker(serverIP, serverPort, datasetPath + "client_" + i + "/");
            workers.add(benchmarkWorker);
            Thread thread = new Thread(benchmarkWorker);
            benchPool.execute(thread);
        }
        System.out.println("Indexing Files");
        try {
            benchPool.shutdown();
            benchPool.awaitTermination(1200, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Benchmark interrupted");
        }
        long combinedBytes = 0;
        for (BenchmarkWorker worker: workers) {
            combinedBytes += worker.result.totalBytesRead;
        }
        double wallTime = System.currentTimeMillis() - startTime;
        System.out.printf("Completed indexing %s bytes of data\n",combinedBytes);
        System.out.printf("Completed indexing in %.2f seconds\n",  wallTime/1000);
        System.out.printf("Indexing Speed: %.2f MB/s\n",Double.parseDouble(String.valueOf(combinedBytes))/wallTime / 1000);

        System.out.println("\nSEARCH TESTING");
        workers.getFirst().search("apple");
        workers.getFirst().search("there AND their");
        workers.getFirst().search("meet AND tell AND show");
        for (BenchmarkWorker worker: workers) {
            worker.disconnect();
        }
    }
}