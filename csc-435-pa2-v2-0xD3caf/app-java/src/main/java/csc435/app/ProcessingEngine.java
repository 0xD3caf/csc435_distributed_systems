package csc435.app;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
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

    public ProcessingEngine(IndexStore store) {
        this.store = store;
    }

    public IndexResult indexFolder(String folderPath) throws IOException {
        IndexResult result = new IndexResult(0.0, 0);
        //start timer
        long indexStartTime = System.currentTimeMillis();
        //long indexStartTimeNano = System.nanoTime();
        // uses walk to traverse tree with custom visitFile for processing
        Files.walkFileTree(Paths.get(folderPath), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (!Files.isDirectory(file)) {
                    HashMap<String, Long> uniqueWords = new HashMap<String, Long>();
                    // add document to index store
                    long docID = store.putDocument(file.normalize().toString());

                    //!TODO need some way to remove double dash without removing single dash from pattern

                    //create regex search for valid words
                    String pattern = "\\b[a-zA-Z0-9_-]{4,}\\b";
                    Pattern compiledPattern = Pattern.compile(pattern);
                    byte[] chunk = null;
                    //create new reader to collect lines from documents
                    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file.toFile()), Charset.forName("UTF-8")));
                    String line = "";
                    // loop through all lines in doc
                    while ((line = in.readLine()) != null) {
                        //collect the bytes and access the length then add to bytes read
                        result.totalBytesRead += line.getBytes(Charset.forName("UTF-8")).length;
                        Matcher matcher = compiledPattern.matcher(line.toLowerCase());
                        // while matches are still found, increment frequency of matched word
                        while (matcher.find()) {
                            uniqueWords.putIfAbsent(matcher.group(), 0L);
                            uniqueWords.put(matcher.group(), uniqueWords.get(matcher.group()) + 1);
                        }
                    }
                    in.close();
                    store.updateIndex(docID, uniqueWords);
                    uniqueWords.clear();
                }
                //continue on to the next file
                return FileVisitResult.CONTINUE;
            }
        });
//        long executionTimeNano = System.nanoTime() - indexStartTimeNano;
//        System.out.println("Nano execution time: " + executionTimeNano);
        result.executionTime = System.currentTimeMillis() - indexStartTime;
        return result;
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
}
