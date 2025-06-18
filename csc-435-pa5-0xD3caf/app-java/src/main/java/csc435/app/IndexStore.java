package csc435.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Data structure that stores a document number and the number of time a word/term appears in the document
class DocFreqPair implements Comparable<DocFreqPair> {
    public long documentNumber;
    public long wordFrequency;

    public DocFreqPair(long documentNumber, long wordFrequency) {
        this.documentNumber = documentNumber;
        this.wordFrequency = wordFrequency;
    }

    //custom sort method, compares by ID number
    @Override
    public int compareTo(DocFreqPair other) {
        return Long.compare(this.documentNumber, other.documentNumber);
    }
}

public class IndexStore {
    private static long nextDocumentId = 1;
    ConcurrentHashMap<String, ArrayList<DocFreqPair>> termInvertedIndex;
    ConcurrentHashMap<Long,String> documentMap;

    public IndexStore() {
        documentMap = new ConcurrentHashMap<Long, String>();
        termInvertedIndex = new ConcurrentHashMap<String, ArrayList<DocFreqPair>>();
    }
    public synchronized long putDocument(String documentPath) {
        long documentNumber;
        //DocumentMap already contains this item
        if (documentMap.containsValue(documentPath)) {
            for (long k: documentMap.keySet()) {
                if (documentMap.get(k).equals(documentPath)) return k;
            }
        }
        //If not found, set new Id and increment Id counter
        documentNumber = nextDocumentId;
        nextDocumentId ++;
        documentMap.put(documentNumber, documentPath);
        return documentNumber;
    }

    public String getDocument(long documentNumber) {
        //returns docString if found, string empty if not
        return documentMap.getOrDefault(documentNumber, "");
    }

    public void updateIndex(long documentNumber, Map<String, Long> wordFrequencies) {
        //iterate through new words
        for (String word: wordFrequencies.keySet()) {
            synchronized (this) {
                termInvertedIndex.putIfAbsent(word.toLowerCase(), new ArrayList<DocFreqPair>());
                termInvertedIndex.get(word.toLowerCase()).add(new DocFreqPair(documentNumber, wordFrequencies.get(word.toLowerCase())));
            }
        }
    }

    public ArrayList<DocFreqPair> lookupIndex(String term) {
        if (termInvertedIndex.containsKey(term.toLowerCase())) {
            return new ArrayList<>(termInvertedIndex.get(term.toLowerCase()));
        } else {
            return new ArrayList<>();
        }
    }
}