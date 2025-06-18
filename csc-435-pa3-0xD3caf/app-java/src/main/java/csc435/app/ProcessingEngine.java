package csc435.app;

import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
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

    public synchronized void incrementTotalBytes (int b) {
        totalBytesRead += b;
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
    public double executionTime;
    public ArrayList<DocPathFreqPair> documentFrequencies;

    public SearchResult(double executionTime, ArrayList<DocPathFreqPair> documentFrequencies) {
        this.executionTime = executionTime;
        this.documentFrequencies = documentFrequencies;
    }
}


public class ProcessingEngine {
    // keep a reference to the index store
    private IndexStore store;
    private final int MAX_THREADS;
    static AtomicInteger bytesRead = new AtomicInteger();



    public ProcessingEngine(IndexStore store, int numWorkerThreads) {
        this.store = store;
        MAX_THREADS = numWorkerThreads;
    }

    public IndexResult indexFiles(String folderPath) throws IOException {
        IndexResult result = new IndexResult(0.0, 0);
        //start timer
        indexFileTask.bytesRead = new AtomicInteger();
        long indexStartTime = System.currentTimeMillis();
        //long indexStartTimeNano = System.nanoTime();
        // uses walk to traverse tree with custom visitFile for processing
        ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS);
        Files.walkFileTree(Paths.get(folderPath), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (!Files.isDirectory(file)) {
                    pool.execute(new indexFileTask(file));
                }

                return FileVisitResult.CONTINUE;
            }
        });
        pool.close();
        result.totalBytesRead = Long.parseLong(indexFileTask.bytesRead.toString());

//        long executionTimeNano = System.nanoTime() - indexStartTimeNano;
//        System.out.println("Nano execution time: " + executionTimeNano);
        result.executionTime = System.currentTimeMillis() - indexStartTime;
        return result;
    }
    class indexFileTask implements Runnable {
        // TODO rewrite bytes read
        // Currently it will combine bytes from multiple indexes into the same counter
        private Path file;
        static AtomicInteger bytesRead = new AtomicInteger();
        static Pattern compiledPattern = Pattern.compile("\\b[a-zA-Z0-9_-]{4,}\\b");
        HashMap<String, Long> uniqueWords = new HashMap<String, Long>();

        public indexFileTask (Path s) {
            file = s;
        }

        @Override
        public void run() {
            long id = store.putDocument(file.normalize().toString());
            String line = "";
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file.toFile()), Charset.forName("UTF-8")));
                while ((line = in.readLine()) != null) {
                    Matcher matcher = compiledPattern.matcher(line.toLowerCase());
                    bytesRead.getAndAdd(line.getBytes(Charset.forName("UTF-8")).length);
                    while (matcher.find()) {
                        uniqueWords.putIfAbsent(matcher.group(), 0L);
                        uniqueWords.put(matcher.group(), uniqueWords.get(matcher.group()) + 1);
                    }
                }
                in.close();
                store.updateIndex(id, uniqueWords);
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public SearchResult search(ArrayList<String> terms) {
        long searchStartTime = System.currentTimeMillis();
        //long searchStartTimeNano = System.nanoTime();
        SearchResult result = new SearchResult(0.0, new ArrayList<DocPathFreqPair>());
        // new hashmap to store pairs of ids and array of frequency values
        HashMap<Long, ArrayList<Long>> pairsIntersection = new HashMap<Long, ArrayList<Long>>();

        for (String term : terms) {
            for (DocFreqPair p: store.lookupIndex(term)) {
                pairsIntersection.putIfAbsent(p.documentNumber, new ArrayList<Long>());
                pairsIntersection.get(p.documentNumber).add(p.wordFrequency);
            }

        }
        // iterate keyset and check if values length matches number of terms
        // if yes the file contains all requested words and we can sum them
        for (long id: pairsIntersection.keySet()) {
            if (pairsIntersection.get(id).size() == terms.size()) {
                long sum = 0L;
                for (Long l: pairsIntersection.get(id)) {
                    sum += l;
                }
                result.documentFrequencies.add(new DocPathFreqPair(store.getDocument(id), sum ));
            }

        }
        // sort by frequency
//        long executionTimeNano = System.nanoTime() - searchStartTimeNano;
//        System.out.println("Nano execution time: " + executionTimeNano);
        result.documentFrequencies.sort(DocPathFreqPair::compareTo);
        result.executionTime = System.currentTimeMillis() - searchStartTime;
        return result;
    }

    public int getMAX_THREADS() {
        return MAX_THREADS;
    }
}


