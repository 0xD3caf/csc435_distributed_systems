package csc435.app;

import java.io.IOException;
import java.lang.System;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class AppInterface {
    // keep a reference to the processing engine
    private ProcessingEngine engine;

    public AppInterface(ProcessingEngine engine) {
        this.engine = engine;
    }

    public void readCommands() throws IOException {
        // TO-DO implement the read commands method
        Scanner sc = new Scanner(System.in);
        String command;
        System.out.println("Running with " + engine.getMAX_THREADS() + " Threads");
        System.out.println("<index | search | quit>\n");

        while (true) {
            System.out.print("> ");

            // read from command line
            command = sc.nextLine();
            // if the command is quit, terminate the program
            if (command.compareTo("quit") == 0) {
                break;
            }

            // if the command begins with index, index the files from the specified directory
            if (command.length() >= 5 && command.substring(0, 5).compareTo("index") == 0) {
                // TO-DO parse command and call indexFolder on the processing engine
                String folderPath = command.substring(6);
                try {
//                    IndexResult indexResult = engine.indexFiles(folderPath);
                    IndexResult indexResult = engine.indexFiles(folderPath);
                    System.out.printf("Completed indexing %s bytes of data\n", indexResult.totalBytesRead);
                    System.out.printf("Completed indexing in %.2f seconds\n",  indexResult.executionTime/1000);
                    System.out.printf("Indexing Speed: %.2f MB/s\n",(indexResult.totalBytesRead / indexResult.executionTime) / 1000);
                } catch (IOException e) {
                    System.out.println("This was not a valid folder");
                }
                continue;
            }

            // if the command begins with search, search for files that matches the query
            if (command.length() >= 6 && command.substring(0, 6).compareTo("search") == 0) {
                // TO-DO parse command and call search on the processing engine
                ArrayList<String> terms = new ArrayList<String>(Arrays.asList(command.substring(7).split(" AND ")));
                // Check that query has valid number of search terms
                if (terms.isEmpty() || terms.size() > 3 ) {
                    System.out.println("This search has an incorrect number of terms (Limit 3)");
                    continue;
                }
                boolean correctTermLength = true;
                for (String term: terms) {
                    if (term.length() < 4) {
                        correctTermLength = false;
                        break;
                    }
                }
                if (!correctTermLength){
                    System.out.println("This search has an incorrect term length (Min 4 letters)");
                    continue;
                }
                SearchResult searchResult = engine.search(terms);
                int totalResults = searchResult.documentFrequencies.size();
                // if list is longer than 10, truncate to top 10
                if (searchResult.documentFrequencies.size() > 10) {
                    searchResult.documentFrequencies = new ArrayList<DocPathFreqPair>(searchResult.documentFrequencies.subList(0,10));
                }
                System.out.printf("Search completed in %.2f seconds\n", searchResult.executionTime/1000);
                System.out.printf("Search results (top %s out of %s)\n", searchResult.documentFrequencies.size(), totalResults);
                for (DocPathFreqPair pair: searchResult.documentFrequencies) {
                    System.out.printf("* %24s: %s\n", pair.documentPath, pair.wordFrequency);
                }
                continue;
            }

            System.out.println("unrecognized command!");
        }
        sc.close();
    }
}
